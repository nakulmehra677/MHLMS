package com.mudrahome.MHLMS.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import com.mudrahome.MHLMS.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;

public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("NotificationMsg","sgdg");
        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    public void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notification")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message.trim()))
                .setContentText(message);

        NotificationManagerCompat managerCompat =NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());

        //if(title.equals("New Lead")){
           // startTimer();
        ///}
    }
    private void startTimer(){
        Calendar calendar = Calendar.getInstance();
        System.out.println("The current date is : " + calendar.getTime());
        //calendar.add(Calendar.MINUTE, 1);
        System.out.println("1 hour later: " + calendar.getTime());

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
