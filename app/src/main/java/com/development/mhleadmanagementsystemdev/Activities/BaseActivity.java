package com.development.mhleadmanagementsystemdev.Activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    //protected boolean isAdmin = false;

    protected boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    protected void showToastMessage(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
