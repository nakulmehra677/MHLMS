package com.development.mhleadmanagementsystemdev.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.paging.PagedList;
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
import com.development.mhleadmanagementsystemdev.Fragments.SalesmanEditLeadDetailsFragment;
import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchSalesPersonListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserDetailsListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUpdateLeadListener;
import com.development.mhleadmanagementsystemdev.Managers.ProfileManager;
import com.development.mhleadmanagementsystemdev.Models.CustomerDetails;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
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
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;

import java.util.List;

import static com.google.firebase.firestore.FieldValue.delete;

public class LeadsListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton fab;
    private ProgressBar progressBar;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private FirebaseRecyclerPagingAdapter adapter;

    private DatabaseReference database;
    private FirebaseAuth mAuth;

    private String currentUserType;
    private CustomerDetails updateLead;
    private String needSalesPersonListFor;
    private SharedPreferences sharedPreferences;
    private UserDetails currentUserdetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_list);

        recyclerView = findViewById(R.id.recycler_view);
        fab = findViewById(R.id.fab);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        showProgressDialog("Loading..", this);
        firebaseDatabaseHelper.getUserDetails(onFetchUserDetailsListener(), mAuth.getUid());

        //sharedPreferences = getSharedPreferences(sharedPreferenceUserDetails, Activity.MODE_PRIVATE);

        //currentUserType = sharedPreferences.getString(sharedPreferenceUserType, "Salesman");
        //currentUserName = sharedPreferences.getString(sharedPreferenceUserName, "");

        // Setting up the recyclerView //
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    @SuppressLint("RestrictedApi")
    private void setLayoutByUser() {
        //if (sharedPreferences.getString(sharedPreferenceUserType, "Salesman").equals("Telecaller"))
        if (currentUserType.equals(telecallerUser))
            fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LeadsListActivity.this, FeedCustomerDetailsActivity.class));
            }
        });
        fetch();
    }

    private void fetch() {
        database = FirebaseDatabase.getInstance().getReference("leadList");
        Query query;

        if (currentUserType.equals(telecallerUser))
            query = database;//.orderByChild("assigner").equalTo(currentUserdetails.getUserName());
        else
            query = database;//.orderByChild("assignedTo").equalTo(currentUserdetails.getUserName());


        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        DatabasePagingOptions<CustomerDetails> options = new DatabasePagingOptions.Builder<CustomerDetails>()
                .setLifecycleOwner(this)
                .setQuery(query, config, CustomerDetails.class)
                .build();

        adapter = new FirebaseRecyclerPagingAdapter<CustomerDetails, LeadListViewHolder>(options) {

            @NonNull
            @Override
            public LeadListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.lead_list_item, parent, false);

                return new LeadListViewHolder(view);
            }

            /*@Override
            public void onDataChanged() {
                if (getItemCount() == 0) {
                    showToastMessage(R.string.no_leads);
                    progress.dismiss();
                }
                super.onDataChanged();
            }*/

            @Override
            protected void onBindViewHolder(final LeadListViewHolder holder, final int position, final CustomerDetails model) {
                holder.setIsRecyclable(false);

                holder.name.setText(model.getName());
                holder.contact.setText(model.getContactNumber());
                holder.propertyType.setText(model.getPropertyType());
                holder.employment.setText(model.getEmployment());
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
                        popupMenu.inflate(R.menu.telecaller_lead_list_item_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.edit_details:
                                        if (currentUserType.equals(telecallerUser)) {
                                            showProgressDialog("Loading..", LeadsListActivity.this);
                                            updateLead = model;
                                            firebaseDatabaseHelper.fetchSalesPersonsByLocation(
                                                    onFetchSalesPersonListListener(), model.getLocation());
                                        } else {
                                            SalesmanEditLeadDetailsFragment.newInstance(new SalesmanEditLeadDetailsFragment.OnSalesmanSubmitClickListener() {
                                                @Override
                                                public void onSubmitClicked(String dialogStatus) {
                                                    updateLead = model;
                                                    updateLead.setStatus(dialogStatus);
                                                    firebaseDatabaseHelper.updateLeadDetails(onUpdateLeadListener(), updateLead);
                                                }
                                            }).show(getSupportFragmentManager(), "promo");
                                        }
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

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        // Do your loading animation
                        progressBar.setVisibility(View.VISIBLE);
                        break;

                    case LOADED:
                        // Stop Animation
                        progressBar.setVisibility(View.GONE);
                        break;

                    case FINISHED:
                        //Reached end of Data set
                        progressBar.setVisibility(View.GONE);
                        break;

                    case ERROR:
                        retry();
                        break;
                }
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
                    //SharedPreferences.Editor editor = sharedPreferences.edit();
                    //editor.clear();

                    showToastMessage(R.string.logged_out);
                    //startActivity(new Intent(LeadsListActivity.this, LoginActivity.class));
                    finish();
                } else
                    showToastMessage(R.string.no_internet);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private OnFetchUserDetailsListener onFetchUserDetailsListener() {
        return new OnFetchUserDetailsListener() {
            @Override
            public void onSuccess(UserDetails userDetails) {
                currentUserdetails = userDetails;
                currentUserType = userDetails.getUserType();
                setLayoutByUser();
            }
        };
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
                progress.dismiss();
                fetch();
            }
        };
    }
}