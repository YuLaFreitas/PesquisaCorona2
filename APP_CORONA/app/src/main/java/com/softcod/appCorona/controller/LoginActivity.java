package com.softcod.appCorona.controller;

import static android.bluetooth.BluetoothGattDescriptor.PERMISSION_WRITE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.softcod.appCorona.R;
import com.softcod.appCorona.utils.Mensagens;
import com.softcod.appCorona.utils.RetrieveHttp;
import com.softcod.appCorona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private SharedPreferences preferences;
    private Button entrar;
    private int status = 2;
    private  String mensagem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setContentView(R.layout.activity_login);

        if(ContextCompat.checkSelfPermission(
                        LoginActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE);
        }

        Button cadastrar = findViewById(R.id.cadastrarButton);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,
                        CadastrarUsuarioActivity.class);
                startActivity(intent);
            }
        });
        Button recuperar = findViewById(R.id.esqueceuBtn);
        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Recuperar", view.toString());
                Intent irRecuperar = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(irRecuperar);
            }
        });

        entrar = findViewById(R.id.connection_entrar);
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entrarButtonOnClick();
            }
        });

        emailEditText = (EditText) findViewById(R.id.connection_emailEditText);

        passwordEditText = (EditText) findViewById(R.id.connection_passwordEditText);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

       passwordEditText.setOnKeyListener(getPasswordOnKeyListener());
       passwordEditText.setTypeface(Typeface.DEFAULT);

       emailEditText.addTextChangedListener(TextWatcher(emailEditText.getEditableText()));
       passwordEditText.addTextChangedListener(TextWatcher(passwordEditText.getEditableText()));
    }

    public TextWatcher TextWatcher(Editable entra){
            Editable sai = entra;
                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(passwordEditText.length()<8) {
                            passwordEditText.setTextColor(Color.RED);
                        }else
                            if(passwordEditText.length() >= 8) {
                            passwordEditText.setTextColor(getColor(R.color.colorAccent));
                        }

                            if(emailEditText.length() == 0){
                               emailEditText.setTextColor(Color.RED);
                            }else{
                                emailEditText.setTextColor(getColor(R.color.colorAccent));
                            }

                        alterarBotao(emailEditText.length() != 0, emailEditText, R.string.emailVazio);
                        alterarBotao(passwordEditText.length() != 0, passwordEditText, R.string.senhaVazia);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                };

            return textWatcher;
        }

    public void alterarBotao(Boolean b, EditText e, int r){
        entrar.setEnabled(b);
        if(b) {
           // e.setError(getString(r));
            entrar.setBackgroundColor(getColor(R.color.colorPrimary));
        }
        else{
            //e.setError(null);
            entrar.setBackgroundColor(getColor(R.color.colorPrimaryLight));
        }
    }

    public void entrarButtonOnClick() {

        String email = emailEditText.getText().toString();
        String senha = passwordEditText.getText().toString();

        if (Utils.isValidEmail(email)) {


            /*progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.authenticating);
            progressDialog.setMessage(getString(R.string.pleaseWait));
            progressDialog.setCancelable(true);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
             */
            RetrieveHttp http = new RetrieveHttp();
            JSONObject json = null;
            try {
                json = http.execute(
                        getString(R.string.servidor) + "/login",
                        "POST", "email=" + email + "&senha=" + senha
                ).get();
                Log.i("JSON>>>>>>>\n", json.toString());
                status = (int) json.get("status");
                mensagem = (String) json.get("message");

                //Log.d(TAG, "login>> " + json.get("nlogado"));

            } catch (ExecutionException | InterruptedException | JSONException e) {
                e.printStackTrace();
            }finally {
                //progressDialog.dismiss();
            }
            preferences.edit().putString(ConfigActivity.PREF_EMAIL, email).apply();
            preferences.edit().putString(getString(R.string.keyEmail), email).apply();
            preferences.edit().putString(ConfigActivity.PREF_SENHA, senha).apply();

            if (status == 1) {
                preferences.edit().putBoolean("nlogado", false).apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                this.startActivity(intent);
            }
            else{
                Mensagens.okDialog(
                        this,
                        getString(R.string.tituloInfo),
                        mensagem,
                        getString(R.string.ok)
                );
            }
        }else {
            Mensagens.okDialog(
                    this,
                    getString(R.string.tituloInfo),
                    getString(R.string.emailVazio),
                    getString(R.string.ok)
            );
        }
   }

    private EditText.OnKeyListener getPasswordOnKeyListener() {

        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == 66) {
                    //Alerta("onKey");
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            LoginActivity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);
               }
                return false;
            }
        };
    }
}