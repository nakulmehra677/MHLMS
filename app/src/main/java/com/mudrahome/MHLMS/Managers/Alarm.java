package com.mudrahome.MHLMS.Managers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.mudrahome.MHLMS.Services.AlertReceiver;

import java.util.Calendar;

public class Alarm {
    private Context context;

    public Alarm(Context context) {
        this.context = context;
    }

    public void startAlarm(Calendar c, String name) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("name", name);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, name.hashCode(), intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
    }

    public void cancelAlarm(String name) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, name.hashCode(), intent, 0);

        alarmManager.cancel(pendingIntent);
    }
}
