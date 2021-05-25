package com.softcod.pesquisacorona.controller;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.softcod.pesquisacorona.R;
import com.softcod.pesquisacorona.utils.MyFirebaseMessagingService;
import com.softcod.pesquisacorona.utils.RetrieveHttp;
import com.softcod.pesquisacorona.utils.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static boolean activityVisible = false;

    private RecyclerView bellsRecyclerView;
   // private NotificationAdapter adapter;
    private Handler handler;

    SharedPreferences prefs = null;

    private static final int PERMISSION_WRITE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(com.centralsigma.campainhaweb2.R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_launcher_foreground);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        setSupportActionBar(toolbar);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void doAutoRefresh(){
        handler = new Handler();
        handler.postDelayed( new Runnable() {

            @Override
            public void run() {
                refreshNotifications();
                handler.postDelayed( this, 30 * 1000 );
            }
        }, 30 * 1000 );
    }


    public static boolean isActivityVisible() {
        return activityVisible;
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshNotifications();

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume");
        registerReceiver(messageReceiver, new IntentFilter("notification"));
        prefs.registerOnSharedPreferenceChangeListener(this);

        activityVisible = true;

        if(prefs.getBoolean("nlogado", true)){
            Log.d("MainActivity VERDADE>>>", "onResume ????" +prefs.getAll());
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            this.startActivity(intent);
        }else{

            if(ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_WRITE);

            }else{
               /* FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (task.isSuccessful()) {
                                    // Get new Instance ID token
                                    //String fcm_id = task.getResult().getToken();
                                    RetrieveHttp http = new RetrieveHttp();
                                    JSONObject json = null;

                                    try {
                                        json = http.execute("http://campainhaweb.com.br/api2/dispositivos",
                                                "POST", "email="+prefs.getString("email", "")+"&fcm_id="+fcm_id).get();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("MainActivity VERDADE>>>", "onResume ????" +prefs.getAll());
                                    //System.out.println(json);

                                }
                            }
                        });
           */ }

            refreshNotifications();
        }

        //doAutoRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause");
        unregisterReceiver(messageReceiver);
        activityVisible = false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_filter);
        Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_filter_list_black_24dp);
        icon.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.white), PorterDuff.Mode.SRC_IN);
        item.setIcon(icon);

        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
     /*
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            refreshNotifications();

            return true;

        }else if (id == R.id.action_cleaner) {

            Utils.showOkCancelDialog(this, R.string.clearHistory, R.string.clearHistoryDescription, R.string.clearHistoryButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                prefs.edit().putString("bellNotifications", null).apply();
                refreshNotifications();

                }
            });

            return true;

        } else if (id == R.id.action_email) {

            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permissionCheck == PERMISSION_GRANTED) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String destino = preferences.getString("email", "");

                writeToSDFile(stringHistorico());

                String filename = "/download/historico.txt";
                File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
                Uri path = Uri.fromFile(filelocation);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{destino});
                intent.putExtra(Intent.EXTRA_STREAM, path);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Histórico da Campainha Web");
                intent.putExtra(Intent.EXTRA_TEXT, "Segue em anexo o histórico de sua campainha.");
                try {
                    startActivity(Intent.createChooser(intent, "Preparado para enviar e-mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    //Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    Utils.showOkDialog(this, R.string.couldNotSendEmail, R.string.couldNotSendEmailDescription);
                }
            }else{
                Mensagens.okDialog(MainActivity.this, "Permissão não concedida!", "Para exportar o histórico por e-mail a Campainha Web requer a permissão para acessar o armazenamento de seu dispositivo.",
                        "Ok");
            }
            return true;
        } /*else if (id == R.id.action_filter) {

            Intent intent = new Intent(MainActivity.this, FilterActivity.class);
            startActivity(intent);

            return true;

        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

      /*  switch (id) {

            case R.id.nav_bells:

                Intent bellsIntent = new Intent(MainActivity.this, BellsActivity.class);
                startActivity(bellsIntent);
                break;

            case R.id.nav_settings:
                Intent settingsIntent = new Intent(MainActivity.this, ConfigActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.nav_about:

                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                break;

            case R.id.nav_logout:

                prefs.edit().putBoolean("nlogado", true).apply();
                prefs.edit().putString("email", "").apply();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                break;

            default:
                break;

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Retorna a listview que mostra as campainhas recebidas
     *
     * @return
     */
    private RecyclerView getBellsRecyclerView() {
        if(bellsRecyclerView == null) {
            //bellsRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);
        }
        return bellsRecyclerView;
    }

    public void refreshNotifications() {
        /*
        List<BellNotification> notificationsList = MyFirebaseMessagingService.getNotifFromSerialized(prefs.getString("bellNotifications", null));
        Collections.reverse(notificationsList);

        adapter = new NotificationAdapter(this, notificationsList);
        getBellsRecyclerView().setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        getBellsRecyclerView().setAdapter(adapter);

        TextView noNotificationsTextView = (TextView) findViewById(R.id.main_noNotificationsTextView);
        noNotificationsTextView.setVisibility(notificationsList.size() > 0 ? View.INVISIBLE : View.VISIBLE);
        */
    }

    private void writeToSDFile(String data){
        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = Environment.getExternalStorageDirectory();

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File (root.getAbsolutePath() + "/download");
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

    public String stringHistorico () {
/*
        String historico = "Historico da Campainha Web: ";

        List<BellNotification> notificationsList = MyFirebaseMessagingService.getNotifFromSerialized(prefs.getString("bellNotifications", null));
        Collections.reverse(notificationsList);

        for(BellNotification bellNotification : notificationsList){

            historico += "\n" + bellNotification.id;
            historico += ";" + bellNotification.getDate();

        }*/

        return null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        System.out.println("HERE");
        if(key.equals("bellNotifications")){
            refreshNotifications();
        }
    }

   /* private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

        private Context context;
       // private List<BellNotification> notificationsList;

        NotificationAdapter(Context context, List<BellNotification> notificationsList) {
            this.context = context;
            this.notificationsList = notificationsList;
        }

        @Override
        public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);

            return new NotificationViewHolder(itemView);

        }

        @Override
        public void onBindViewHolder(NotificationViewHolder holder, int position) {

            BellNotification bellNotification = notificationsList.get(position);

            holder.labelTextView.setText(bellNotification.id);

            holder.timeTextView.setText(bellNotification.getDate());

        }

        @Override
        public int getItemCount() {
            return notificationsList.size();
        }

        class NotificationViewHolder extends RecyclerView.ViewHolder {

            private ImageView notificationImageView;
            private TextView labelTextView;
            private TextView timeTextView;

            NotificationViewHolder(View itemView) {
                super(itemView);

                notificationImageView = (ImageView) itemView.findViewById(R.id.notification_imageView);
                labelTextView = (TextView) itemView.findViewById(R.id.notification_labelTextView);
                timeTextView = (TextView) itemView.findViewById(R.id.notification_timeTextView);

            }
        }

    }*/

}
