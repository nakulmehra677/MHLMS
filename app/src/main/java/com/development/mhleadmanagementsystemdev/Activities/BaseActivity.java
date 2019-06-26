package com.development.mhleadmanagementsystemdev.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    protected ProgressDialog progress;
    protected String sharedPreferenceUserDetails = "shared_preference_user_details";
    protected String sharedPreferenceUserName = "shared_preference_user_name";
    protected String sharedPreferenceUserType = "shared_preference_user_type";
    protected String sharedPreferenceUserLocation = "shared_preference_user_location";
    protected String sharedPreferenceUserKey = "shared_preference_user_key";

    //protected boolean isAdmin = false;

    protected boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    protected void showToastMessage(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void showProgressDialog(String message, Context context) {
        progress = new ProgressDialog(context);
        progress.setMessage(message);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }
}
