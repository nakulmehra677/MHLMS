package com.development.mhleadmanagementsystemdev.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Models.TeleCallerDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.development.mhleadmanagementsystemdev.ViewHolders.UserListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class UsersListActivity extends BaseActivity {

    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private ProgressDialog progress;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    //private FloatingActionButton fab;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        recyclerView = findViewById(R.id.recycler_view);
        //fab = findViewById(R.id.user_list_fab);

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        /*if (isAdmin) {
            fab.setVisibility(View.VISIBLE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UsersListActivity.this, CreateUserActivity.class));
            }
        });*/

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        fetch();
    }

    private void fetch() {
        progress = new ProgressDialog(UsersListActivity.this);
        progress.setMessage("Loading..");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        Query query = FirebaseDatabase.getInstance().getReference("telecallerList");

        FirebaseRecyclerOptions<TeleCallerDetails> options =
                new FirebaseRecyclerOptions.Builder<TeleCallerDetails>()
                        .setQuery(query, new SnapshotParser<TeleCallerDetails>() {
                            @NonNull
                            @Override
                            public TeleCallerDetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new TeleCallerDetails(snapshot.child("uId").getValue().toString(),
                                        snapshot.child("userName").getValue().toString(),
                                        snapshot.child("mail").getValue().toString(),
                                        snapshot.child("location").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<TeleCallerDetails, UserListViewHolder>(options) {
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
            public void onDataChanged() {
                if (getItemCount() == 0) {
                    showToastMessage(R.string.no_telecallers);
                    progress.dismiss();
                }
                super.onDataChanged();
            }

            @Override
            protected void onBindViewHolder(final UserListViewHolder holder, final int position, TeleCallerDetails model) {
                holder.setIsRecyclable(false);

                Log.d("userName", model.getUserName());
                holder.userName.setText(model.getUserName());

                progress.dismiss();
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