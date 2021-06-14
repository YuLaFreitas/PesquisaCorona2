package com.softcod.pesquisacorona;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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
import com.softcod.pesquisacorona.utils.Gps;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    Location location;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    Double latitude =0.0, longitude = 0.0;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        ) {


        }else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

            Gps gps = new Gps(this);
            longitude = gps.getLon();
            latitude = gps.getLat();



        Log.d("\n\n\n\nlocation\n\n\n\n", "Lat"+longitude + "lon" + longitude);

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

   public void sair(MenuItem item){
        Intent home = new Intent(this, LoginActivity.class);


       startActivity(home);


   }

}