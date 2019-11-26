package com.mudrahome.mhlms.managers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mudrahome.mhlms.activities.BaseActivity;
import com.mudrahome.mhlms.firebase.Firestore;
import com.mudrahome.mhlms.model.UserDetails;

import java.util.List;

public class ProfileManager extends BaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Firestore firestore;
    private UserDetails currentUserDetails;
    private String uId;

    public static String TOKEN;

    public ProfileManager() {
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
