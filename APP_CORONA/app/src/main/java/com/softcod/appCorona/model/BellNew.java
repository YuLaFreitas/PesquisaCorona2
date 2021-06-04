package com.softcod.appCorona.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BellNew implements Parcelable {
    private String ID;
    private String ID_USUARIO;
    private String SERIAL;
    private String NOME;
    private String DATA_CADASTRO;

    protected BellNew(Parcel in) {
        ID = in.readString();
        ID_USUARIO = in.readString();
        SERIAL = in.readString();
        NOME = in.readString();
        DATA_CADASTRO = in.readString();
    }

    public static final Creator<BellNew> CREATOR =
            new Creator<BellNew>() {
        @Override
        public BellNew createFromParcel(Parcel in) {
            return new BellNew(in);
        }

        @Override
        public BellNew[] newArray(int size) {
            return new BellNew[size];
        }
    };

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID_USUARIO() {
        return ID_USUARIO;
    }

    public void setID_USUARIO(String ID_USUARIO) {
        this.ID_USUARIO = ID_USUARIO;
    }

    public String getSERIAL() {
        return SERIAL;
    }

    public void setSERIAL(String SERIAL) {
        this.SERIAL = SERIAL;
    }

    public String getNOME() {
        return NOME;
    }

    public void setNOME(String NOME) {
        this.NOME = NOME;
    }

    public String getDATA_CADASTRO() {
        return DATA_CADASTRO;
    }

    public void setDATA_CADASTRO(String DATA_CADASTRO) {
        this.DATA_CADASTRO = DATA_CADASTRO;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(ID_USUARIO);
        dest.writeString(SERIAL);
        dest.writeString(NOME);
        dest.writeString(DATA_CADASTRO);
    }
}
