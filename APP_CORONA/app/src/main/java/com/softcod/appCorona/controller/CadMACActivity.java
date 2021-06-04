package com.softcod.appCorona.controller;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.softcod.appCorona.R;
import com.softcod.appCorona.model.MaqNew;
import com.softcod.appCorona.utils.RetrieveHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class CadMACActivity extends AppCompatActivity {

    private EditText nomeMaquina;
    private TextView qrCode;
    private Button buttonSalvar;
    private Button buttonQRCODE;
    private SharedPreferences prefs = null;
    private ProgressDialog progressDialog;
    private TimePicker relogio;
    private MaqNew maq = null;
    String codigoQR;
    IntentResult intentResult;

    //String nomeMaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_maq);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         relogio = findViewById(R.id.tempo);
         relogio.setIs24HourView(true);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        checkPermission();

        prefs = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());

        nomeMaquina = findViewById(R.id.maquina);
        qrCode = findViewById(R.id.refMacQR);
        buttonSalvar = findViewById(R.id.button);
        buttonQRCODE = findViewById(R.id.QRbutton);

        //---------impede a perca prematura dos dados
        if(savedInstanceState != null) {
            codigoQR = (String) savedInstanceState.get("codigoQR");
            qrCode.setText(codigoQR);
            //nomeMaq = (String) savedInstanceState.get("nomeMaquina");
        }


        if(getIntent().getExtras() == null){
            cadastrarMaquina();
        }else {
            maq = getIntent().getParcelableExtra(getString(R.string.keyMaquina));
            editarMaquina();
        }

        buttonQRCODE.setOnClickListener(v -> openCamera());

    }

    private void checkPermission() {
        // Verifica necessidade de verificacao de permissao
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, getString(R.string.cameraErroPermissao), Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        102);
            } else {
                // Solicita permissao
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        102);
            }
        }
    }

    private void openCamera(){

        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);
//            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            intentIntegrator.setPrompt(getString(R.string.focarQR));
            intentIntegrator.setCameraId(0);
            intentIntegrator.initiateScan();
        }else{
            checkPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, @Nullable Intent data) {
       super.onActivityResult(requestCode, resultCode, data);

        intentResult = IntentIntegrator
                .parseActivityResult(requestCode,resultCode,data);

        if(intentResult != null){
            if (intentResult.getContents() !=  null){
                qrCode.setText(intentResult.getContents());
                //Log.e("codigo", intentResult.getContents());
                    codigoQR = qrCode.getText().toString();
            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.erroScannear), Toast.LENGTH_LONG).show();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //Log.i("onSave", "\n\n\n" + codigoQR+ "\n\n\n\n");
        outState.putString("codigoQR", codigoQR );

        super.onSaveInstanceState(outState);
    }

    public void cadastrarMaquina(){
        String nome = nomeMaquina.getText().toString();
        String mac = codigoQR;
        String email = prefs.getString(getString(R.string.keyEmail), "");
        LinearLayout casa = findViewById(R.id.casa);
        casa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CadMACActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        final int[] status = {2};
        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(CadMACActivity.this);
                progressDialog.setTitle(R.string.authenticating);
                progressDialog.setMessage(getString(R.string.pleaseWait));
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                String message = "";

                RetrieveHttp http = new RetrieveHttp();
                JSONObject json = null;
                try {
                    json = http.execute(getString(R.string.servidor) +
                                    "/cadastra_maquina",
                            "POST", "email="+email+"&nome="+nome+
                                    "&mac="+mac + "&tempo_teorico=" +
                                    relogio.getHour()*60 + relogio.getMinute()).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                }

                try {
                    status[0] = (int) json.get("status");
                    message = (String) json.get("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(json);

                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        });
        if(status[0] == 1){
            Intent intent = new Intent(this,
                    MaqActivity.class);
            startActivity(intent);
        }

    }

    public void editarMaquina(){
        TextView titulo = findViewById(R.id.tituloMac);
        titulo.setText(getString(R.string.editarMac));

        TextView refCodQR = findViewById(R.id.refMacQR);
        String nome = nomeMaquina.getText().toString();

        String mac = codigoQR;

        refCodQR.setText(maq.getNOME());
        int tempo =  relogio.getHour()*60 + relogio.getMinute();
        String id = maq.getID();

        LinearLayout casa = findViewById(R.id.casa);

        casa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CadMACActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        progressDialog = new ProgressDialog(this);


        String message = "";

        final int[] status = {2};
        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "";
                JSONObject json = null;
                progressDialog.setTitle(R.string.authenticating);
                progressDialog.setMessage(getString(R.string.pleaseWait));
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                try {

                    RetrieveHttp http = new RetrieveHttp();
                   json =  http.execute(getString(R.string.servidor) + "/edita_maquina",
                                "POST", "nome=" + nome + "&mac=" + mac + "&tempo_teorico=" + tempo + "&id=" + id).get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
                    }

                    try {
                        status[0] = (int) json.get("status");
                        message = (String) json.get("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        });

        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

        if(status[0] == 1){
            Intent intent = new Intent(CadMACActivity.this, MaqActivity.class);
            startActivity(intent);
        }

    }

}