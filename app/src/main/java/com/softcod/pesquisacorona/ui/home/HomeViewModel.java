package com.softcod.pesquisacorona.ui.home;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.softcod.pesquisacorona.utils.RetrieveHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mText = new MutableLiveData<>();

            mText.setValue(
                    "FADIGA " + getDado("fadiga") +
                    "\nMUSCULAR "+ getDado("muscular") +
                    "\nPALADAR " + getDado("paladar") +
                            "\nFALTA DE AR " + getDado("asfixia") +
                            "\nDOR DE CABEÇA " + getDado("cefalia") +
                            "\nDORES MUSCULARES " + getDado("muscular") +
                            "\nQUEDA DE CABELO " + getDado("cabelo") +
                            "\nDISTÚRBIO SENSORIAL " + getDado("paladar") +
                            "\nDORES NO PEITO " + getDado("atacardia") +
                            "\nTROMBOSES " + getDado("trombose") +
                            "\nTONTURA " + getDado("tontura") +
                            "\nPALPITAÇÕES " + getDado("palpitacao") +
                            "\nDEPRESSÃO " + getDado("depressao") +
                            "\nANSIEDADE " + getDado("ansiedade") +
                            "\nDISTURBIO NEORAL " + getDado("neuro") +
                    "\nASFIXIA "+ getDado("asfixia")


            );

    }
    RetrieveHttp http;
    JSONObject json = null;
    @SuppressLint("RestrictedApi")
    public String getDado(String pesquisa){
        String resultado = "NADA";
        http = new RetrieveHttp();

        //a pessoa seleciona, envia para a api, e vou colocar outro borão
        //para ela poder excluir
        try {
            json = http.execute(
                    "http://softcod.com.br/api/" +
                            "filtros.php/?",
                            "POST",
                            "tabela=sintoma" +
                            "&coluna=sintoma" +
                            "&pesquisa=" + pesquisa
            ).get();

            String quant =json.getString("q");
            Log.d("Quantidade", quant);
            resultado = quant;
        } catch (NullPointerException | InterruptedException | ExecutionException | JSONException e) {
            return "erro" + e.toString();
        }
        return resultado;

    }

    public LiveData<String> getText() {
        return mText;
    }
}