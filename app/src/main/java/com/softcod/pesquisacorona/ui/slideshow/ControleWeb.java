package com.softcod.pesquisacorona.ui.slideshow;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.annotation.RequiresApi;

import com.softcod.pesquisacorona.R;
import com.softcod.pesquisacorona.utils.RetrieveHttp;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ControleWeb {
    Context ctxt;
    RetrieveHttp http;
    JSONObject json = null;
    SharedPreferences preferences;
    public ControleWeb(Context c) {
        ctxt = c;
        preferences = PreferenceManager
                .getDefaultSharedPreferences(c);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @JavascriptInterface
    public void opcao(String sint, int nivel) {

        http = new RetrieveHttp();

        //a pessoa seleciona, envia para a api, e vou colocar outro borão
        //para ela poder excluir
        try {
            String i = preferences.getString("id_usuario", "");
            Log.d("\n\n\n\n\n\n\n\nID\n\n\n\n", ""+i);
            json = http.execute(
                    ctxt.getString(R.string.servidor) + "/?",
                    "POST",
                    "acao=registrar_pesquisa" +
                    "&tabela=sintoma" +
                    "&idcidadao=" +i+
                    "&nivel=" + nivel +
                    "&sintoma=" + sint
            ).get();

            Log.d("json", json.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }

}
