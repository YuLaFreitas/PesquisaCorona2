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
//import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    /*private static String TAG = "FirebaseMessaging";
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

           MaqNotificationInterrupcao maqNotificationInterrupcao =
                    new MaqNotificationInterrupcao();
            maqNotificationInterrupcao.description = data.get("description");
            maqNotificationInterrupcao.id = data.get("id");
            maqNotificationInterrupcao.type = data.get("type");


           String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);

            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new
                        NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
            }

            //Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pi = PendingIntent
                    .getActivity(this, 0, intent, 0);

            Notification notification = new NotificationCompat.Builder(this,
                    getString(R.string.default_notification_channel_id))
                    .setSmallIcon(R.drawable.ic_engrenagem)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(data.get("description"))
                    .setSound(RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(getColor(R.color.colorPrimaryLight))
                    .setContentIntent(pi)
                    .build();


            notificationManager.notify(createID(), notification);

            List<MaqNotificationInterrupcao> list = getNotifFromSerialized(
                    prefs.getString(getString(R.string.notificacaoMaquinas), null)
            );
            saveNotifications(list, maqNotificationInterrupcao);

          //  Log.d(TAG, "onMessageReceived: notification saved!");
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

    public static List<MaqNotificationInterrupcao>
    getNotifFromSerialized(String serializedObject){
        if (serializedObject != null) {
            MaqNotificationInterrupcao[] arr = new Gson().fromJson(
                    serializedObject, MaqNotificationInterrupcao[].class);
            return Arrays.asList(arr);
        }
        return new ArrayList<>();
    }

    public void saveNotifications(List<MaqNotificationInterrupcao> listNotif,
                                  MaqNotificationInterrupcao maqNotif){
        Gson gson = new Gson();
        List<MaqNotificationInterrupcao> listNew = new ArrayList<>();

        for(int i = 0; i < listNotif.size(); i++){
            listNew.add(listNotif.get(i));
        }

        listNew.add(maqNotif);

        String json = gson.toJson(listNew);
        prefs.edit().putString(getString(R.string.notificacaoMaquinas),
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
    }*/
}
