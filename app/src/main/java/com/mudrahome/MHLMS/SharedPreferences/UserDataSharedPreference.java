package com.mudrahome.MHLMS.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.R;

public class UserDataSharedPreference {
    private SharedPreferences preferences;
    private Context context;

    public UserDataSharedPreference(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(
                context.getString(R.string.SH_user_details),
                AppCompatActivity.MODE_PRIVATE);
    }

    public void setUserDetails(UserDetails details) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.SH_user_name), details.getUserName());
        editor.putString(context.getString(R.string.SH_user_type), details.getUserType());
        editor.putString(context.getString(R.string.SH_user_location), details.getLocation());
        editor.putString(context.getString(R.string.SH_user_key), details.getKey());
        editor.putString(context.getString(R.string.SH_user_uid), details.getuId());
        editor.putString(context.getString(R.string.SH_user_number), details.getContactNumber());

        editor.commit();
    }
}
