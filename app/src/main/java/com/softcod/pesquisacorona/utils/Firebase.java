package com.softcod.pesquisacorona.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

public class Firebase{
    private  String senha;
    private  String email;
    /*public Firebase(FirebaseApp firebaseApp) {
        super(firebaseApp);
    }*/

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
/*
    @NonNull
    @Override
    public Task<SignInMethodQueryResult> fetchSignInMethodsForEmail(@NonNull String s) {

        return super.fetchSignInMethodsForEmail(s);
    }

    @Override
    public FirebaseAuthSettings getFirebaseAuthSettings() {
        return super.getFirebaseAuthSettings();
    }

    @NonNull
    @Override
    public Task<Void> confirmPasswordReset(@NonNull String s, @NonNull String s1) {
        return super.confirmPasswordReset(s, s1);
    }*/
}
