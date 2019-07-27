package com.development.mhleadmanagementsystemdev.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.development.mhleadmanagementsystemdev.Helper.NotificationHelper;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String customerName = intent.getStringExtra("name");

        NotificationHelper notificationHelper = new NotificationHelper(context, customerName);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(customerName.hashCode(), nb.build());
    }
}