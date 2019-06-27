package com.development.mhleadmanagementsystemdev.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseAuthenticationHelper;
import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserDetailsListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUserLoginListener;
import com.development.mhleadmanagementsystemdev.Managers.ProfileManager;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.development.mhleadmanagementsystemdev.R;

public class LoginActivity extends BaseActivity {

    private EditText mail, password;
    private Button loginButton;
    private String strMail, strPassword;

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

        firebaseAuthenticationHelper = new FirebaseAuthenticationHelper(this);
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                startActivity(new Intent(LoginActivity.this, LeadsListActivity.class));
                finish();
            }

            @Override
            public void onFailer() {
                progress.dismiss();
                showToastMessage(R.string.authentication_failed);
            }
        };
    }

    /*@Override
    public void onBackPressed() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent = new Intent();

        if (currentUser == null) {
            intent.putExtra("MESSAGE", false);
            Log.i("MMMM", "false");
        } else {
            intent.putExtra("MESSAGE", true);
            Log.i("MMMM", "true");
        }
        setResult(1, intent);
        finish();
    }*/

    private OnFetchUserDetailsListener onFetchUserDetailsListener() {
        return new OnFetchUserDetailsListener() {
            @Override
            public void onSuccess(UserDetails userDetails) {
                SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferenceUserDetails, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(sharedPreferenceUserName, userDetails.getUserName());
                editor.putString(sharedPreferenceUserType, userDetails.getUserType());
                editor.putString(sharedPreferenceUserLocation, userDetails.getLocation());
                editor.putString(sharedPreferenceUserKey, userDetails.getKey());

                editor.commit();

                progress.dismiss();
                showToastMessage(R.string.logged_in);

                onBackPressed();
            }
        };
    }
}