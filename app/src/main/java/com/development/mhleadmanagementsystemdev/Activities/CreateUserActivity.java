package com.development.mhleadmanagementsystemdev.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.CountNoOfNodesInDatabaseListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUploadNewUserDetailsListener;
import com.development.mhleadmanagementsystemdev.Interfaces.SignUpAccountListener;
import com.development.mhleadmanagementsystemdev.Models.TeleCallerDetails;
import com.development.mhleadmanagementsystemdev.R;

public class CreateUserActivity extends BaseActivity {

    private EditText mail, password, confirmPassword, userName;
    private Button signInButton;
    private String strMail, strPassword, strUserName, strConfirmPassword, strUserType = null;
    private ProgressDialog progress;
    private String UID;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        userName = findViewById(R.id.user_name);
        signInButton = findViewById(R.id.sign_in);

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    getDetail();
                    if (!strMail.isEmpty() && !strPassword.isEmpty() &&
                            !strConfirmPassword.isEmpty() && !strUserName.isEmpty() && strUserType != null) {

                        if (strPassword.equals(strConfirmPassword)) {
                            progress = new ProgressDialog(CreateUserActivity.this);
                            progress.setMessage("Signing Up..");
                            progress.setCancelable(false);
                            progress.setCanceledOnTouchOutside(false);
                            progress.show();

                            firebaseDatabaseHelper.signUpAccount(onSignUpAccountListener(),
                                    strMail, strPassword, strUserName);

                        } else
                            showToastMessage(R.string.passwords_not_matching);
                    } else
                        showToastMessage(R.string.fill_all_fields);
                } else
                    showToastMessage(R.string.no_internet);
            }
        });
    }

    public void onRadioButtonSignUpClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.telecaller:
                if (checked) {
                    strUserType = "Telecaller";
                    break;
                }
            case R.id.sales_person:
                if (checked) {
                    strUserType = "Sales person";
                    break;
                }
        }
    }

    private void getDetail() {
        strMail = mail.getText().toString();
        strPassword = password.getText().toString();
        strConfirmPassword = confirmPassword.getText().toString();
        strUserName = userName.getText().toString();
    }

    private SignUpAccountListener onSignUpAccountListener() {
        return new SignUpAccountListener() {
            @Override
            public void signUpSuccessful(String uId) {
                UID = uId;
                //firebaseDatabaseHelper.countNoOfNodes(onCountNoOfNodesInDatabase(), "userList");
            }

            @Override
            public void signUpFailed() {
                showToastMessage(R.string.authentication_failed);
                progress.dismiss();
            }
        };
    }

    private CountNoOfNodesInDatabaseListener onCountNoOfNodesInDatabase() {
        return new CountNoOfNodesInDatabaseListener() {

            @Override
            public void onFetched(long nodes) {
                Log.i("No of Nodes", "About to upload details");
                TeleCallerDetails teleCallerDetails = new TeleCallerDetails(UID, strUserName, strMail, "erfr");

                firebaseDatabaseHelper.uploadNewUserDetails(uploadNewUserDetailsListener(), teleCallerDetails, nodes);
            }

            @Override
            public void failedToFetch() {
                progress.dismiss();
            }
        };
    }

    private OnUploadNewUserDetailsListener uploadNewUserDetailsListener() {
        return new OnUploadNewUserDetailsListener() {
            @Override
            public void dataUploaded() {
                progress.dismiss();

                showToastMessage(R.string.user_created);
                startActivity(new Intent(CreateUserActivity.this, LeadsListActivity.class));
                finish();
            }
        };
    }
}