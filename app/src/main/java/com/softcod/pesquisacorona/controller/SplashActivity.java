package com.softcod.pesquisacorona.controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity {

    final String KEY_VERSION = "version";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String version = preferences.getString(KEY_VERSION, "1.0");

        switch (version) {

            case "1.0":

                try { // Unsubscribe from all topics and then subscribe to the correct one

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                           /* try {
                              FirebaseInstanceId.getInstance().deleteInstanceId();
                               Utils.log("New Token: " + FirebaseInstanceId.getInstance().getToken());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                        }
                    }).start();

                    preferences.edit().putString(KEY_VERSION, "3.0").apply();

                } catch (Exception e) {

                    e.printStackTrace();

                }

            case "3.0":

                FirebaseMessaging.getInstance().unsubscribeFromTopic("_");

                preferences.edit().putString(KEY_VERSION, "3.0.3").apply();

            case "3.0.3":

                break;

        }

       // Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        finish();
    }
}
