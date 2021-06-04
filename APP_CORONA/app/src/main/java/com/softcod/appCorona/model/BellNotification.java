package com.softcod.appCorona.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BellNotification {
    public String id;
    public String type;
    public String description;
    public Date createdAt;

    public BellNotification() {
        this.createdAt = new Date();
    }

    public String getDate() {
        try {
            SimpleDateFormat to = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
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
