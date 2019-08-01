package com.development.mhleadmanagementsystemdev.Models;

public class TimeModel {
    String time, date;
    long timeStamp;

    public TimeModel(String time, String date, long timeStamp) {
        this.time = time;
        this.date = date;
        this.timeStamp = timeStamp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
