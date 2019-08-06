package com.mudrahome.MHLMS.Broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mudrahome.MHLMS.Managers.Callrecord;

public class CallStatus extends BroadcastReceiver {
    public CallStatus() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Call started", "Dfv");
        Callrecord callrecord = new Callrecord();
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            Log.d("Call started", "Dfv");

        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            Log.d("Call ended", "Dfv");
        }
    }
}
