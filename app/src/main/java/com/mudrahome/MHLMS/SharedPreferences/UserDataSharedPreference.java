package com.mudrahome.MHLMS.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDataSharedPreference {
    private SharedPreferences preferences;
    private Context context;
    private String userdetails;

    public UserDataSharedPreference(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(
                context.getString(R.string.SH_user_details),
                AppCompatActivity.MODE_PRIVATE);
    }

    public void setUserDetails(UserDetails details) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(context.getString(R.string.SH_user_name), details.getUserName());
        editor.putString(context.getString(R.string.SH_user_location), details.getLocation());
        editor.putString(context.getString(R.string.SH_user_key), details.getKey());
        editor.putString(context.getString(R.string.SH_user_uid), details.getuId());
        editor.putString(context.getString(R.string.SH_user_number), details.getContactNumber());

        Set<String> set = new HashSet<String>();
        set.addAll(details.getUserType());
        editor.putStringSet(context.getString(R.string.SH_user_type), set);

        editor.commit();
    }

    public String getUserName() {
        return preferences.getString(context.getString(R.string.SH_user_name), "");
    }

    public Set<String> getUserType() {
        Set<String> set = new HashSet<String>();
        set.add("Salesman");
        return preferences.getStringSet(context.getString(R.string.SH_user_type), set);
    }
}
