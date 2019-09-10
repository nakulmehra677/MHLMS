package com.mudrahome.MHLMS.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.mudrahome.MHLMS.Firebase.Firestore;
import com.mudrahome.MHLMS.Managers.ProfileManager;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.R;
import com.mudrahome.MHLMS.SharedPreferences.UserDataSharedPreference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileDetailsActivity extends AppCompatActivity {

    TextView profileName, profileEmail, profilePhone, profileLocation, profileDesignation;
    Firestore firestore;
    ProfileManager profileManager;
    UserDataSharedPreference userDataSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);


        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileLocation = findViewById(R.id.profileLocation);
        profilePhone = findViewById(R.id.profileContact);
        profileDesignation = findViewById(R.id.profileDesignation);


        userDataSharedPreference = new UserDataSharedPreference(ProfileDetailsActivity.this);

        Set<String> userType = new HashSet<>();
        userType = userDataSharedPreference.getUserType();

        String userDesignation = "";

        for (String s : userType) {

            if(userDesignation == ""){

                userDesignation = s;
            }else {
                userDesignation = ","+s;
            }

        }

        profileName.setText(userDataSharedPreference.getUserName());
        profileEmail.setText(userDataSharedPreference.getUserEmail());
        profileDesignation.setText(userDesignation);
        profilePhone.setText(userDataSharedPreference.getContactNumber());
        profileLocation.setText(userDataSharedPreference.getLocation());


       /* firestore.getUsers(new com.mudrahome.MHLMS.Interfaces.Firestore.OnGetUserDetails() {
            @Override
            public void onSuccess(UserDetails userDetails) {

                List<String> userType = new ArrayList<>();
                userType = userDetails.getUserType();

                String userDesignation = "";

                for (int i = 0; i < userType.size(); i++) {

                    if(userDesignation == ""){

                        userDesignation = userType.get(i);
                    }else {
                        userDesignation = ","+userType.get(i);
                    }

                }


                profileName.setText(userDetails.getUserName());
                profileDesignation.setText(userDesignation);
                profileEmail.setText(userDetails.getMail());
                profileLocation.setText(userDetails.getLocation());
                profilePhone.setText(userDetails.getContactNumber());

            }

            @Override
            public void fail() {

            }
        }, profileManager.getuId());*/


    }
}
