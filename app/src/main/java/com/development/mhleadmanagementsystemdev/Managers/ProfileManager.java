package com.development.mhleadmanagementsystemdev.Managers;

import com.development.mhleadmanagementsystemdev.Activities.BaseActivity;
import com.development.mhleadmanagementsystemdev.Firebase.Firestore;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileManager extends BaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Firestore firestore;
    private UserDetails currentUserDetails;
    private String uId;

    public ProfileManager() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uId = mAuth.getUid();
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

    public String getCurrentUserType() {
        return currentUserDetails.getUserType();
    }

    public String getuId() {
        return uId;
    }

    public void signOut() {
        mAuth.signOut();
    }
}
