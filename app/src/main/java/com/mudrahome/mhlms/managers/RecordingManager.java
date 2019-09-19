//package com.mudrahome.MHLMS.Managers;
//
//import android.content.Context;
//import android.media.MediaPlayer;
//import android.media.MediaRecorder;
//import android.util.Log;
//
//
//import com.mudrahome.MHLMS.Firebase.Storage;
//
//import java.io.IOException;
//
//public class RecordingManager {
//    private Context context;
//    private String fileName = null;
//    private MediaRecorder recorder = null;
//
//    public RecordingManager(Context context) {
//        this.context = context;
//        fileName = context.getExternalCacheDir().getAbsolutePath();
//        fileName += "/audiorecordtest.3gp";
//    }
//
//    private RecordingManager() {
//    }
//
//    public void startRecording() {
////        RecordingManager manager = new RecordingManager();
////        Thread t1 = new Thread(manager);
////        t1.start();
////    }
////
////    @Override
////    public void run() {
//        recorder = new MediaRecorder();
//        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        recorder.setOutputFile(fileName);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//        try {
//            recorder.prepare();
//            recorder.start();
//            Log.d("Status", "Recording started");
//        } catch (IOException e) {
//            Log.d("Status", "Recording failed to start");
//        }
//    }
//
//    public void stopRecording() {
//        recorder.stop();
//        recorder.release();
//        recorder = null;
//        Log.d("Status", "Recording stopped");
//        MediaPlayer player = new MediaPlayer();
//        try {
//            player.setDataSource(fileName);
//            player.prepare();
//            player.start();
//            Log.d("Status", "player started");
//        } catch (IOException e) {
//            Log.e("Status", "player failed to start");
//        }
//    }
//
//    public void uploadRecording() {
//        Storage storage = new Storage();
//        storage.uploadRecording(fileName);
//    }
//}
