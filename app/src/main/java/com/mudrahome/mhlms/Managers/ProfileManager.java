package com.mudrahome.mhlms.Managers;

import com.mudrahome.mhlms.Activities.BaseActivity;
import com.mudrahome.mhlms.Firebase.Firestore;
import com.mudrahome.mhlms.models.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

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

    /*public List<String> getCurrentUserType() {
        return currentUserDetails.getUserType();
    }*/

    public List<String> getCurrentUserType() {
        return currentUserDetails.getUserType();
    }

    public String getuId() {
        return uId;
    }

    public void signOut() {
        mAuth.signOut();
    }
}
