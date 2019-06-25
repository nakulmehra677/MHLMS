package com.development.mhleadmanagementsystemdev.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.development.mhleadmanagementsystemdev.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainEmptyActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_empty);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Intent intent;

        if (isNetworkConnected()) {
            if (currentUser != null) {
                intent = new Intent(MainEmptyActivity.this, LeadsListActivity.class);
            } else
                intent = new Intent(MainEmptyActivity.this, LoginActivity.class);

            startActivity(intent);
            finish();
        }
    }
}
