package com.softcod.appCorona.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.softcod.appCorona.R;
import com.softcod.appCorona.utils.RetrieveHttp;
import com.softcod.appCorona.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class EditarUsuarioActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfEditText;
    private EditText telefoneConfEditText;
    private EditText nomeConfEditText;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cadastrar);

        TextView titulo = (TextView) findViewById(R.id.connection_connectionTextView);
        titulo.setText(getString(R.string.editarUsuario));

        telefoneConfEditText =  (EditText) findViewById(R.id.connection_phonedConfEditText);
        nomeConfEditText = (EditText) findViewById(R.id.connection_nameConfEditText);
        emailEditText = (EditText) findViewById(R.id.connection_emailEditText);
        passwordEditText = (EditText) findViewById(R.id.connection_passwordEditText);
        passwordConfEditText = (EditText) findViewById(R.id.connection_passwordConfEditText);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        RetrieveHttp http = new RetrieveHttp();
        JSONArray jsonArray = null;
        JSONObject usuarioAll;
        try {
            usuarioAll = http.execute(getString(R.string.servidor) +
             "/get_usuario", "POST", "email="+ preferences.getString(getString(R.string.keyEmail),"")
            ).get();

            JSONArray usuario = usuarioAll.getJSONArray("usuario");

            JSONObject fracao = (JSONObject) usuario.get(0);

        telefoneConfEditText.setText((String) fracao.get("TELEFONE"));
        nomeConfEditText.setText((String) fracao.get("NOME"));
        emailEditText.setText((String) fracao.get("EMAIL"));
        emailEditText.setEnabled(false);
        //Log.d("nome>>>\n\n\n\n", fracao.toString());
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

    }
    public void entrarButtonOnClick(View v) {
        String nome = nomeConfEditText.getText().toString();
        String telefone = telefoneConfEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String senha =  passwordEditText.getText().toString();
        String senhaConf =  passwordConfEditText.getText().toString();
        int status = 2;
        String message = "";

        if (Utils.isValidEmail(email)) {

            if(senha.equals(senhaConf)){

                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle(R.string.authenticating);
                progressDialog.setMessage(getString(R.string.pleaseWait));
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                RetrieveHttp http = new RetrieveHttp();
                JSONObject json = null;
                try {
                    json = http.execute(getString(R.string.servidor) +
                            "/edita_usuario", "POST",
                            "email="+email+ "&email_old=" + preferences.getString(getString(R.string.keyEmail),"")
                            + "&senha="+senha + "&nome="+nome  +"&telefone="+telefone
                    ).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                }

                preferences.edit().putString(ConfigActivity.PREF_EMAIL, email).apply();
                preferences.edit().putString(getString(R.string.keyEmail), email).apply();
                preferences.edit().putString(ConfigActivity.PREF_SENHA, senha).apply();
                preferences.edit().putString(ConfigActivity.CONT_NOME, nome).apply();
                preferences.edit().putString(ConfigActivity.PREF_PHONE_NUMBER, telefone).apply();

                try {
                    status = (int) json.get("status");
                    message = (String) json.get("message");
                   // Log.d("Edição", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(status == 1){
                    preferences.edit().putBoolean("nlogado", false).apply();
                    Intent intent = new Intent(EditarUsuarioActivity.this, MainActivity.class);
                    this.startActivity(intent);
                }else{
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, "" + getString(R.string.invalidPasswordText), Toast.LENGTH_LONG).show();
            }

        } else {

            Toast.makeText(this, "" + getString(R.string.invalidEmailText), Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private EditText.OnKeyListener getPasswordOnKeyListener() {

        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == 66) {

                    InputMethodManager imm = (InputMethodManager)getSystemService(EditarUsuarioActivity.INPUT_METHOD_SERVICE);
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
                    InputMethodManager imm = (InputMethodManager)getSystemService(EditarUsuarioActivity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordConfEditText.getWindowToken(), 0);
                }
                return false;
            }
        };

    }

    public void voltar(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        Snackbar.make(view, getString(R.string.sairUsuarioEdCa), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(intent);
                    }
                }).show();
    }
}