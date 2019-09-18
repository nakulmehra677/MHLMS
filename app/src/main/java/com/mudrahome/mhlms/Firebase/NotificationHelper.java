package com.mudrahome.mhlms.Firebase;


import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.mudrahome.mhlms.R;

public class NotificationHelper extends ContextWrapper {
    public static String channelID;
    public static String channelName;
    private String customerName;

    private NotificationManager mManager;

    public NotificationHelper(Context base, String customerName) {
        super(base);
        this.customerName = customerName;
        channelID = customerName;
        channelName = customerName;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("Reminder!")
                .setContentText("You got a reminder to work on the lead of " + customerName)
                .setSmallIcon(R.mipmap.ic_launcher);
    }
}
