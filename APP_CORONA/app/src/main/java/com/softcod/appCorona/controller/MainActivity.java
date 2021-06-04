package com.softcod.appCorona.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.softcod.appCorona.R;
import com.softcod.appCorona.model.MaqNotificationInterrupcao;
import com.softcod.appCorona.utils.Mensagens;
import com.softcod.appCorona.utils.MyFirebaseMessagingService;
import com.softcod.appCorona.utils.RetrieveHttp;
import com.softcod.appCorona.utils.Utils;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView maqRecyclerView;
    private NotificationAdapter adapter;
    private Handler handler;
    Intent pauseInte;

    SharedPreferences prefs = null;
    private static final int PERMISSION_WRITE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniciar();
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             refreshNotifications();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void iniciar() {
        setContentView(com.softcod.appCorona.R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        prefs = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setLogo(R.drawable.ic_icone_branco);
        toolbar.setTitle("MTBF");
        toolbar.setSubtitle("APP");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)
                findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent comecarCadastraMaquinas=
                new Intent(this, CadMACActivity.class);
        FloatingActionButton addMaquina = findViewById(R.id.addMaquina);
        addMaquina.setOnClickListener(view -> startActivity(comecarCadastraMaquinas));
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    JSONObject json = null;
    @Override
    protected void onResume() {
        super.onResume();

        RetrieveHttp http = new RetrieveHttp();
        RetrieveHttp http2 = new RetrieveHttp();
        registerReceiver(messageReceiver, new IntentFilter("notification"));

        prefs.registerOnSharedPreferenceChangeListener(this);

         //nlogado = não esta logado
        if(prefs.getBoolean("nlogado", true)){

            Intent intent = new Intent(MainActivity.this,
                    LoginActivity.class);
            this.startActivity(intent);
        }else {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_WRITE);
            }
            else {

                try {
                    json = http.execute(
                            getString(R.string.servidor)
                                    + "/get_maquinas"
                            , "POST",
                            "email=" +prefs.getString(getString(R.string.keyEmail), "")

                    ).get();
                    int status = json.getInt("status");
                    //String mensagem = json.getString("message");

                    //Log.d("JSON\n\n\n\n", "" +  json);
                    if(status == 2){

                        AlertDialog.Builder dialogo =
                                new AlertDialog.Builder(this);
                        Intent comecarCadastraMaquinas=
                                new Intent(this, CadMACActivity.class);
                        dialogo.create();
                        dialogo.setTitle(R.string.tituloInfo);
                        dialogo.setMessage(R.string.orientacaoCadastroMac);
                        dialogo.setPositiveButton(R.string.ok, (dialogInterface, i) ->
                                startActivity(comecarCadastraMaquinas));
                        dialogo.show();
                    }

                } catch (ExecutionException | InterruptedException | JSONException e) {
                    e.printStackTrace();
                }

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Get new Instance ID token
                                String fcm_id = Objects.requireNonNull(task.getResult()).getToken();

                                try {
                                    http2.execute(
                                            getString(R.string.servidor)
                                                    + "/dispositivos"
                                            , "POST",
                                            "email=" + prefs.getString(getString(R.string.keyEmail), "") +
                                                    "&fcm_id=" + fcm_id
                                    ).get();
                                } catch (ExecutionException | InterruptedException e) {
                                    Log.e("DISPOSITIVOS", Objects.requireNonNull(e.getMessage()));
                                }
                            }
                        });
                refreshNotifications();
            }
            doAutoRefresh();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_editaUsario:
                Intent editUsuario = new Intent(this, EditarUsuarioActivity.class);
                startActivity(editUsuario);
            break;
            case R.id.nav_maqs:
                Intent maquinas = new Intent(this, MaqActivity.class);
                 startActivity(maquinas);
                break;

           /* case R.id.nav_settings:
                Intent settingsIntent = new Intent(this, ConfigActivity.class);
                 startActivity(settingsIntent);
                break;*/
            case R.id.nav_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;

            case R.id.nav_logout:
                prefs.edit().putBoolean("nlogado", true).apply();
                prefs.edit().putString(getString(R.string.keyEmail), "").apply();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;

            default:
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        //System.out.println("HERE");
        if (key.equals(getString(R.string.notificacaoMaquinas))) {
                   refreshNotifications();
        }
    }

       public void doAutoRefresh() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshNotifications();
                handler.postDelayed(this, 30 * 1000);
            }
        }, 30 * 1000);
    }

      private RecyclerView getMaqsRecyclerView() {
        if(maqRecyclerView == null) {
            maqRecyclerView = (RecyclerView) findViewById(R.id.relacaoMaquinas);
        }
        return maqRecyclerView;
    }

      public void refreshNotifications() {
        List<MaqNotificationInterrupcao> notificationsList =
                MyFirebaseMessagingService.getNotifFromSerialized(
                        prefs.getString(getString
                                (R.string.notificacaoMaquinas), null));
        Collections.reverse(notificationsList);

        adapter = new NotificationAdapter(this, notificationsList);
        getMaqsRecyclerView().setLayoutManager(
                new LinearLayoutManager(getApplicationContext())
        );
          getMaqsRecyclerView().setAdapter(adapter);

        TextView noNotificationsTextView = (TextView)
                findViewById(R.id.main_noNotificationsTextView);
        noNotificationsTextView.setVisibility(notificationsList.size() > 0 ?
                View.INVISIBLE : View.VISIBLE);
    }

      @Override
      protected void onPause() {
          super.onPause();      }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            refreshNotifications();

            return true;

        } else if (id == R.id.action_cleaner) {

            Utils.showOkCancelDialog(this,
                    R.string.clearHistory,
                    R.string.clearHistoryDescription,
                    R.string.clearHistoryButton,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    prefs.edit().putString(getString(R.string.notificacaoMaquinas), null).apply();
                    refreshNotifications();

                }
            });

            return true;

        } else if (id == R.id.action_email) {

            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PERMISSION_GRANTED) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String destino = preferences.getString(getString(R.string.keyEmail), "");

              // writeToSDFile(stringHistorico());

                String filename = "/download/historico.txt";
                File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
                Uri path = Uri.fromFile(filelocation);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{destino});
                intent.putExtra(Intent.EXTRA_STREAM, path);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.historico));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.anexo));
                try {
                    startActivity(Intent.createChooser(intent, getString(R.string.tituloEmail)));
                } catch (android.content.ActivityNotFoundException ex) {
                    //Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    Utils.showOkDialog(this, R.string.couldNotSendEmail, R.string.couldNotSendEmailDescription);
                }
            } else {
                Mensagens.okDialog(
                        MainActivity.this,
                        getString(R.string.semPermissao),
                        getString(R.string.necessidade) +
                        getString(R.string.precisamosPremissao),
                        getString(R.string.ok)
                );
            }
            return true;
        }
       /*  else if (id == R.id.action_filter) {

            Intent intent = new Intent(MainActivity.this, FilterActivity.class);
            startActivity(intent);

            return true;

        }*/

        return super.onOptionsItemSelected(item);
    }


/**
 * Retorna a listview que mostra as campainhas recebidas
 *
 * @return
 */

   public String stringHistorico() {

        String historico = "Historico do Alerta: ";

        List<MaqNotificationInterrupcao> notificationsList =
                MyFirebaseMessagingService.getNotifFromSerialized(prefs.getString("maqNotifications", null));
        Collections.reverse(notificationsList);

        for (MaqNotificationInterrupcao maqNotificationInterrupcao : notificationsList) {

            historico += "\n" + maqNotificationInterrupcao.id;
            historico += ";" + maqNotificationInterrupcao.getDate();

        }

        return historico;
    }

    private static class NotificationAdapter extends
            RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

        private Context context;
        private List<MaqNotificationInterrupcao> notificationsList;

        NotificationAdapter(Context context,
                            List<MaqNotificationInterrupcao> notificationsList) {
            this.context = context;
            this.notificationsList = notificationsList;
        }


        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notification_item, parent, false);
            itemView.setId(notificationsList.hashCode());
            return new NotificationViewHolder(itemView);

        }
        int corUno;
        //RetrieveHttp pegarMaquinas;
        public View addView(int cor, int texto, NotificationViewHolder holder){
            CardView card = new CardView(context);
            LinearLayout ll = new LinearLayout(context);

            card.addView(ll);
            card.setRadius(90);
            card.setId(cor);

            ImageView img = new ImageView(context);
            img.setColorFilter(cor);
            img.setImageResource(R.drawable.ic_info_outline_black_24dp);

            TextView text = new TextView(context);
            text.setTextSize(15);
            text.setText(texto);

            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.addView(img);
            ll.addView(text);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.sinaleira.setColorFilter(cor);
                    corUno = cor;
                }
            });
            return card;
        }
        public void setNotificao(NotificationViewHolder holder, Context context){
            AlertDialog.Builder dialogo = new AlertDialog.Builder(context);
            EditText editText = new EditText(context);

            holder.itemView.setOnClickListener(view -> {
                dialogo.create();
                dialogo.setTitle(dialogo.getContext().getString(R.string.tituloInfo));
                dialogo.setMessage(
                        dialogo.getContext().getText(R.string.motivoParada)
                );
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                editText.setAllCaps(true);
                layout.setVerticalGravity(LinearLayout.VERTICAL);

                layout.addView(editText);
                layout.addView(addView(Color.GREEN, R.string.verde, holder));
                layout.addView(addView(Color.YELLOW, R.string.amarelo, holder));
                layout.addView(addView(Color.RED, R.string.vermelho, holder));

                dialogo.setView(layout);

                dialogo.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Snackbar.make(view, dialogo.getContext().getText(R.string.noAlteracao), Snackbar.LENGTH_LONG).show();
                        holder.sinaleira.clearColorFilter();
                    }
                });

                dialogo.setNegativeButton(dialogo.getContext().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Snackbar.make(view, dialogo.getContext().getText(R.string.noAlteracao), Snackbar.LENGTH_LONG).show();
                        holder.sinaleira.clearColorFilter();
                    }
                });

                dialogo.setPositiveButton(dialogo.getContext().getText(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(editText.getText().toString().isEmpty() && corUno == Color.RED){
                            editText.setError(dialogo.getContext().getText(R.string.motivo));

                            AlertDialog.Builder alert = new AlertDialog.Builder(dialogo.getContext());
                            alert.create();
                            alert.setMessage(dialogo.getContext().getString(R.string.obrigatorio));
                            alert.setNegativeButton(dialogo.getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    alert.setCancelable(true);
                                   holder.sinaleira.clearColorFilter();
                                   holder.motivoTextView.clearComposingText();

                                }
                            });

                            alert.show();
                        }
                        else {
                            holder.motivoTextView.setText(
                                    editText.getText().toString()
                            );
                        }
                    }
                });
                dialogo.show();
            });
        }
        @Override
        public void onBindViewHolder(NotificationViewHolder holder, int position) {
            MaqNotificationInterrupcao maqNotificationInterrupcao = notificationsList.get(position);

            holder.motivoTextView.setText(maqNotificationInterrupcao.type);

            /////
            setNotificao(holder, this.context);
            ////
            holder.maquinaTextView.setText(maqNotificationInterrupcao.description);
            holder.timeTextView.setText(maqNotificationInterrupcao.getDate());

        }
        @Override
        public int getItemCount() {
            return notificationsList.size();
        }

       static class NotificationViewHolder extends RecyclerView.ViewHolder {
           private ImageView sinaleira;
           private TextView motivoTextView;
           private TextView maquinaTextView;
           private TextView timeTextView;
           private int  id;
            @SuppressLint("ResourceAsColor")
            NotificationViewHolder(View itemView) {
                super(itemView);
                id = itemView.getId();
                sinaleira = (ImageView) itemView.findViewById(R.id.notification_imageView);
                maquinaTextView = (TextView) itemView.findViewById(R.id.maquinaParada);
                motivoTextView = (TextView) itemView.findViewById(R.id.motivoParada);
                timeTextView = (TextView) itemView.findViewById(R.id.horarioParada);

            }
        }
    }
}

  /*  private void writeToSDFile(String data) {
        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = android.os.Environment.getExternalStorageDirectory();

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File(root.getAbsolutePath() + "/download");
        dir.mkdirs();
        File file = new File(dir, "historico.txt");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(data);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
