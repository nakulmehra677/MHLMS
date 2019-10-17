package com.mudrahome.mhlms.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.mudrahome.mhlms.R;
import com.mudrahome.mhlms.firebase.Firestore;
import com.mudrahome.mhlms.fragments.EditPhoneFragment;
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces;
import com.mudrahome.mhlms.managers.ProfileManager;
import com.mudrahome.mhlms.sharedPreferences.UserDataSharedPreference;

import java.util.Set;

public class ProfileDetailsActivity extends BaseActivity {

    private TextView profileName, profileEmail, profilePhone, profileLocation, profileDesignation;
    private Button button;

    private UserDataSharedPreference preference;
    private String userDesignation = "";
    private String userlocation = "";
    private String strContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileLocation = findViewById(R.id.profileLocation);
        profilePhone = findViewById(R.id.profileContact);
        profileDesignation = findViewById(R.id.profileDesignation);

        button = findViewById(R.id.edit_number);

        preference = new UserDataSharedPreference(this);
        String userType;
        userType = preference.getUserType();

        if (userType.equals(getString(R.string.admin)))
            userDesignation = getString(R.string.admin);
        else if (userType.equals(getString(R.string.telecaller)))
            userDesignation = getString(R.string.telecaller);
        else if (userType.equals(getString(R.string.business_associate)))
            userDesignation = getString(R.string.business_associate);
        else if (userType.equals(getString(R.string.teleassigner)))
            userDesignation = "Caller";
        else
            userDesignation = getString(R.string.salesman);

        Set<String> locationset;
        locationset = preference.getLocation();

        for (String s : locationset) {
            if (userlocation.equals("")) {
                userlocation = s;
            } else {
                userlocation += "," + s;
            }
        }

        profileName.setText(preference.getUserName());
        profileEmail.setText(preference.getUserEmail());
        profileDesignation.setText(userDesignation);
        profilePhone.setText(preference.getContactNumber());
        profileLocation.setText(userlocation);

        button.setOnClickListener(view -> EditPhoneFragment.newInstance(preference.getContactNumber(), number -> {
            Firestore firestore = new Firestore();
            ProfileManager manager = new ProfileManager();
            strContact = number;
            firestore.updateUserDetails(new FirestoreInterfaces.OnUpdateUser() {
                @Override
                public void onSuccess() {
                    preference.setContactNumber(strContact);
                    profilePhone.setText(strContact);
                    showToastMessage(R.string.updated);
                }

                @Override
                public void onFail() {
                    showToastMessage(R.string.update_fail);
                }
            }, number, manager.getuId());
        }).show(getSupportFragmentManager(), "promo"));
    }
}