package com.mudrahome.mhlms.managers;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;
import com.mudrahome.mhlms.activities.BaseActivity;
import com.mudrahome.mhlms.firebase.Firestore;
import com.mudrahome.mhlms.model.UserDetails;
import com.mudrahome.mhlms.sharedPreferences.ProfileSP;

import java.util.List;

public class ProfileManager extends BaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Firestore firestore;
    private UserDetails currentUserDetails;
    private String uId;
    private ProfileSP profileSP;

    public static String TOKEN;

    public ProfileManager() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uId = mAuth.getUid();
        firestore = new Firestore();
    }

    public ProfileManager(Context context) {
        profileSP = new ProfileSP(context);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uId = mAuth.getUid();
        firestore = new Firestore();
    }

    public UserDetails getCurrentUserDetails() {
        return currentUserDetails;
    }

    public void setCurrentUserDetails(UserDetails currentUserDetails) {
        this.currentUserDetails = currentUserDetails;
    }

    public boolean checkUserExist() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
            return false;
        else
            return true;
    }

    public void setPreferences(UserDetails user) {
        profileSP.setUserDetails(user);
    }

    public String getSPUserType(){
        return profileSP.getUserType();
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public List<String> getCurrentUserType() {
        return currentUserDetails.getUserType();
    }

    public String getuId() {
        return uId;
    }

    public void signOut() {
        mAuth.signOut();
    }

    public void updateDeviceToken(String s) {
        if (currentUser != null) {
            firestore.updateDeviceToken(mAuth.getUid(), s);
        }
        TOKEN = s;
    }
}
