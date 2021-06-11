package com.softcod.pesquisacorona.utils;

import android.content.ContentValues;
import android.util.Log;

import java.util.ArrayList;

public class Cache{
    ContentValues cv;
    int nivel;
    String Sintoma;
    String Data;
    int[] nivelA = null;
    String[] SintomaA = null;
    int i = 0;

    public Cache(){
        super();
        cv = new ContentValues();
    }

    @Override
    public String toString() {
        return "";
    }

    public ArrayList<String> guardar(){
        
    return null;
    }
    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        Log.d("setNilvel", String.valueOf(nivel));
        this.nivel = nivel;
    }

    public String getSintoma() {
        return Sintoma;
    }

    public void setSintoma(String sintoma) {
        Log.d("setSintoma", sintoma);
        this.Sintoma = sintoma;
    }

   /* public String getData() {
        return Data;
    }*/

    /*public void setData(String data) {
        Data = data;
    }*/
}
