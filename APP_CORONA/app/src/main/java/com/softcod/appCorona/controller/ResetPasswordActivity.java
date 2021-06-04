package com.softcod.appCorona.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.softcod.appCorona.R;
import com.softcod.appCorona.utils.RetrieveHttp;
import com.softcod.appCorona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private Button home;
    private Button entrarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailEditText = (EditText) findViewById(R.id.reset_emailEditText);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        entrarButton = (Button) findViewById(R.id.connection_enviar);

        home = findViewById(R.id.voltar);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordActivity.this,
                        LoginActivity.class);
                startActivity(intent);

            }
        });


        entrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrarButtonOnClick();
            }
        });
    }

    public void entrarButtonOnClick() {

        String email = emailEditText.getText().toString();

        String message = "";

        if (Utils.isValidEmail(email)) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.authenticating);
            progressDialog.setMessage(getString(R.string.pleaseWait));
            progressDialog.setCancelable(true);
            progressDialog.setIndeterminate(true);
            progressDialog.show();

            RetrieveHttp http = new RetrieveHttp();
            JSONObject json = null;
            try {
                json = http.execute(getString(R.string.servidor)+
                                "/recuperar_senha",
                        "POST", "email=" + email).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }

            try {
                message = (String) json.get("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(this, "" + getString(R.string.invalidEmailText), Toast.LENGTH_LONG).show();

        }

    }
}