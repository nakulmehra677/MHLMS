package com.development.mhleadmanagementsystemdev.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.development.mhleadmanagementsystemdev.Managers.ProfileManager;

public class MainEmptyActivity extends BaseActivity {

    private ProfileManager profileManager;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_empty);

        profileManager = new ProfileManager();
        profileManager.checkUserExist();

        if (isNetworkConnected()) {
            if (profileManager.checkUserExist()) {
                intent = new Intent(MainEmptyActivity.this, LeadsListActivity.class);
            } else
                intent = new Intent(MainEmptyActivity.this, LoginActivity.class);

            startActivity(intent);
            finish();
        }
    }
}
