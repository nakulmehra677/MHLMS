package com.mudrahome.mhlms.services;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mudrahome.mhlms.R;
import com.mudrahome.mhlms.activities.LeadListActivity;
import com.mudrahome.mhlms.managers.ProfileManager;

import java.util.Calendar;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

public class NotificationService extends FirebaseMessagingService {

    int id = 12;
    PendingIntent pendingIntent;
    Intent resultIntent;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());

    }

    public void showNotification(String title, String message) {

        try {
            String[] bodymessage = message.split("@@");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel channel = new NotificationChannel(getResources().getString(R.string.notification_channel_id), "User Notification", NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.setLightColor(Color.BLUE);
                channel.enableVibration(true);
                channel.setShowBadge(true);
                channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
                channel.setVibrationPattern(new long[]{100, 1000, 100, 1000, 100});

                NotificationManager manager = getSystemService(NotificationManager.class);
                assert manager != null;
                manager.createNotificationChannel(channel);

                Log.d("NotificationService", "showNotification: channel created");
            }
            if (!bodymessage[1].isEmpty()) {

                resultIntent = new Intent(NotificationService.this, LeadListActivity.class)
                        .putExtra("UIDNotification", bodymessage[1]);

                Log.d("UIDNotification", bodymessage[1] + "      " + bodymessage[0]);

                pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, getResources().getString(R.string.notification_channel_id))
                        .setContentTitle(title)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVibrate(new long[]{100, 1000, 100, 1000})
                        .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(bodymessage[0].trim()))
                        .setContentText(bodymessage[0])
                        .setContentIntent(pendingIntent);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(NotificationService.this);
                managerCompat.notify(id, builder.build());
                id++;

                Log.d("NotificationService", "showNotification: notification show");

            }


            Log.d("AlertReceiver", "showNotification: ");

            //if(title.equals("New Lead")){
            // startTimer();
            ///}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d("Token updated ", s);
        ProfileManager manager = new ProfileManager();
        manager.updateDeviceToken(s);
        super.onNewToken(s);
    }

    private void startTimer() {
        Calendar calendar = Calendar.getInstance();
        System.out.println("The current date is : " + calendar.getTime());
        //calendar.add(Calendar.MINUTE, 1);
        System.out.println("1 hour later: " + calendar.getTime());

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
