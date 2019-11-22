package com.mudrahome.mhlms.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mudrahome.mhlms.BuildConfig;
import com.mudrahome.mhlms.R;
import com.mudrahome.mhlms.firebase.Authentication;
import com.mudrahome.mhlms.firebase.Firestore;
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces;
import com.mudrahome.mhlms.interfaces.OnUserLogin;
import com.mudrahome.mhlms.managers.PermissionManager;
import com.mudrahome.mhlms.managers.ProfileManager;
import com.mudrahome.mhlms.model.UserDetails;
import com.mudrahome.mhlms.sharedPreferences.UserDataSharedPreference;

import org.jsoup.Jsoup;

import java.io.IOException;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class LoginActivity extends BaseActivity {

    private PermissionManager permissionManager;
    private EditText mail, password;
    private UserDetails currentUserDetails;
    private CardView cardView;

    private Authentication authentication;
    private Firestore firestore;
    private ProfileManager profileManager;
    private String contactNumber;
    private boolean newLogin = false;

    AppUpdateManager appUpdateManager;
    Task<AppUpdateInfo> appUpdateInfoTask;

    private int UPDATE_REQUEST_CODE = 9898;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isNetworkConnected()) {
            appUpdateManager = AppUpdateManagerFactory.create(this);
            appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            permissionManager = new PermissionManager(LoginActivity.this);
            checkUpdate();

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
                cardView.setVisibility(View.GONE);
                String strDeviceToken = FirebaseInstanceId.getInstance().getToken();

                userDetails.setDeviceToken(strDeviceToken);
                if(strDeviceToken !=null )
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
        /*startActivityForResult(new Intent(
                LoginActivity.this, LeadListActivity.class), 101);*/
        Intent intent = new Intent(LoginActivity.this, LeadListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isUpdating();
        } else {
            *//*checkPermission();*//*
            checkLogin();
        }*/
        checkUpdate();
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (resultCode == 101) {
            Log.d("profileManager", "onBackPressed: 1" + data.hasExtra("loggedIn"));
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


    }*/

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }


    private void checkUpdate() {
        Log.d("UpdateAvailable","CheckUpdate");
       VersionChecker checker = new VersionChecker();
       try{
           String latestVersion = checker.execute().get();
           if(!BuildConfig.VERSION_NAME.matches(latestVersion)){
               showUpdateDialog();
               Toast.makeText(this, "UpdateAvailable", Toast.LENGTH_LONG).show();
           }else{
               Log.d("UpdateAvailable","else Block "+latestVersion);
               checkLogin();
           }
       }catch (Exception e){
           e.printStackTrace();
       }

      /*  appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    private void isUpdating() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(
                appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability()
                            == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        try {
                            appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    IMMEDIATE,
                                    this,
                                    UPDATE_REQUEST_CODE);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("checkUpdate", "not available");
                        /*checkPermission();*/
                        checkLogin();
                    }
                });
    }

    public class VersionChecker extends AsyncTask<String, String, String> {

        private String newVersion;

        @Override
        protected String doInBackground(String... params) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=com.development.mhleadmanagementsystemdev&hl=en_IN")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".IxB2fe .hAyfc:nth-child(4) .htlgb span")
                        .get(0)
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("TAG", "doInBackground: " + newVersion);
            return newVersion;
        }
    }

    public void showUpdateDialog(){
        new AlertDialog.Builder(LoginActivity.this, R.style.AppCompatAlertDialogStyle)
                .setTitle("Update Available")
                .setMessage("It looks like you are missing out some new features, kindly Update app to get a better experience")
                .setPositiveButton("Update now", (dialog, which) -> {
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                })
                .setCancelable(false)
                .show();
    }
}