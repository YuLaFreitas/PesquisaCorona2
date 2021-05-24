package com.softcod.pesquisacorona;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.webkit.WebView;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.softcod.pesquisacorona.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        soliciatLogin();
    }

    public void soliciatLogin(){

        setContentView(R.layout.activity_login);

       // abrirSecao();
    }

    public void abrirSecao(View v){
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery,
                R.id.nav_slideshow, R.id.nav_sair)
                .setOpenableLayout(drawer)
                .build();
        NavController navController =
                Navigation.findNavController(this,
                        R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this,
                navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(
                navigationView, navController);
    }

    public void cadastrar(View v){
     setContentView(R.layout.activity_cadastrar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //webView.clearCache(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        Intent cadastro = new Intent(this, CadastrarUsuarioActivity.class);
        startActivity(cadastro);

    }
}