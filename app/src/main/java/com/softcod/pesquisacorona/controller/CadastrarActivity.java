package com.softcod.pesquisacorona.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.softcod.pesquisacorona.R;
import com.softcod.pesquisacorona.utils.RetrieveHttp;
import com.softcod.pesquisacorona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class CadastrarActivity extends AppCompatActivity {


    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfEditText;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private Button backButton;
    private Button entrarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cadastrar);

        emailEditText = (EditText) findViewById(R.id.connection_apelido);
        passwordEditText = (EditText) findViewById(R.id.connection_passwordEditText);
        //passwordConfEditText = (EditText) findViewById(R.id.connection_passwordConfEditText);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        passwordEditText.setOnKeyListener(getPasswordOnKeyListener());
        passwordEditText.setTypeface(Typeface.DEFAULT);
        passwordConfEditText.setOnKeyListener(getPasswordConfOnKeyListener());
        passwordConfEditText.setTypeface(Typeface.DEFAULT);

        backButton = (Button) findViewById(R.id.connection_backButton);
        entrarButton = (Button) findViewById(R.id.connection_entrar);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonOnClick();
            }
        });


        entrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrarButtonOnClick();
            }
        });

    }

    public void backButtonOnClick() {

        Intent intent = new Intent(CadastrarActivity.this, LoginActivity.class);
        this.startActivity(intent);

    }

    public void entrarButtonOnClick() {

        String email = emailEditText.getText().toString();
        String password =  passwordEditText.getText().toString();
        String passwordConf =  passwordConfEditText.getText().toString();
        int status = 2;
        String message = "";

        if (Utils.isValidEmail(email)) {

            if(password.equals(passwordConf)){
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle(R.string.aguarde);
                progressDialog.setMessage(getString(R.string.aguarde));
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                RetrieveHttp http = new RetrieveHttp();
                JSONObject json = null;
                try {
                    json = http.execute("http://campainhaweb.om.br/api2/cadastrar",
                            "POST", "email="+email+"&senha="+password).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                }

                preferences.edit().putString(ConfigActivity.PREF_EMAIL, email).apply();
                preferences.edit().putString("email", email).apply();
                preferences.edit().putString(ConfigActivity.PREF_SENHA, password).apply();

                try {
                    status = (int) json.get("status");
                    message = (String) json.get("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(status == 1){
                    preferences.edit().putBoolean("nlogado", false).apply();
                    Intent intent = new Intent(CadastrarActivity.this, MainActivity.class);
                    this.startActivity(intent);
                }else{
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, "" + getString(R.string.esqueceuSenha), Toast.LENGTH_LONG).show();
            }

        } else {

            Toast.makeText(this, "" + getString(R.string.invalidEmailText), Toast.LENGTH_LONG).show();

        }

    }

    private EditText.OnKeyListener getPasswordOnKeyListener() {

        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == 66) {

                    InputMethodManager imm = (InputMethodManager)getSystemService(CadastrarActivity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);

                }

                return false;
            }
        };

    }

    private EditText.OnKeyListener getPasswordConfOnKeyListener() {

        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == 66) {

                    InputMethodManager imm = (InputMethodManager)getSystemService(CadastrarActivity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordConfEditText.getWindowToken(), 0);

                }

                return false;
            }
        };

    }

}