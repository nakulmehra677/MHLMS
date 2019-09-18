package com.mudrahome.MHLMS.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.mudrahome.MHLMS.Firebase.NotificationHelper;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("AlertReceiver", " context " );
        try {
            Log.d("AlertReceiver", " context " + context.getApplicationContext().getPackageName());
        }catch (Exception e){

        }


        String customerName = intent.getStringExtra("name");
        Log.d("AlertReceiver", " context " );
        NotificationHelper notificationHelper = new NotificationHelper(context, customerName);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(customerName.hashCode(), nb.build());
    }
}