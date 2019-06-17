package com.development.mhleadmanagementsystemdev.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserListListener;
import com.development.mhleadmanagementsystemdev.R;

import java.util.ArrayList;
import java.util.List;

public class UsersListActivity extends BaseActivity {

    private ListView listView;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        listView = findViewById(R.id.users_list);

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        progress = new ProgressDialog(UsersListActivity.this);
        progress.setMessage("Loading..");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        firebaseDatabaseHelper.fetchUserList(onFetchUserListListener());
    }

    private OnFetchUserListListener onFetchUserListListener() {
        return new OnFetchUserListListener() {
            @Override
            public void onUserListFetched(List<String> list) {
                // Create an ArrayAdapter from List
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                        (UsersListActivity.this, android.R.layout.simple_list_item_1, list);

                // DataBind ListView with items from ArrayAdapter
                listView.setAdapter(arrayAdapter);
                progress.dismiss();
            }

            @Override
            public void onFailed() {
                progress.dismiss();
                showToastMessage(R.string.failed_to_fetch);
            }
        };
    }
}