package com.mudrahome.MHLMS.Activities;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.cardview.widget.CardView;

import com.mudrahome.MHLMS.Firebase.Authentication;
import com.mudrahome.MHLMS.Firebase.Firestore;
import com.mudrahome.MHLMS.Interfaces.FirestoreInterfaces;
import com.mudrahome.MHLMS.Interfaces.OnUserLogin;
import com.mudrahome.MHLMS.Managers.ProfileManager;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.R;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mudrahome.MHLMS.SharedPreferences.UserDataSharedPreference;


import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class LoginActivity extends BaseActivity {

    private EditText mail, password;
    private UserDetails currentUserDetails;
    private CardView cardView;

    private Authentication authentication;
    private com.mudrahome.MHLMS.Firebase.Firestore firestore;
    private ProfileManager profileManager;
    private String contactNumber;
    SharedPreferences sharedPreferences;

    private boolean newLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isNetworkConnected()) {
            sharedPreferences = getApplicationContext().getSharedPreferences("com.mudrahome.MHLMS", MODE_PRIVATE);
            cardView = findViewById(R.id.card);
            mail = findViewById(R.id.mail);
            password = findViewById(R.id.password);

            authentication = new Authentication(this);
            firestore = new Firestore(this);
            profileManager = new ProfileManager();
        } else {
            showToastMessage(R.string.no_internet);
            finish();
        }

    }

    public void loginButton(View view) {
        hideKeyboard(LoginActivity.this);

        if (isNetworkConnected()) {
            String strMail = mail.getText().toString();
            String strPassword = password.getText().toString();

            if (!strMail.isEmpty() && !strPassword.isEmpty()) {
                showProgressDialog("Loading..", LoginActivity.this);

                newLogin = true;
                authentication.loginUser(new OnUserLogin() {
                    @Override
                    public void onSuccess(String uId) {
                        profileManager = new ProfileManager();
                        firestore.getUsers(onGetUserDetails(), uId);
                    }

                    @Override
                    public void onFailer() {
                        if (progress.isShowing())
                            dismissProgressDialog();
                        showToastMessage(R.string.authentication_failed);
                    }
                }, strMail, strPassword);

            } else
                showToastMessage(R.string.fill_all_fields);

        } else
            showToastMessage(R.string.no_internet);
    }

    private void checkLogin() {
        if (profileManager.checkUserExist()) {
            firestore.getUsers(onGetUserDetails(), profileManager.getuId());
        } else {
            cardView.setVisibility(View.VISIBLE);
        }
    }

    private FirestoreInterfaces.OnGetUserDetails onGetUserDetails() {
        return new FirestoreInterfaces.OnGetUserDetails() {
            @Override
            public void onSuccess(UserDetails userDetails) {
                String strDeviceToken = FirebaseInstanceId.getInstance().getToken();

                userDetails.setDeviceToken(strDeviceToken);
                firestore.setCurrentDeviceToken(strDeviceToken, profileManager.getuId());

                currentUserDetails = userDetails;

                UserDataSharedPreference preference = new UserDataSharedPreference(LoginActivity.this);
                preference.setUserDetails(currentUserDetails);

                if (newLogin) {
                    dismissProgressDialog();
                    newLogin = false;
                    showToastMessage(R.string.logged_in);
                }
                startLeadsPage();
            }

            @Override
            public void fail() {
                profileManager.signOut();
                cardView.setVisibility(View.VISIBLE);
                firestore.setCurrentDeviceToken("", profileManager.getuId());
            }
        };
    }

    private void startLeadsPage() {
        startActivityForResult(new Intent(
                LoginActivity.this, LeadListActivity.class), 101);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            isUpdating();
        else {
            checkLogin();
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
                cardView.setVisibility(View.VISIBLE);
        }
        if (requestCode == 17362) {
            if (resultCode != RESULT_OK) {
                finish();
            }
        }
    }

    private void checkUpdate() {
        final AppUpdateManager manager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = manager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    try {
                        manager.startUpdateFlowForResult(
                                appUpdateInfo,
                                IMMEDIATE,
                                LoginActivity.this,
                                17362);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {
                    checkLogin();
                }
            }
        });
    }

    private void isUpdating() {
        final AppUpdateManager manager = AppUpdateManagerFactory.create(this);

        manager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() ==
                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        manager.startUpdateFlowForResult(
                                appUpdateInfo,
                                IMMEDIATE,
                                LoginActivity.this,
                                17362);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else
                    checkUpdate();
            }
        });
    }

//    private void sendVerificationCode() {
//        showProgressDialog("Waiting for otp, wait for a while. Don't close the app.", this);
//        hideKeyboard(this);
//        startCountdown(60);
//
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                contactNumber,
//                60,
//                TimeUnit.SECONDS,
//                this,
//                mCallbacks);
//
//    }showToastMessage
//
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
//            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//        @Override
//        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//            Log.d("timer", "sent : " + s);
//        }
//
//        @Override
//        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//            String code = phoneAuthCredential.getSmsCode();
//            Log.d("Timer", "verificationCompleted");
//            flag = true;
//
//            currentUserDetails.setContactNumber(contactNumber);
//            firestore.updateUserDetails(new FirestoreInterfaces.OnUpdateUser() {
//                @Override
//                public void onSuccess() {
//                    startLeadsPage();
//                }
//
//                @Override
//                public void onFail() {
//                    openMobileFragment();
//                }
//            }, currentUserDetails);
//        }
//
//        @Override
//        public void onVerificationFailed(FirebaseException e) {
//            Log.d("timer", "fail");
//            flag = true;
//            openMobileFragment();
//        }
//    };
//
//    protected void startCountdown(final int i) {
//        flag = false;
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                for (int j = i; j > 0; j--) {
//                    if (!flag) {
//                        try {
//                            Thread.sleep(1000);
//                            Log.d("Timer", " " + j);
//                        } catch (InterruptedException e) {
//                            System.out.println("got interrupted!");
//                        }
//                    } else
//                        break;
//                }
//
//                dismissProgressDialog();
//                if (!flag) {
//                    profileManager.signOut();
//                    scrollView.setVisibility(View.VISIBLE);
//                    showToastMessage(R.string.otp_verification_failed);
//                }
//            }
//        }).start();
//    }
}