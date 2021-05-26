package com.softcod.pesquisacorona.controller;

import static android.bluetooth.BluetoothGattDescriptor.PERMISSION_WRITE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.softcod.pesquisacorona.R;
import com.softcod.pesquisacorona.utils.RetrieveHttp;
import com.softcod.pesquisacorona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private Button backButton;
    private Button nextButton;
    private Button entrarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        final com.softcod.pesquisacorona.MainActivity[] mn = new com.softcod.pesquisacorona.MainActivity[1];
        emailEditText = (EditText) findViewById(R.id.connection_apelido);
        passwordEditText = (EditText) findViewById(R.id.connection_passwordEditText);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        passwordEditText.setOnKeyListener(getPasswordOnKeyListener());
        passwordEditText.setTypeface(Typeface.DEFAULT);

        backButton = (Button) findViewById(R.id.connection_backButton);
        nextButton = (Button) findViewById(R.id.connection_nextButton);
        entrarButton = (Button) findViewById(R.id.connection_entrar);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonOnClick(v);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonOnClick(v);
            }
        });

        entrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                entrarButtonOnClick(v);
            }
        });

        if(ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE);
        }

    }

    public void nextButtonOnClick(View v) {

        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        this.startActivity(intent);
    }

    public void backButtonOnClick(View v) {

        Intent intent = new Intent(LoginActivity.this, CadastrarActivity.class);
        this.startActivity(intent);
    }

    public void entrarButtonOnClick(View v) {

        String email = emailEditText.getText().toString();
        String password =  passwordEditText.getText().toString();
        int status = 2;
        String message = "";

        if (Utils.isValidEmail(email)) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.prossesando);
            progressDialog.setMessage(getString(R.string.aguarde));
            progressDialog.setCancelable(true);
            progressDialog.setIndeterminate(true);
            progressDialog.show();

            RetrieveHttp http = new RetrieveHttp();
            JSONObject json = null;
            try {
                json = http.execute(getString(R.string.servidor),
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
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                this.startActivity(intent);
            }else{
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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

                    InputMethodManager imm = (InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);

                }

                return false;
            }
        };

    }


}