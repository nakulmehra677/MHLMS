package com.mudrahome.MHLMS.Activities;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mudrahome.MHLMS.BuildConfig;
import com.mudrahome.MHLMS.Firebase.Authentication;
import com.mudrahome.MHLMS.Fragments.LeadListFragment;
import com.mudrahome.MHLMS.Fragments.MobileFragment;
import com.mudrahome.MHLMS.Interfaces.Firestore;
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


import org.jsoup.Jsoup;

import java.io.IOException;

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

    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getApplicationContext().getSharedPreferences("com.mudrahome.MHLMS", MODE_PRIVATE);
        cardView = findViewById(R.id.card);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);

        authentication = new Authentication(this);
        firestore = new com.mudrahome.MHLMS.Firebase.Firestore(this);
        profileManager = new ProfileManager();

        checkUpdate();

    }

    public void loginButton(View view) {
        hideKeyboard(LoginActivity.this);

        if (isNetworkConnected()) {

            String strMail = mail.getText().toString();
            String strPassword = password.getText().toString();

            if (!strMail.isEmpty() && !strPassword.isEmpty()) {
                showProgressDialog("Loading..", LoginActivity.this);

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
        Log.d("log", "sdvvdfs");
        if (profileManager.checkUserExist()) {
            firestore.getUsers(onGetUserDetails(), profileManager.getuId());
        } else {
            cardView.setVisibility(View.VISIBLE);
        }
    }

    private Firestore.OnGetUserDetails onGetUserDetails() {
        return new Firestore.OnGetUserDetails() {
            @Override
            public void onSuccess(UserDetails userDetails) {
                String strDeviceToken = FirebaseInstanceId.getInstance().getToken();

                userDetails.setDeviceToken(strDeviceToken);
                firestore.setCurrentDeviceToken(strDeviceToken, profileManager.getuId());

                currentUserDetails = userDetails;

                UserDataSharedPreference preference = new UserDataSharedPreference(LoginActivity.this);
                preference.setUserDetails(currentUserDetails);

                showToastMessage(R.string.logged_in);
                startLeadsPage();
/*                if (currentUserDetails.getContactNumber() == null ||
                        currentUserDetails.getContactNumber().isEmpty()) {
                    openMobileFragment();
                } */
            }

            @Override
            public void fail() {
                Log.d("Flag", String.valueOf(flag));
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

    private void openMobileFragment() {
        MobileFragment.newInstance(new MobileFragment.OnNumberClickListener() {
            @Override
            public void onSubmitClicked(String number) {
                contactNumber = "+91" + number.trim();
                if (isNetworkConnected()) {
                    currentUserDetails.setContactNumber(contactNumber);
                    firestore.updateUserDetails(new Firestore.OnUpdateUser() {
                        @Override
                        public void onSuccess() {
                            startLeadsPage();
                        }

                        @Override
                        public void onFail() {
                            openMobileFragment();
                        }
                    }, currentUserDetails);
                } else {
                    showToastMessage(R.string.no_internet);
                    openMobileFragment();
                }
            }
        }).show(getSupportFragmentManager(), "promo");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNetworkConnected()) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                isUpdating();
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
                }
            }
        });
    }

    private void isUpdating() {
        final AppUpdateManager manager = AppUpdateManagerFactory.create(this);

        manager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability()
                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                    try {
                        manager.startUpdateFlowForResult(
                                appUpdateInfo,
                                IMMEDIATE,
                                LoginActivity.this,
                                17362);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }else
                    checkLogin();
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
//            firestore.updateUserDetails(new Firestore.OnUpdateUser() {
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