package com.development.mhleadmanagementsystemdev.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.development.mhleadmanagementsystemdev.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private EditText mail, password, userName;
    private Button loginButton;
    private String strMail, strPassword, strUserName, strUserType;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        userName = findViewById(R.id.user_name);
        loginButton = findViewById(R.id.login);

        if (isNetworkConnected()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(MainActivity.this, LeadsListActivity.class));
                finish();
            }
        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection...", Toast.LENGTH_SHORT).show();
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    progress = new ProgressDialog(MainActivity.this);
                    progress.setMessage("Logging In..");
                    progress.setCancelable(false);
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();

                    getDetail();
                    if (!strMail.isEmpty() && !strPassword.isEmpty() && !strUserName.isEmpty()) {
                        mAuth.signInWithEmailAndPassword(mail.getText().toString(), password.getText().toString())
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("TAG", "signInWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();

                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(strUserName).build();

                                            user.updateProfile(profileUpdates);
                                            Toast.makeText(MainActivity.this, "Logged In.", Toast.LENGTH_SHORT).show();
                                            progress.dismiss();

                                            startActivity(new Intent(MainActivity.this, LeadsListActivity.class));
                                            finish();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            progress.dismiss();
                                        }
                                    }
                                });
                    } else
                        Toast.makeText(MainActivity.this, "Fill all the fields.", Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(MainActivity.this, "No Internet Connection...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDetail() {
        strMail = mail.getText().toString();
        strPassword = password.getText().toString();
        strUserName = userName.getText().toString();
    }

    public void onRadioButtonLoginClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.salaried:
                if (checked) {
                    strUserType = "Telecaller";
                    break;
                }
            case R.id.self_employed:
                if (checked) {
                    strUserType = "Sales person";
                    break;
                }
        }
    }
}
