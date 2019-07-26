package com.development.mhleadmanagementsystemdev.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseAuthenticationHelper;
import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchDeviceTokenListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserDetailsByUId;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserDetailsListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnSetCurrentDeviceTokenListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUserLoginListener;
import com.development.mhleadmanagementsystemdev.Managers.ProfileManager;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.development.mhleadmanagementsystemdev.Services.ReminderService;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends BaseActivity {

    private EditText mail, password;
    private Button loginButton;
    private String strMail, strPassword;
    private UserDetails currentUserDetails;
    private ScrollView scrollView;

    private FirebaseAuthenticationHelper firebaseAuthenticationHelper;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private ProfileManager profileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //startService(new Intent(this, ReminderService.class));

        showProgressDialog("Loading..", this);

        scrollView = findViewById(R.id.scrollLayout);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);

        firebaseAuthenticationHelper = new FirebaseAuthenticationHelper(this);
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
        profileManager = new ProfileManager();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(LoginActivity.this);
                if (isNetworkConnected()) {
                    getDetail();
                    if (!strMail.isEmpty() && !strPassword.isEmpty()) {
                        showProgressDialog("Loading..", LoginActivity.this);
                        firebaseAuthenticationHelper.loginUser(onUserLoginListener(), strMail, strPassword);
                    } else
                        showToastMessage(R.string.fill_all_fields);

                } else
                    showToastMessage(R.string.no_internet);
            }
        });
    }

    private void checkLogin() {
        if (profileManager.checkUserExist()) {
            firebaseDatabaseHelper.getUsersByUId(onFetchUserDetailsByUId(), profileManager.getuId());
        } else {
            dismissProgressDialog();
            scrollView.setVisibility(View.VISIBLE);
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
                Log.i("UID", uId);
                profileManager = new ProfileManager();
                firebaseDatabaseHelper.getUsersByUId(onFetchUserDetailsByUId(), uId);
            }

            @Override
            public void onFailer() {
                dismissProgressDialog();
                showToastMessage(R.string.authentication_failed);
            }
        };
    }

    private OnFetchUserDetailsByUId onFetchUserDetailsByUId() {
        return new OnFetchUserDetailsByUId() {
            @Override
            public void onSuccess(UserDetails userDetails) {
                String strDeviceToken = FirebaseInstanceId.getInstance().getToken();

                if (!userDetails.getDeviceToken().equals(strDeviceToken)) {
                    userDetails.setDeviceToken(strDeviceToken);

                    firebaseDatabaseHelper.setCurrentDeviceToken(
                            strDeviceToken, profileManager.getuId());
                }

                currentUserDetails = userDetails;

                storeInSharedPrefernces();
                dismissProgressDialog();
                showToastMessage(R.string.logged_in);

                startActivityForResult(new Intent(
                        LoginActivity.this, LeadsListActivity.class), 101);
            }

            @Override
            public void fail() {
                profileManager.signOut();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNetworkConnected()) {
            checkUpdate();
        } else {
            showToastMessage(R.string.no_internet);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (resultCode == 101) {
            if (data.getBooleanExtra("loggedIn", true))
                finish();
            else
                scrollView.setVisibility(View.VISIBLE);
        }
        if (requestCode == 17362) {
            if (resultCode != RESULT_OK) {
                finish();
            }
        }
    }

    private void storeInSharedPrefernces() {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferenceUserDetails, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(sharedPreferenceUserName, currentUserDetails.getUserName());
        editor.putString(sharedPreferenceUserType, currentUserDetails.getUserType());
        editor.putString(sharedPreferenceUserLocation, currentUserDetails.getLocation());
        editor.putString(sharedPreferenceUserKey, currentUserDetails.getKey());
        editor.putString(sharedPreferenceUserUId, currentUserDetails.getuId());

        editor.commit();
    }

    private void checkUpdate() {
        final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    if (progress.isShowing())
                        progress.dismiss();
                    Log.i("UPDATE", "YESS");
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                LoginActivity.this,
                                17362);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("UPDATE", "NOO");
                    checkLogin();
                }
            }
        });
    }
}