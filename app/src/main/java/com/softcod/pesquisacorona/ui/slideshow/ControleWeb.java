package com.softcod.pesquisacorona.ui.slideshow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.softcod.pesquisacorona.R;

public class ControleWeb {
    Context ctxt;

    String sintoma;
    int nivel;

    public String getSintoma() {
        return sintoma;
    }

    public void setSintoma(String sintoma) {
        this.sintoma = sintoma;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    ControleWeb(Context c){ctxt = c;}


    @JavascriptInterface
    public void opcao(String sint, int nivel){
        Log.d("sintoma", "teste " + sint +" / " + nivel);
        setNivel(nivel);
        setSintoma(sint);
    }

    @SuppressLint({"JavascriptInterface", "ResourceType"})
    public void getOpcao(){
        Intent intent = new Intent(ctxt, SlideshowViewModel.class);

        intent.putExtra(String.valueOf(R.string.sintoma), getSintoma());
        intent.putExtra(String.valueOf(R.string.nivel), getNivel());

    }


}
