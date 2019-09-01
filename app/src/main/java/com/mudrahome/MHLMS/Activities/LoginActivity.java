package com.mudrahome.MHLMS.Activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mudrahome.MHLMS.Firebase.Authentication;
import com.mudrahome.MHLMS.Firebase.Firestore;
import com.mudrahome.MHLMS.Fragments.MobileFragment;
import com.mudrahome.MHLMS.Interfaces.OnGetUserDetails;
import com.mudrahome.MHLMS.Interfaces.OnUpdateUser;
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

import java.util.concurrent.TimeUnit;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class LoginActivity extends BaseActivity {

    private EditText mail, password;
    private UserDetails currentUserDetails;
    private ScrollView scrollView;

    private Authentication authentication;
    private Firestore firestore;
    private ProfileManager profileManager;
    private String contactNumber;

    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        showProgressDialog("Loading..", this);

        scrollView = findViewById(R.id.scrollLayout);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);

        authentication = new Authentication(this);
        firestore = new Firestore(this);
        profileManager = new ProfileManager();

        if (isNetworkConnected()) {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                checkLogin();
            } else
                checkUpdate();
        } else
            showToastMessage(R.string.no_internet);
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
        profileManager = new ProfileManager();
        if (profileManager.checkUserExist()) {
            firestore.getUsers(onGetUserDetails(), profileManager.getuId());
        } else {
            if (progress.isShowing())
                dismissProgressDialog();
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    private OnGetUserDetails onGetUserDetails() {
        return new OnGetUserDetails() {
            @Override
            public void onSuccess(UserDetails userDetails) {
                String strDeviceToken = FirebaseInstanceId.getInstance().getToken();

                userDetails.setDeviceToken(strDeviceToken);
                firestore.setCurrentDeviceToken(strDeviceToken, profileManager.getuId());

                currentUserDetails = userDetails;

                UserDataSharedPreference preference = new UserDataSharedPreference(LoginActivity.this);
                preference.setUserDetails(currentUserDetails);

                dismissProgressDialog();
                showToastMessage(R.string.logged_in);

                if (currentUserDetails.getContactNumber() == null ||
                        currentUserDetails.getContactNumber().isEmpty()) {
                    openMobileFragment();
                } else
                    startLeadsPage();
            }

            @Override
            public void fail() {
                profileManager.signOut();
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
                    firestore.updateUserDetails(new OnUpdateUser() {
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
                scrollView.setVisibility(View.VISIBLE);
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
                    if (progress.isShowing())
                        progress.dismiss();
                    Log.i("UPDATE", "YES");
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
                    Log.i("UPDATE", "NO");
                    checkLogin();
                }
            }
        });
    }

    private void isUpdating() {
        final AppUpdateManager manager = AppUpdateManagerFactory.create(this);

        Task<AppUpdateInfo> appUpdateInfoTask = manager.getAppUpdateInfo();

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
                }
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
//    }
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
//            firestore.updateUserDetails(new OnUpdateUser() {
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