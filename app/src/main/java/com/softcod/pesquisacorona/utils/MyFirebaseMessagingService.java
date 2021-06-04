package com.softcod.pesquisacorona.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.softcod.pesquisacorona.MainActivity;
import com.softcod.pesquisacorona.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    private static String TAG = "FirebaseMessaging";
    SharedPreferences prefs = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        Map<String, String> data = remoteMessage.getData();
       // Log.d(TAG, "onMessageReceived>>"+data.toString());
        //id da parada
        if (data.containsKey("id")
                && data.containsKey("type")
                && data.containsKey("description")) {
           // Log.i(TAG, "onMessageReceived: data valid");
            Log.d(TAG, "onMessageReceived: data valida \n\n\n\n"
                    +data.toString());

           AlertaFirebase alerta =
                    new AlertaFirebase();
            alerta.id = data.get("id");
            alerta.type = data.get("type");


           String channelId = getString( R.string.idNotifique);
            String channelName = getString(R.string.idNotifique);

            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new
                        NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
            }

            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pi = PendingIntent
                    .getActivity(this, 0, intent, 0);

            Notification notification = new NotificationCompat.Builder(this,
                    getString(R.string.idNotifique))
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(data.get("description"))
                    .setSound(RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(getColor(R.color.purple_200))
                    .setContentIntent(pi)
                    .build();


            notificationManager.notify(createID(), notification);

            List<AlertaFirebase> list = getNotifFromSerialized(
                    prefs.getString(getString(R.string.idNotifique), null)
            );
            saveNotifications(list, alerta);

            Log.d(TAG, "onMessageReceived: notification saved!");
        }
    }

    public int createID(){
        Date now = new Date();
        int id = Integer
                .parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US)
                .format(now));
        Log.i("id dasmensagems", " \n\n\n" + id);
        return id;
    }

    public static List<AlertaFirebase>
    getNotifFromSerialized(String serializedObject){
        if (serializedObject != null) {
            AlertaFirebase[] arr = new Gson().fromJson(
                    serializedObject, AlertaFirebase[].class);
            return Arrays.asList(arr);
        }
        return new ArrayList<>();
    }

    public void saveNotifications(List<AlertaFirebase> listNotif,
                                  AlertaFirebase maqNotif){
        Gson gson = new Gson();
        List<AlertaFirebase> listNew = new ArrayList<>();

        for(int i = 0; i < listNotif.size(); i++){
            listNew.add(listNotif.get(i));
        }

        listNew.add(maqNotif);

        String json = gson.toJson(listNew);
        prefs.edit().putString(getString(R.string.idNotifique),
                json).apply();
    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        String fcm_id = token;
        RetrieveHttp http = new RetrieveHttp();
        //JSONObject json = null;

        try {
            if(prefs != null){
               http.execute(getString(R.string.servidor) +
                                "/dispositivos", "POST",
                        "email="+prefs.getString("email", "")
                        +"&fcm_id=" +fcm_id
                ).get();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println("Class FireMsng\n" + json);
    }
}
