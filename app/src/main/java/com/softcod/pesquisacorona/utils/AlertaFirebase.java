package com.softcod.pesquisacorona.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlertaFirebase {
    public String type;
    public String id;
    public Date data;
    public AlertaFirebase(){
        this.data = new Date();
    }
    public String getDate() {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat to =
                    new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
            return to.format(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTypeDesc() {

        return type + " " + id;
    }

}
