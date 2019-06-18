package com.development.mhleadmanagementsystemdev.Activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserListListener;
import com.development.mhleadmanagementsystemdev.Models.CustomerDetails;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.development.mhleadmanagementsystemdev.ViewHolders.LeadListViewHolder;
import com.development.mhleadmanagementsystemdev.ViewHolders.UserListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class UsersListActivity extends BaseActivity {

    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private ProgressDialog progress;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        recyclerView = findViewById(R.id.recycler_view);
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        fetch();
    }

   /* private OnFetchUserListListener onFetchUserListListener() {
        return new OnFetchUserListListener() {
            @Override
            public void onUserListFetched(List<String> list) {
                // Create an ArrayAdapter from List
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                        (UsersListActivity.this, android.R.layout.simple_list_item_1, list);

                // DataBind ListView with items from ArrayAdapter
                recyclerView.setAdapter(adapter);
                progress.dismiss();
            }

            @Override
            public void onFailed() {
                progress.dismiss();
                showToastMessage(R.string.failed_to_fetch);
            }
        };
    }*/

    private void fetch() {
        progress = new ProgressDialog(UsersListActivity.this);
        progress.setMessage("Loading..");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        Query query = FirebaseDatabase.getInstance().getReference("userList");

        FirebaseRecyclerOptions<UserDetails> options =
                new FirebaseRecyclerOptions.Builder<UserDetails>()
                        .setQuery(query, new SnapshotParser<UserDetails>() {
                            @NonNull
                            @Override
                            public UserDetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new UserDetails(snapshot.child("uId").getValue().toString(),
                                        snapshot.child("password").getValue().toString(),
                                        snapshot.child("userName").getValue().toString(),
                                        snapshot.child("mail").getValue().toString(),
                                        snapshot.child("userType").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<UserDetails, UserListViewHolder>(options) {
            @Override
            public int getItemViewType(int position) {
                return 1;
            }

            @Override
            public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_list_item, parent, false);

                return new UserListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final UserListViewHolder holder, final int position, UserDetails model) {
                holder.setIsRecyclable(false);

                Log.d("userName", model.getUserName());
                Log.d("userType", model.getUserType());

                holder.userName.setText(model.getUserName());
                holder.userType.setText(model.getUserType());

                progress.dismiss();

                holder.optionMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(UsersListActivity.this, holder.optionMenu);
                        popupMenu.inflate(R.menu.user_list_item_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.delete_account:
                                        showToastMessage(R.string.no_internet);
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }
}