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
import android.widget.Toast;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private EditText mail, password;
    private Button loginButton;
    private String strMail, strPassword;
    private ProgressDialog progress;
    private TextView signUp;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        signUp = findViewById(R.id.sign_up);

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    getDetail();

                    if (!strMail.isEmpty() && !strPassword.isEmpty()) {
                        progress = new ProgressDialog(LoginActivity.this);
                        progress.setMessage("Logging In..");
                        progress.setCancelable(false);
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();

                        mAuth.signInWithEmailAndPassword(mail.getText().toString(), password.getText().toString())
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("TAG", "loginInWithEmail:success");
                                            Toast.makeText(LoginActivity.this, "Logged In.", Toast.LENGTH_SHORT).show();
                                            progress.dismiss();

                                            startActivity(new Intent(LoginActivity.this, LeadsListActivity.class));
                                            finish();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            progress.dismiss();
                                        }
                                    }
                                });
                    } else
                        Toast.makeText(LoginActivity.this, "Fill all the fields.", Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(LoginActivity.this, "No Internet Connection...", Toast.LENGTH_SHORT).show();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    private void getDetail() {
        strMail = mail.getText().toString();
        strPassword = password.getText().toString();
    }
}
