package com.development.mhleadmanagementsystemdev.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.development.mhleadmanagementsystemdev.Fragments.EditLeadDetailsFragment;
import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchSalesPersonListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUpdateLeadListener;
import com.development.mhleadmanagementsystemdev.Models.CustomerDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.development.mhleadmanagementsystemdev.ViewHolders.LeadListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class LeadsListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private FloatingActionButton fab;
    private FirebaseAuth mAuth;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    private CustomerDetails updateLead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_list);

        recyclerView = findViewById(R.id.recycler_view);
        fab = findViewById(R.id.fab);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        if (isNetworkConnected()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                startActivityForResult(new Intent(LeadsListActivity.this, LoginActivity.class), 1);
            } else {
                intializeVariables();
            }
        } else {
            showToastMessage(R.string.no_internet);
            finish();
        }
    }

    private void intializeVariables() {
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
        showProgressDialog("Loading..", this);

        Query query = FirebaseDatabase.getInstance().getReference("leadList");

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
                                        snapshot.child("assignedTo").getValue().toString(),
                                        snapshot.child("status").getValue().toString(),
                                        snapshot.child("key").getValue().toString());
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
            public void onDataChanged() {
                if (getItemCount() == 0) {
                    showToastMessage(R.string.no_leads);
                    progress.dismiss();
                }
                super.onDataChanged();
            }

            @Override
            protected void onBindViewHolder(final LeadListViewHolder holder, final int position, final CustomerDetails model) {
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
                holder.status.setText("Status\n" + model.getStatus());
                holder.date.setText("Date\n" + model.getDate());

                holder.expandableLinearLayout.setInRecyclerView(true);
                holder.expandableLinearLayout.setExpanded(false);

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.expandableLinearLayout.toggle();
                    }
                });

                holder.optionMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(LeadsListActivity.this, holder.optionMenu);
                        popupMenu.inflate(R.menu.lead_list_item_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.edit_details:
                                        showProgressDialog("Loading..", LeadsListActivity.this);

                                        updateLead = model;
                                        firebaseDatabaseHelper.fetchSalesPersons(
                                                onFetchSalesPersonListListener(), model.getLocation());
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
                progress.dismiss();
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 1) {
            boolean message = data.getBooleanExtra("MESSAGE", false);
            if (message)
                intializeVariables();
            else
                finish();
        }
    }

    @Override
    protected void onDestroy() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            adapter.stopListening();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lead_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                if (isNetworkConnected()) {
                    mAuth.signOut();
                    showToastMessage(R.string.logged_out);
                    startActivity(new Intent(LeadsListActivity.this, LoginActivity.class));
                    finish();
                } else
                    showToastMessage(R.string.no_internet);

                return true;

            /*case R.id.user_list_menu:
                if (isNetworkConnected()) {
                    startActivity(new Intent(LeadsListActivity.this, UsersListActivity.class));
                } else
                    showToastMessage(R.string.no_internet);

                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private OnFetchSalesPersonListListener onFetchSalesPersonListListener() {
        return new OnFetchSalesPersonListListener() {
            @Override
            public void onListFetched(List arrayList) {
                progress.dismiss();
                EditLeadDetailsFragment.newInstance(arrayList, new EditLeadDetailsFragment.OnSubmitClickListener() {
                    @Override
                    public void onSubmitClicked(String dialogAssignedTo, String dialogStatus) {
                        updateLead.setAssignedTo(dialogAssignedTo);
                        updateLead.setStatus(dialogStatus);
                        firebaseDatabaseHelper.updateLeadDetails(onUpdateLeadListener(), updateLead);
                    }
                }).show(getSupportFragmentManager(), "promo");
            }
        };
    }

    private OnUpdateLeadListener onUpdateLeadListener() {
        return new OnUpdateLeadListener() {
            @Override
            public void onLeadUpdated() {
                adapter.stopListening();
                showToastMessage(R.string.lead_update);

                showProgressDialog("Loading..", LeadsListActivity.this);

                adapter.startListening();
            }
        };
    }
}