package com.development.mhleadmanagementsystemdev.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseAuthenticationHelper;
import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchDeviceTokenListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserDetailsListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUserLoginListener;
import com.development.mhleadmanagementsystemdev.Managers.ProfileManager;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends BaseActivity {

    private EditText mail, password;
    private Button loginButton;
    private String strMail, strPassword, localDeviceToken;

    private FirebaseAuthenticationHelper firebaseAuthenticationHelper;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private ProfileManager profileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);

        if (isNetworkConnected()) {

            firebaseAuthenticationHelper = new FirebaseAuthenticationHelper(this);
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            profileManager = new ProfileManager();

            if (profileManager.checkUserExist()) {
                showProgressDialog("Loading..", LoginActivity.this);
                firebaseAuthenticationHelper.checkDeviceToken(onFetchDeviceTokenListener());
            }


            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    hideKeyboard(LoginActivity.this);
                    if (isNetworkConnected()) {
                        getDetail();

                        if (!strMail.isEmpty() && !strPassword.isEmpty()) {
                            showProgressDialog("Logging in", LoginActivity.this);

                            firebaseAuthenticationHelper.loginUser(onUserLoginListener(), strMail, strPassword);
                        } else
                            showToastMessage(R.string.fill_all_fields);

                    } else
                        showToastMessage(R.string.no_internet);
                }
            });
        } else {
            showToastMessage(R.string.no_internet);
            finish();
        }
    }

    private void getDetail() {
        strMail = mail.getText().toString();
        strPassword = password.getText().toString();
    }

    private OnUserLoginListener onUserLoginListener() {
        return new OnUserLoginListener() {
            @Override
            public void onSuccess(String uId) {
                profileManager = new ProfileManager();
                firebaseDatabaseHelper.getUserDetails(onFetchUserDetailsListener(), uId);
            }

            @Override
            public void onFailer() {
                progress.dismiss();
                showToastMessage(R.string.authentication_failed);
            }
        };
    }

    private OnFetchDeviceTokenListener onFetchDeviceTokenListener() {
        return new OnFetchDeviceTokenListener() {
            @Override
            public void onFetch(String token) {
                localDeviceToken = token;
                firebaseDatabaseHelper.getUserDetails(onFetchUserDetailsListener(), profileManager.getuId());
            }
        };
    }

    private OnFetchUserDetailsListener onFetchUserDetailsListener() {
        return new OnFetchUserDetailsListener() {
            @Override
            public void onSuccess(UserDetails userDetails) {

                if (localDeviceToken.equals(userDetails.getDeviceToken()) ||
                        userDetails.getDeviceToken() == null) {

                    SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferenceUserDetails, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(sharedPreferenceUserName, userDetails.getUserName());
                    editor.putString(sharedPreferenceUserType, userDetails.getUserType());
                    editor.putString(sharedPreferenceUserLocation, userDetails.getLocation());
                    editor.putString(sharedPreferenceUserKey, userDetails.getKey());
                    editor.putString(sharedPreferenceUserUId, userDetails.getuId());

                    editor.commit();

                    if (progress.isShowing())
                        progress.dismiss();
                    showToastMessage(R.string.logged_in);
                    startActivityForResult(new Intent(LoginActivity.this, LeadsListActivity.class), 101);
                } else
                    profileManager.signOut();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (resultCode == 101)
            if (data.getBooleanExtra("loggedIn", true))
                finish();
    }
}