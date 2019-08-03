package com.development.mhleadmanagementsystemdev.Managers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;

public class PermissionManager {
    private Context context;

    public PermissionManager(Context context) {
        this.context = context;
    }

    public boolean checkCallPhone() {
        return ContextCompat.checkSelfPermission(context, CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkReadPhoneState() {
        return ContextCompat.checkSelfPermission(context, READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCallPhone() {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{CALL_PHONE}, 1);

    }

    public void requestReadPhoneState() {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{READ_PHONE_STATE}, 1);
    }

    public boolean checkRecordAudio() {
        return ContextCompat.checkSelfPermission(context, RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestRecordAudio() {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{RECORD_AUDIO},
                1);
    }
}
