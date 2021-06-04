package com.softcod.appCorona.model;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MaqNotificationInterrupcao {
    public String id;
    public String type;
    public String description;
    public Date createdAt;


    public MaqNotificationInterrupcao() {
        this.createdAt = new Date();
    }

    public String getDate() {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat to =
                    new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
            return to.format(createdAt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTypeDesc() {
        return type + " " + id;
    }

}
