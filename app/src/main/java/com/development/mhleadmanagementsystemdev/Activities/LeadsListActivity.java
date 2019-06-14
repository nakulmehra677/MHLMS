package com.development.mhleadmanagementsystemdev.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.ItemClickListener;
import com.development.mhleadmanagementsystemdev.Models.CustomerDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.development.mhleadmanagementsystemdev.ViewHolders.LeadListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class LeadsListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private ProgressDialog progress;
    private FloatingActionButton fab;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_list);

        recyclerView = findViewById(R.id.recycler_view);
        fab = findViewById(R.id.fab);

        mAuth = FirebaseAuth.getInstance();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LeadsListActivity.this, FeedCustomerDetailsActivity.class));
            }
        });

        // Setting up the recyclerView //
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();
    }

    private void fetch() {
        progress = new ProgressDialog(LeadsListActivity.this);
        progress.setMessage("Loading..");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show(); //dd

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

        adapter = new FirebaseRecyclerAdapter<CustomerDetails, LeadListViewHolder>(options) {
            @Override
            public int getItemViewType(int position) {
                return 1;
            }

            @Override
            public LeadListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.lead_list_item, parent, false);

                return new LeadListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final LeadListViewHolder holder, final int position, CustomerDetails model) {
                holder.setIsRecyclable(false);

                holder.name.setText(model.getName());
                holder.contact.setText("Contact : " + model.getContactNumber());
                holder.propertyType.setText("Property type : " + model.getPropertyType());
                holder.employment.setText("Employment : " + model.getEmployement());
                holder.loanType.setText("Loan type : " + model.getLoanType());
                holder.location.setText("Location : " + model.getLocation());
                holder.loanAmount.setText("Loan amount : " + model.getLoanAmount());
                holder.remarks.setText("Remarks : " + model.getRemarks());
                holder.assignedTo.setText("Assigned to\n" + model.getAssignedTo());
                //holder.status.setText("Property type\n"model.getStatus());
                holder.date.setText("Date\n" + model.getDate());

                holder.expandableLinearLayout.setInRecyclerView(true);
                holder.expandableLinearLayout.setExpanded(false);

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.expandableLinearLayout.toggle();
                    }
                });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                if (isNetworkConnected()) {
                    mAuth.signOut();
                    Toast.makeText(LeadsListActivity.this, "Logged Out.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LeadsListActivity.this, MainActivity.class));
                    finish();
                } else
                    Toast.makeText(LeadsListActivity.this, "No Internet Connection...", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}