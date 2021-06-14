package com.softcod.pesquisacorona;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.softcod.pesquisacorona.controller.ConfigActivity;
import com.softcod.pesquisacorona.databinding.ActivityMainBinding;
import com.softcod.pesquisacorona.ui.home.HomeViewModel;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    private Location location;
    private LocationManager locationManager;
    //Intent intent;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    HomeViewModel hvm;
    double latitude = 0.0, longitude = 0.0;

    SharedPreferences preferences;
    SharedPreferences start = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hvm = new HomeViewModel();

        start = getSharedPreferences("execultado", MODE_PRIVATE);
        //intent = new Intent(this, HomeViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        DrawerLayout drawer = binding.drawerLayout;

        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_informacao, R.id.nav_pesquisa)
                .setOpenableLayout(drawer)
                .build();

        NavController navController =
                Navigation.findNavController(
                        this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(
                this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(
                navigationView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(start.getBoolean("execultado", true)){
            start.edit().putBoolean("execultado", false).apply();
        }else{

            hvm.carregarBanco();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        alerta(this);
    }

    public void alerta(Context contexto){
        setLongitude(getLatitude());
        setLatitude(getLatitude());

        String dados = null;

        try {
        dados = "No mês passado tivemos:\n"+ hvm.dadosMesPassado();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("\n\n\n\nPERMISSÃO\n\n\n\n", "Sem pemissão??");

        } else {
            locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            location =
                    locationManager
                            .getLastKnownLocation(LocationManager
                                    .GPS_PROVIDER);        }
        if(location !=null){
            setLongitude(location.getLongitude());
            setLatitude(location.getLatitude());
            //intent.putExtra("latitude", getLatitude());
            //intent.putExtra("longitude", getLongitude());
            dados += ".\n Essa pesquisa foi feita na ";
            dados += "\n latitude: " +location.getLongitude()+
                    "\ne na longitude: " + location.getLatitude();


            Log.d("\n\n\n\nlocation\n\n\n\n",
                    "Lat" + getLongitude() +
                            "lon" + getLatitude());
        }else{
            Log.e("\n\n\n\nlocation\n\n\n\n", "Erro");
        }
        dados += ".\n Hoje é: " + hvm.getDia() +"/"+ hvm.getMes() +"/"+ hvm.getAno();

        AlertDialog.Builder alerta =new AlertDialog.Builder(contexto);
        TextView textView = new TextView(contexto);
        textView.setText(dados);
        textView.setTextColor(Color.RED);
        alerta.setView(textView);
        alerta.setMessage("Podemos armazenar o histórico de pesquisa?\n");

        alerta.setPositiveButton("SIM",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hvm.registrarConsulta();
                    }
                });
        alerta.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        alerta.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        TextView textView = findViewById(R.id.cidadao);
        textView.setText(preferences.getString(
                ConfigActivity.CONT_NOME, ""));


        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController =
                Navigation.findNavController(this,
                        R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController,
                mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void cadastrar(MenuItem item) {
        Intent cadastro = new Intent(this,
                CadastrarUsuarioActivity.class);
        startActivity(cadastro);
    }

    public void sair(MenuItem item) {
        Intent home = new Intent(this, LoginActivity.class);
        startActivity(home);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


}