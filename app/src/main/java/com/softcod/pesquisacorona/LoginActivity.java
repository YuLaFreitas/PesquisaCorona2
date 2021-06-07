package com.softcod.pesquisacorona;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.softcod.pesquisacorona.controller.ConfigActivity;
import com.softcod.pesquisacorona.controller.ResetPasswordActivity;
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
    private Button cadastro;
    private Button renomear;
    private Button entrarButton;
    private FirebaseAuth mAuth;
    String senha, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        passwordEditText = (EditText) findViewById(R.id.connection_passwordEditText);

        cadastro = (Button) findViewById(R.id.connection_cadastrar);
        renomear = (Button) findViewById(R.id.connection_renomearButton);

        entrarButton = (Button) findViewById(R.id.connection_entrar);
        emailEditText = (EditText) findViewById(R.id.connection_email);

        passwordEditText.setOnKeyListener(getPasswordOnKeyListener());
        passwordEditText.setTypeface(Typeface.DEFAULT);

       Intent chamarCadastro = new Intent(this, CadastrarUsuarioActivity.class);
       cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startActivity(chamarCadastro);}
        });

        entrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();

                senha = passwordEditText.getText().toString();
                email = emailEditText.getText().toString();
                acessando();
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


    public void acessando() {

        String email = emailEditText.getText().toString();
        String password =  passwordEditText.getText().toString();
        Log.d("entrar","teste");
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
                json = http.execute(getString(R.string.servidor) + "/?",
                        "POST", "tabela=cidadao"
                                +"&acao=verificar"
                                +"&email="+email+
                                "&senha="+password

                ).get();
                Log.d("JSON____", String.valueOf(json));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }

            preferences.edit().putString(ConfigActivity.PREF_EMAIL, email).apply();
            preferences.edit().putString(getString(R.string.keyEmail), email).apply();
            preferences.edit().putString(ConfigActivity.PREF_SENHA, password).apply();

            try {
                status = (int) json.get("status");
                message = (String) json.get("mensagem");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status == 1){
                preferences.edit().putBoolean("nlogado", false).apply();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
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

                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            LoginActivity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);

                }

                return false;
            }
        };

    }

   /* public void entrar() {

        final Context ct = this;
        Log.d("entrar","teste");

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mensagem("certo.", ct);
                            preferences.edit().putString(ConfigActivity.PREF_EMAIL, email).apply();
                            preferences.edit().putString(getString(R.string.keyEmail), email).apply();
                            preferences.edit().putString(ConfigActivity.PREF_SENHA, senha).apply();
                            setContentView(R.layout.activity_main);
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                mensagem("erro", ct);
                        }
                    }
                });
    }*/

   /* public void mensagem(String msng, Context c){
        Toast t = Toast.makeText(c, msng, Toast.LENGTH_LONG);
        t.getVerticalMargin();
        t.setMargin(300,100);
        t.setGravity(Gravity.HORIZONTAL_GRAVITY_MASK, 10, 10);
        t.show();
    }*/


    public void configurarBotaoLogin() {
        if (passwordEditText.getText().length() > 0 && emailEditText.getText().length() > 0) {
            senha = passwordEditText.getText().toString();
            email = emailEditText.getText().toString();
            acessando();
        } else {
            Snackbar.make(this.getCurrentFocus(), Snackbar.LENGTH_LONG, Integer.parseInt(getString(R.string.erroLogin))).show();
            }
    }
  /*  @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currenUser = mAuth.getCurrentUser();
        if(currenUser != null){
            updateUI(currenUser);
        }else {
            updateUI(null);
        }
    }*/

    /*private void updateUI(FirebaseUser user) {

            if(user != null){
                mensagem("OI " + user.getEmail(),this);
               // com.softcod.pesquisacorona.MainActivity mainActivity = new com.softcod.pesquisacorona.MainActivity();
                //mainActivity.abrirSecao();
            }
            else{
                mensagem(
                        "Informe seu E-Mail e Senha...", this);
            }}*/

}