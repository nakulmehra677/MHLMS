package com.mudrahome.MHLMS.Managers;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;

public class Callrecord extends Service {

    private MediaRecorder recorder = null;
    private static String fileName = null;
    private boolean recordstarted = false;


    public Callrecord() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


        TelephonyManager manager = (TelephonyManager) getApplicationContext()
                .getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        manager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);

                if (TelephonyManager.CALL_STATE_IDLE == state && recorder == null) {
                    recorder.stop();
                    recorder.reset();
                    recorder.release();
                    recordstarted = false;
                } else if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                    try {
                        recorder.prepare();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    recorder.start();
                    recordstarted = true;
                }
            }
        },PhoneStateListener.LISTEN_CALL_STATE);

        return START_STICKY;
    }
}
