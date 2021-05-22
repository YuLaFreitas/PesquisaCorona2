package com.softcod.pesquisacorona;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.softcod.pesquisacorona.utils.RetrieveHttp;
import com.softcod.pesquisacorona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class CadastrarUsuarioActivity extends AppCompatActivity {


    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfEditText;
    private EditText telefoneConfEditText;
    private EditText nomeConfEditText;

    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private Button entrarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cadastrar);

        telefoneConfEditText =  (EditText) findViewById(R.id.connection_phonedConfEditText);
        nomeConfEditText = (EditText) findViewById(R.id.connection_nameConfEditText);
        emailEditText = (EditText) findViewById(R.id.connection_emailEditText);
        passwordEditText = (EditText) findViewById(R.id.connection_passwordEditText);
        passwordConfEditText = (EditText) findViewById(R.id.connection_passwordConfEditText);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        passwordEditText.setOnKeyListener(getPasswordOnKeyListener());
        passwordEditText.setTypeface(Typeface.DEFAULT);
        passwordConfEditText.setOnKeyListener(getPasswordConfOnKeyListener());
        passwordConfEditText.setTypeface(Typeface.DEFAULT);

        entrarButton = (Button) findViewById(R.id.connection_entrar);

    }

   public void voltar(View v) {
       Intent intent = new Intent(CadastrarUsuarioActivity.this, MainActivity.class);
       Snackbar.make(v, getString(R.string.sairUsuarioEdCa), Snackbar.LENGTH_INDEFINITE)
               .setAction(getString(R.string.ok), new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       startActivity(intent);
                   }
               }).show();
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

                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle(R.string.authenticating);
                progressDialog.setMessage(getString(R.string.pleaseWait));
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                RetrieveHttp http = new RetrieveHttp();
                JSONObject json = null;
                try {

                    json = http.execute(getString(R.string.servidor) +
                            "/cadastrar", "POST",
                            "email="+email+ "&senha="+senha +
                            "&nome="+nome  +"&telefone="+telefone
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
                    Log.d("Cadastro", message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(status == 1){
                    preferences.edit().putBoolean("nlogado", false).apply();
                    Intent intent = new Intent(CadastrarUsuarioActivity.this, MainActivity.class);
                    this.startActivity(intent);
                }else{
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, "" + getString(R.string.invalidPasswordText), Toast.LENGTH_LONG).show();
            }

        } else {

            Toast.makeText(this, "" +
                    getString(R.string.invalidEmailText),
                    Toast.LENGTH_LONG).show();

        }

    }

    private EditText.OnKeyListener getPasswordOnKeyListener() {

        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == 66) {

                    InputMethodManager imm = (InputMethodManager)getSystemService(CadastrarUsuarioActivity.INPUT_METHOD_SERVICE);
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
                    InputMethodManager imm = (InputMethodManager)getSystemService(CadastrarUsuarioActivity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordConfEditText.getWindowToken(), 0);
                }
                return false;
            }
        };

    }

}