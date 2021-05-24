package com.softcod.pesquisacorona;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.softcod.pesquisacorona.controller.ConfigActivity;

public class CadastrarUsuarioActivity extends AppCompatActivity {


    private EditText apelidoEditText;
    private EditText passwordEditText;
    private EditText passwordConfEditText;
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

        nomeConfEditText = (EditText) findViewById(R.id.connection_nameConfEditText);
        apelidoEditText = (EditText) findViewById(R.id.connection_apelidoConfEditText);
        passwordEditText = (EditText) findViewById(R.id.connection_passwordEditText);
        passwordConfEditText = (EditText) findViewById(R.id.connection_senhaConfEditText2);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //passwordEditText.setOnKeyListener(getPasswordOnKeyListener());
       // passwordEditText.setTypeface(Typeface.DEFAULT);
       // passwordConfEditText.setOnKeyListener(getPasswordConfOnKeyListener());
        //passwordConfEditText.setTypeface(Typeface.DEFAULT);

        entrarButton = (Button) findViewById(R.id.connection_entrar);

    }

   public void voltar(View v) {
       Intent intent = new Intent(CadastrarUsuarioActivity.this, MainActivity.class);
       Snackbar.make(v, getString(R.string.sairUsuarioEdCa), Snackbar.LENGTH_INDEFINITE)
               .setAction(getString(R.string.ok), new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       startActivity(intent);
                       //onBackPressed();
                   }
               }).show();
    }

    public void salvar(View v) {
        String nome = nomeConfEditText.getText().toString();
        //String telefone = telefoneConfEditText.getText().toString();
        String apelido = apelidoEditText.getText().toString();
        String senha =  passwordEditText.getText().toString();
        int status = 2;
        String message = "";
        preferences.edit().putString(ConfigActivity.PREF_EMAIL, apelido).apply();
        preferences.edit().putString(getString(R.string.keyEmail), apelido).apply();
        preferences.edit().putString(ConfigActivity.PREF_SENHA, senha).apply();
        preferences.edit().putString(ConfigActivity.CONT_NOME, nome).apply();
       /* if (Utils.isValidEmail(apelido)) {

            if(senha.equals(senhaConf)){

                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle(R.string.aguarde);
                progressDialog.setMessage(getString(R.string.prossesando));
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                /*RetrieveHttp http = new RetrieveHttp();
                JSONObject json = null;
                try {

                    json = http.execute(getString(R.string.servidor) +
                            "/cadastrar", "POST",
                            "apelido="+apelido+ "&senha="+senha +
                            "&nome="+nome  +"&telefone="+telefone
                    ).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                }


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
                Toast.makeText(this, "" + getString(R.string.invalidPasswordText),
                        Toast.LENGTH_LONG).show();
            }

        } else {

            Toast.makeText(this, "" +
                    getString(R.string.invalidEmailText),
                    Toast.LENGTH_LONG).show();

        }*/

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