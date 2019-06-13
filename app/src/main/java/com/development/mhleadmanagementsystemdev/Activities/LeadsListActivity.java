package com.development.mhleadmanagementsystemdev.Activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.LeadsListAdapter;
import com.development.mhleadmanagementsystemdev.Models.CustomerDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class LeadsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private ProgressDialog progress;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_list);

        recyclerView = findViewById(R.id.recycler_view);

        // Setting up the recyclerView //
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView txtTitle;
        public TextView txtDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            txtTitle = itemView.findViewById(R.id.list_title);
            txtDesc = itemView.findViewById(R.id.list_desc);
        }

        public void setTxtTitle(String string) {
            txtTitle.setText(string);
        }

        public void setTxtDesc(String string) {
            txtDesc.setText(string);
        }
    }

    private void fetch() {
        progress = new ProgressDialog(LeadsListActivity.this);
        progress.setMessage("Loading..");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        Query query = FirebaseDatabase.getInstance().getReference();

        FirebaseRecyclerOptions<CustomerDetails> options =
                new FirebaseRecyclerOptions.Builder<CustomerDetails>()
                        .setQuery(query, new SnapshotParser<CustomerDetails>() {
                            @NonNull
                            @Override
                            public CustomerDetails parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new CustomerDetails(snapshot.child("name").getValue().toString(),
                                        snapshot.child("contactNumber").getValue().toString(),
                                        snapshot.child("propertyType").getValue().toString(),
                                        snapshot.child("employement").getValue().toString(),
                                        snapshot.child("loanType").getValue().toString(),
                                        snapshot.child("location").getValue().toString(),
                                        snapshot.child("loanAmount").getValue().toString(),
                                        snapshot.child("remarks").getValue().toString(),
                                        snapshot.child("date").getValue().toString(),
                                        snapshot.child("assignedTo").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<CustomerDetails, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.lead_list_item, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, CustomerDetails model) {
                holder.setTxtTitle(model.getName());
                holder.setTxtDesc(model.getContactNumber());
                progress.dismiss();

                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(LeadsListActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}