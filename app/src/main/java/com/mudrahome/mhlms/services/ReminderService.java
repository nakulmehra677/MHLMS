package com.mudrahome.mhlms.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class ReminderService extends IntentService {

    public ReminderService() {
        super("Timer Service");
        Log.v("timer","Backgroung service has been started.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        for(int i=0;i<20;i++){
            Log.v("timer ", "i = " + i);
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
