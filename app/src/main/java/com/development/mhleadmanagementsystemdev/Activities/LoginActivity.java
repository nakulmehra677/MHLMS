package com.development.mhleadmanagementsystemdev.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseAuthenticationHelper;
import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.OnCheckAdminListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUserLoginListener;
import com.development.mhleadmanagementsystemdev.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends BaseActivity {

    private EditText mail, password;
    private Button loginButton;
    private String strMail, strPassword;
    //private TextView signUp;

    private FirebaseAuthenticationHelper firebaseAuthenticationHelper;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        //signUp = findViewById(R.id.sign_up);

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

        /*signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CreateUserActivity.class));
                finish();
            }
        });*/
    }

    private void getDetail() {
        strMail = mail.getText().toString();
        strPassword = password.getText().toString();
    }

    private OnUserLoginListener onUserLoginListener() {
        return new OnUserLoginListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                showToastMessage(R.string.logged_in);

                Intent intent = new Intent();
                intent.putExtra("MESSAGE", true);
                setResult(1, intent);
                finish();
                //firebaseDatabaseHelper.checkAdmin(onCheckAdminListener(), strMail);
            }

            @Override
            public void onFailer() {
                progress.dismiss();
                showToastMessage(R.string.authentication_failed);
            }
        };
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("MESSAGE", false);
        setResult(1, intent);
        finish();
    }

    /*private OnCheckAdminListener onCheckAdminListener() {
        return new OnCheckAdminListener() {
            @Override
            public void onSuccess(boolean a) {
                progress.dismiss();
                showToastMessage(R.string.logged_in);
            }

            @Override
            public void onFailer() {
                progress.dismiss();
            }
        };
    }*/
}