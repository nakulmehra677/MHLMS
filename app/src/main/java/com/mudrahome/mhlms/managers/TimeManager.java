package com.mudrahome.mhlms.managers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeManager {
    private String strDate;
    private String strTime;
    private long timeStamp;

    public TimeManager() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

        strDate = dateFormatter.format(new Date());
        strTime = timeFormatter.format(new Date());

        Date date = new Date();
        timeStamp = date.getTime();
    }


    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
