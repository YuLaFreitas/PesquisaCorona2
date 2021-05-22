package com.softcod.pesquisacorona.utils;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class PlayAudio extends Service{
    private static final String LOGCAT = "PlayAudio";
    MediaPlayer objPlayer;

    public void onCreate(){
        super.onCreate();
        Log.d(LOGCAT, "Service Started!");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       // String ringtone_path = prefs.getString(ConfigActivity.PREF_RINGTONE, null);
        Uri ringtone_uri;
       /* if(ringtone_path != null) {
            ringtone_uri = Uri.parse(ringtone_path);
        } else {
            ringtone_uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }*/

        //objPlayer = MediaPlayer.create(this, ringtone_uri);
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(LOGCAT, "Media Player preparing");
        objPlayer.prepareAsync();
        objPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(LOGCAT, "Media Player started!");
                objPlayer.start();

                if(!objPlayer.isLooping()){
                    Log.d(LOGCAT, "Problem in Playing Audio");
                }
            }
        });

        return Service.START_REDELIVER_INTENT;
    }

    public void onStop(){
        objPlayer.stop();
        objPlayer.release();
    }

    public void onPause(){
        objPlayer.stop();
        objPlayer.release();
    }
    public void onDestroy(){
        objPlayer.stop();
        objPlayer.release();
    }
    @Override
    public IBinder onBind(Intent objIndent) {
        return null;
    }
}

