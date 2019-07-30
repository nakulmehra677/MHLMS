package com.development.mhleadmanagementsystemdev.Managers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TimeManager {

    public TimeManager() {
    }

    public HashMap<String, String> getTime() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

        String strDate = dateFormatter.format(new Date());
        String strTime = timeFormatter.format(new Date());

        HashMap<String, String> time = new HashMap<>();
        time.put("date", strDate);
        time.put("time", strTime);
        return time;
    }
}
