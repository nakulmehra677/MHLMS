package com.mudrahome.mhlms.managers;

import android.util.Log;

import com.mudrahome.mhlms.model.TimeModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeManager {

    public TimeManager() {
    }

    public TimeModel getTime() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

        String strDate = dateFormatter.format(new Date());
        String strTime = timeFormatter.format(new Date());

        Date date = new Date();
        long timeStamp = date.getTime();
        Log.d("TimeStamp", String.valueOf(timeStamp));
        TimeModel timeModel = new TimeModel(strTime, strDate, timeStamp);

        return timeModel;
    }
}
