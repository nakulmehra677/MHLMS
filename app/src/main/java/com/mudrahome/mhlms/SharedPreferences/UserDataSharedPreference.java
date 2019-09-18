package com.mudrahome.mhlms.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.mudrahome.mhlms.models.UserDetails;
import com.mudrahome.mhlms.R;

import java.util.HashSet;
import java.util.Set;

public class UserDataSharedPreference {
    private SharedPreferences preferences;
    private Context context;
    private String userdetails;
    private String email;
    private SharedPreferences.Editor editor;

    public UserDataSharedPreference(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(
                context.getString(R.string.SH_user_details),
                AppCompatActivity.MODE_PRIVATE);
    }

    public void setUserDetails(UserDetails details) {
        editor = preferences.edit();

        editor.putString(context.getString(R.string.SH_user_name), details.getUserName());
        editor.putString(context.getString(R.string.SH_user_key), details.getKey());
        editor.putString(context.getString(R.string.SH_user_uid), details.getuId());
        editor.putString(context.getString(R.string.SH_user_number), details.getContactNumber());
        editor.putString(context.getString(R.string.SH_user_email), details.getMail());

        Set<String> location = new HashSet<String>();
        for (String l : details.getLocation().keySet()) {
            boolean yes = details.getLocation().get(l);
            if (yes) {
                location.add(l);
            }
        }

        editor.putStringSet(context.getString(R.string.SH_user_location), location);

        Set<String> userType = new HashSet<String>();
        userType.addAll(details.getUserType());
        editor.putStringSet(context.getString(R.string.SH_user_type), userType);

        editor.commit();
    }

    public void setContactNumber(String number) {
        editor = preferences.edit();
        editor.putString(context.getString(R.string.SH_user_number), number);
        editor.commit();
    }

    public Set<String> getLocation() {

        Set<String> locationset = new HashSet<String>();
        locationset.add("null");
        return preferences.getStringSet(context.getString(R.string.SH_user_location), locationset);
    }

    public String getContactNumber() {
        return preferences.getString(context.getString(R.string.SH_user_number), "null");
    }

    public String getUserUid() {
        return preferences.getString(context.getString(R.string.SH_user_uid), "null");
    }

    public String getUserEmail() {
        return preferences.getString(context.getString(R.string.SH_user_email), "abc@gmail.com");
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
