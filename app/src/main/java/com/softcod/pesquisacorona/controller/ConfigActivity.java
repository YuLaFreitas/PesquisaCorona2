package com.softcod.pesquisacorona.controller;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.softcod.pesquisacorona.R;
import com.softcod.pesquisacorona.utils.RetrieveHttp;
import com.softcod.pesquisacorona.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ConfigActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOGCAT = "ConfigActivity";


    public static String PREF_EMAIL = "email_chave";
    public static String PREF_SENHA = "senha_cidadao";
    public static String PREF_RINGTONE = "_cidadao_ringtone";
    public static String PREF_ACTIVE = "_cidadao_ativada";
    public static String PREF_VIBRAR = "_cidadao_vibratoria";
    public static String CONT_EMAIL = "contato_email";
    public static String CONT_NOME = "contato_nome";
    public static String PREF_DOENCA = "DOENCA_CIADAO";
    public static String PREF_EMAIL_OLD = "old_email";
    public static String PREF_FILTER_BY_PERIOD = "filter_by_period";
    public static String PREF_FILTER_MIN = "filter_min";
    public static String PREF_FILTER_MAX = "filter_max";

    ConfigFragment configFragment;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;

    private static final int PERMISSION_VIBRATE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ContextCompat.checkSelfPermission(
                ConfigActivity.this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ConfigActivity.this,
                    Manifest.permission.VIBRATE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(ConfigActivity.this,
                        new String[]{Manifest.permission.VIBRATE},
                        PERMISSION_VIBRATE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        configFragment = new ConfigFragment();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //setContentView(R.layout.activity_configuracoes);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, configFragment)
                .commit();
        getFragmentManager().executePendingTransactions();

        updateSummaries();

    }

    public static class ConfigFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.config);


        }

    }

    /**
     * Transforma o e-mail no nome do topico
     *
     * @param email
     * @return
     */
    public String parseEmailTopic(String email) {
        return email.replace("@", "_at_");
    }

    /**
     * Essa função é chamada sempre que uma (qualquer uma) configuração seja alterada
     *
     * @param sharedPreferences
     * @param prefKey
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String prefKey) {
        Log.d(LOGCAT, "Mudança na preferencia "+prefKey);

        if(prefKey.equals(PREF_EMAIL) || prefKey.equals(PREF_SENHA) || prefKey.equals(CONT_NOME) ||
                prefKey.equals(PREF_DOENCA)) {

            final String prefMail = sharedPreferences.getString(PREF_EMAIL, "*nao cadastrada*");
            final String prefSenha = sharedPreferences.getString(PREF_SENHA, "*nao cadastrada*");
            final String contNome = sharedPreferences.getString(CONT_NOME, "*nao cadastrada*");
            final String phoneNumber = sharedPreferences.getString(PREF_DOENCA, "*nao cadastrada*");
            final String oldEmail = sharedPreferences.getString("email", "");

            int status = 2;
            String message = "";

            if (Utils.isValidEmail(prefMail)) {

                progressDialog = new ProgressDialog(this);
                //progressDialog.setTitle(R);
                //progressDialog.setMessage(getString(R.string.pleaseWait));
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                RetrieveHttp http = new RetrieveHttp();
                JSONObject json = null;
                try {
                    
                    json = http.execute(
                            getString(R.string.servidor),
                            "POST", "email="+prefMail
                                    +"&senha="+prefSenha+
                                    "&nome="+contNome+
                                    "&telefone="+phoneNumber+
                                    "&email_old="+oldEmail
                    ).get();

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                }

                try {
                    status = (int) json.get("status");
                    message = (String) json.get("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status == 1) {
                    sharedPreferences.edit().putString(PREF_EMAIL, prefMail).apply();
                    sharedPreferences.edit().putString(PREF_SENHA, prefSenha).apply();
                    sharedPreferences.edit().putString(CONT_NOME, contNome).apply();
                    sharedPreferences.edit().putString(PREF_DOENCA, phoneNumber).apply();
                    sharedPreferences.edit().putString(getString(R.string.keyEmail), prefMail).apply();
                    updateSummaries();
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }

                System.out.println(json);

            } else if (prefKey.equals(PREF_ACTIVE)) {

                boolean shouldPlaySound = sharedPreferences.getBoolean(PREF_ACTIVE, true);

                if (!shouldPlaySound) {

                    sharedPreferences.edit().putBoolean(PREF_VIBRAR, false).apply();
                    ((SwitchPreference) configFragment.findPreference(PREF_VIBRAR)).setChecked(false);
                   // sharedPreferences.edit().putBoolean(PREF_LED, false).apply();
                    //((SwitchPreference) configFragment.findPreference(PREF_LED)).setChecked(false);

                }

            } else if (prefKey.equals(PREF_VIBRAR)) {

                boolean shouldVibrate = sharedPreferences.getBoolean(PREF_VIBRAR, true);

                if (shouldVibrate) {

                    sharedPreferences.edit().putBoolean(PREF_ACTIVE, true).apply();
                    ((SwitchPreference) configFragment.findPreference(PREF_ACTIVE)).setChecked(true);

                }

            } /*else if (prefKey.equals(PREF_LED)) {

                boolean shouldBlinkLed = sharedPreferences.getBoolean(PREF_LED, true);

                if (shouldBlinkLed) {

                    sharedPreferences.edit().putBoolean(PREF_ACTIVE, true).apply();
                    ((SwitchPreference) configFragment.findPreference(PREF_ACTIVE)).setChecked(true);

                }

            } */else if (prefKey.equals(PREF_RINGTONE)) {

                updateSummaries();

            }
        }
    }

    /**
     * Atualiza o sumário das permissões
     */
    private void updateSummaries() {
        // SET SUMMARIES
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        //configFragment.findPreference(PREF_EMAIL).setSummary(sharedPreferences.getString(PREF_EMAIL, "").toCharArray().length > 0 ? sharedPreferences.getString(PREF_EMAIL, "") : getString(R.string.passwordDescription));
        //configFragment.findPreference(PREF_SENHA).setSummary(sharedPreferences.getString(PREF_SENHA, "").toCharArray().length > 0 ? "******" : getString(R.string.passwordDescription));
        //configFragment.findPreference(CONT_NOME).setSummary(sharedPreferences.getString(CONT_NOME, getString(R.string.nameDescription)));
        //configFragment.findPreference(PREF_PHONE_NUMBER).setSummary(sharedPreferences.getString(PREF_PHONE_NUMBER, getString(R.string.nameDescription)));
        //configFragment.findPreference(PREF_RINGTONE).setSummary(Utils.getDeviceSoundName(getApplicationContext(), sharedPreferences.getString(PREF_RINGTONE, "")));

        //getString(R.string.ringtoneDescription
    }

}
