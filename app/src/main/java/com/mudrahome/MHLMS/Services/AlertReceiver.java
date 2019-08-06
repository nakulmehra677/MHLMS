package com.mudrahome.MHLMS.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.mudrahome.MHLMS.Firebase.NotificationHelper;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String customerName = intent.getStringExtra("name");

        NotificationHelper notificationHelper = new NotificationHelper(context, customerName);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(customerName.hashCode(), nb.build());
    }
}