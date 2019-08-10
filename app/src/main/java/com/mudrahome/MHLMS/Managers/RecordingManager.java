package com.mudrahome.MHLMS.Managers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.mudrahome.MHLMS.Firebase.Storage;

import java.io.IOException;

public class RecordingManager {

    private Context context;

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String fileName = null;

    private MediaRecorder recorder = null;

    private MediaPlayer player = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    public RecordingManager(Context context) {
        this.context = context;
        // Record to the external cache directory for visibility
        fileName = context.getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";
    }

    public void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        Thread t = new Thread() {
            public void run() {
                try {
                    recorder.prepare();
                    recorder.start();
                    Log.d("CALLL", "started");
                } catch (
                        IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        };

        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        Log.d("CALLL", "stopped");
    }

    public void uploadRecording(){
        Storage storage = new Storage();
        storage.uploadRecording(fileName);
    }
}
