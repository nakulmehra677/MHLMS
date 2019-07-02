package com.development.mhleadmanagementsystemdev.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    protected ProgressDialog progress;
    public String sharedPreferenceUserDetails = "shared_preference_user_details";
    public String sharedPreferenceUserName = "shared_preference_user_name";
    public String sharedPreferenceUserType = "shared_preference_user_type";
    public String sharedPreferenceUserLocation = "shared_preference_user_location";
    public String sharedPreferenceUserKey = "shared_preference_user_key";
    public String sharedPreferenceUserUId = "shared_preference_user_uid";

    protected String telecallerUser = "Telecaller";
    protected String salesmanUser = "Salesman";

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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
