package com.mudrahome.MHLMS.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.R;

import java.util.HashSet;

public class UserDataSharedPreference {
    private SharedPreferences preferences;
    private Context context;
    HashSet<String> userdetails = new HashSet<>();

    public UserDataSharedPreference(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(
                context.getString(R.string.SH_user_details),
                AppCompatActivity.MODE_PRIVATE);
    }

    public void setUserDetails(UserDetails details) {
        SharedPreferences.Editor editor = preferences.edit();

        for(int i =0; i <details.getUserType().size();i++){
            userdetails.add(details.getUserType().get(i));
        }

        editor.putString(context.getString(R.string.SH_user_name), details.getUserName());
        editor.putStringSet(context.getString(R.string.SH_user_type), userdetails);
        editor.putString(context.getString(R.string.SH_user_location), details.getLocation());
        editor.putString(context.getString(R.string.SH_user_key), details.getKey());
        editor.putString(context.getString(R.string.SH_user_uid), details.getuId());
        editor.putString(context.getString(R.string.SH_user_number), details.getContactNumber());

        editor.commit();
    }
}
