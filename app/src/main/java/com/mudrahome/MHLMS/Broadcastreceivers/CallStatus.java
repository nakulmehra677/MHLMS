package com.mudrahome.MHLMS.Broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mudrahome.MHLMS.Managers.RecordingManager;

public class CallStatus extends BroadcastReceiver {
    private RecordingManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            Log.d("State", "Broadcast started");
            manager = new RecordingManager(context);
            manager.startRecording();

        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            Log.d("State", "Broadcast stopped");
            manager.stopRecording();
            manager.uploadRecording();
        }
    }
}
