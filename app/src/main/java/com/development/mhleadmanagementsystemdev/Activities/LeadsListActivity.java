package com.development.mhleadmanagementsystemdev.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;

import com.development.mhleadmanagementsystemdev.Fragments.EditLeadDetailsFragment;
import com.development.mhleadmanagementsystemdev.Fragments.FilterFragment;
import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchSalesPersonListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnLastLeadListener;
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
import com.google.firebase.database.DatabaseReference;
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
    //private ProgressBar progressBar;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private long items = 0;

    private CustomerDetails updateLead, lastLead;
    private String temporaryLastLeadKey;
    private boolean allListFetched = false;
    private String needSalesPersonListFor;
    private DatabaseReference database;
    private FirebaseRecyclerOptions<CustomerDetails> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_list);

        recyclerView = findViewById(R.id.recycler_view);
        fab = findViewById(R.id.fab);
        //progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {
                    if (!allListFetched) {
                        progressBar.setVisibility(View.VISIBLE);
                        Log.i("SCROLL", "not all list fetched");
                        fetch();
                    }
                    Log.i("SCROLL", "recuclerview is scrolling");
                }
            }
        });*/
        intializeVariables();

    }

    @SuppressLint("RestrictedApi")
    private void intializeVariables() {

        SharedPreferences sharedPreferences = getSharedPreferences("shared_preference", Activity.MODE_PRIVATE);
        Log.i("UUUSER_TYPE", sharedPreferences.getString("shared_preference_user_type", "Salesman"));
        if (sharedPreferences.getString("shared_preference_user_type", "salesman").equals("Telecaller"))
            fab.setVisibility(View.VISIBLE);

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
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        fetchLastLead();
    }

    private void fetchLastLead() {
        showProgressDialog("Loading..", this);
        fetch();
        //firebaseDatabaseHelper.getLastLead(onLastLeadListener());
    }

    private void fetch() {
        Log.i("function", "fetch() function called");

        options =
                new FirebaseRecyclerOptions.Builder<CustomerDetails>()
                        .setQuery(database, new SnapshotParser<CustomerDetails>() {
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
                SharedPreferences sharedPreferences = getSharedPreferences("shared_preference", Activity.MODE_PRIVATE);
                Log.i("UUUSER_TYPE", sharedPreferences.getString("shared_preference_user_type", "Salesman"));

                boolean showItemMenu = false;
                if (sharedPreferences.getString("shared_preference_user_type", "Salesman").equals("Telecaller"))
                    showItemMenu = true;

                items = getItemCount();

                return new LeadListViewHolder(view, showItemMenu);
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
                holder.contact.setText(model.getContactNumber());
                holder.propertyType.setText(model.getPropertyType());
                holder.employment.setText(model.getEmployement());
                holder.loanType.setText(model.getLoanType());
                holder.location.setText(model.getLocation());
                holder.loanAmount.setText(model.getLoanAmount());
                holder.remarks.setText(model.getRemarks());
                holder.assignedTo.setText(model.getAssignedTo());
                holder.status.setText(model.getStatus());
                holder.date.setText(model.getDate());

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
                                        needSalesPersonListFor = "edit";
                                        firebaseDatabaseHelper.fetchSalesPersonsByLocation(
                                                onFetchSalesPersonListListener(), model.getLocation());
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });

                /*temporaryLastLeadKey = model.getKey();

                items++;
                Log.i("itemNumber", String.valueOf(items));

                if (model.getKey().equals(lastLead.getKey())) {
                    Log.i("LastLeadfetched", "true");
                    allListFetched = true;
                }*/

                //progressBar.setVisibility(View.INVISIBLE);
                progress.dismiss();
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check if the request code is same as what is passed  here it is 2

        if (requestCode == 1) {
            boolean message = data.getBooleanExtra("MESSAGE", false);
            Log.i("MESSGE", String.valueOf(message));
            if (message)
                intializeVariables();
            else {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                Log.i("AUTHHH", String.valueOf(currentUser));
                if (currentUser != null)
                    intializeVariables();
                else
                    finish();

            }
        }

    }*/

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

            case R.id.name:
                if (isNetworkConnected()) {
                    showProgressDialog("Loading..", LeadsListActivity.this);
                    needSalesPersonListFor = "filter";
                    firebaseDatabaseHelper.fetchAllSalesPersons(onFetchSalesPersonListListener());
                }

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

    private OnLastLeadListener onLastLeadListener() {
        return new OnLastLeadListener() {
            @Override
            public void onLastLeadFetched(CustomerDetails lead) {
                //lastLead = lead;
                //Log.i("LASTLEAD", lastLead.getName());
                //fetch();
            }
        };
    }

    private OnFetchSalesPersonListListener onFetchSalesPersonListListener() {
        return new OnFetchSalesPersonListListener() {
            @Override
            public void onListFetched(List arrayList) {
                progress.dismiss();

                if (needSalesPersonListFor.equals("edit")) {
                    EditLeadDetailsFragment.newInstance(arrayList, new EditLeadDetailsFragment.OnSubmitClickListener() {
                        @Override
                        public void onSubmitClicked(String dialogAssignedTo, String dialogStatus) {
                            updateLead.setAssignedTo(dialogAssignedTo);
                            updateLead.setStatus(dialogStatus);
                            firebaseDatabaseHelper.updateLeadDetails(onUpdateLeadListener(), updateLead);
                        }
                    }).show(getSupportFragmentManager(), "promo");

                } else {
                    FilterFragment.newInstance(arrayList, new FilterFragment.OnSubmitClickListener() {
                        @Override
                        public void onSubmitClicked(String dialogAssignedTo, String dialogStatus) {
                            updateLead.setAssignedTo(dialogAssignedTo);
                            updateLead.setStatus(dialogStatus);
                            firebaseDatabaseHelper.updateLeadDetails(onUpdateLeadListener(), updateLead);
                        }
                    }).show(getSupportFragmentManager(), "promo");
                }
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