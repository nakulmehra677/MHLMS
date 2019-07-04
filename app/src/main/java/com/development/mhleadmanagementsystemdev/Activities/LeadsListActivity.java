package com.development.mhleadmanagementsystemdev.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.development.mhleadmanagementsystemdev.Adapters.LeadListItemAdapter;
import com.development.mhleadmanagementsystemdev.Fragments.EditLeadDetailsFragment;
import com.development.mhleadmanagementsystemdev.Fragments.SalesmanEditLeadDetailsFragment;
import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchLeadListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchSalesPersonListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserDetailsListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUpdateLeadListener;
import com.development.mhleadmanagementsystemdev.Managers.ProfileManager;
import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.development.mhleadmanagementsystemdev.ViewHolders.LeadListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class LeadsListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton fab;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    //private FirebaseRecyclerAdapter adapter;

    private DatabaseReference database;

    public static String currentUserType;
    private LeadDetails updateLead;
    private SharedPreferences sharedPreferences;
    private UserDetails currentUserdetails;
    private ProfileManager profileManager;
    private ProgressBar progressBar;

    private List<UserDetails> userDetailsList;
    private List<LeadDetails> leadDetails = new ArrayList<>();
    private LeadListItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_list);

        recyclerView = findViewById(R.id.recycler_view);
        fab = findViewById(R.id.fab);
        progressBar = findViewById(R.id.progressBar);

        profileManager = new ProfileManager();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        showProgressDialog("Loading..", this);
        firebaseDatabaseHelper.getUserDetails(onFetchUserDetailsListener(), profileManager.getuId());

        //sharedPreferences = getSharedPreferences(sharedPreferenceUserDetails, Activity.MODE_PRIVATE);

        //currentUserType = sharedPreferences.getString(sharedPreferenceUserType, "Salesman");
        //currentUserName = sharedPreferences.getString(sharedPreferenceUserName, "");

        // Setting up the recyclerView //
        linearLayoutManager = new LinearLayoutManager(this) {

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(LeadsListActivity.this) {

                    private static final float SPEED = 1000f;// Change this value (default=25f)

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }

                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }

        };

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {
                    progressBar.setVisibility(View.VISIBLE);
                    /*if (!lastChapter.equals(lastFetechedChapter) && newLastChapterFeteched) {
                        scrollProgressBar.setVisibility(View.VISIBLE);
                        newLastChapterFeteched = false;
                        getData();
                    }*/
                    fetch();
                }
            }
        });

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemViewCacheSize(20);
        adapter = new LeadListItemAdapter(leadDetails);
        recyclerView.setAdapter(adapter);
        fetch();
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
        firebaseDatabaseHelper.getLeadList(onFetchLeadListListener(), leadDetails.size());
        /*database = FirebaseDatabase.getInstance().getReference("leadList");
        Query query;

        if (currentUserType.equals(telecallerUser))
            query = database.orderByChild("assigner").equalTo(currentUserdetails.getUserName());
        else
            query = database.orderByChild("assignedTo").equalTo(currentUserdetails.getUserName());

        FirebaseRecyclerOptions<LeadDetails> options = new FirebaseRecyclerOptions.Builder<LeadDetails>()
                .setLifecycleOwner(this)
                .setQuery(query, LeadDetails.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<LeadDetails, LeadListViewHolder>(options) {

            @NonNull
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
            public long getItemId(int position) {
                return super.getItemId(position);
                // return items[position].id.hashCode().toLong();

            }

            @Override
            protected void onBindViewHolder(final LeadListViewHolder holder, final int position, final LeadDetails model) {
                holder.setIsRecyclable(false);

                holder.name.setText(model.getName());
                holder.contact.setText(model.getContactNumber());
                holder.employment.setText(model.getEmployment());
                holder.loanType.setText(model.getLoanType());
                holder.location.setText(model.getLocation());
                holder.loanAmount.setText("\u20B9" + model.getLoanAmount());
                holder.telecallerRemarks.setText(model.getTelecallerRemarks());
                holder.status.setText(model.getStatus());
                holder.date.setText(model.getDate());

                if (model.getEmployment().equals("Self Employed"))
                    holder.employementType.setText(model.getEmploymentType());
                else
                    holder.employementTypeLayout.setVisibility(View.GONE);

                if (model.getSalesmanRemarks().equals("None")) {
                    holder.salesmanRemarksLayout.setVisibility(View.GONE);
                    holder.salesmanReasonLayout.setVisibility(View.GONE);
                } else {
                    holder.salesmanRemarks.setText(model.getSalesmanRemarks());
                    holder.salesmanReason.setText(model.getSalesmanReason());
                }

                if (model.getLoanType().equals("Home Loan") || model.getLoanType().equals("Loan Against Property")) {
                    holder.propertyType.setText(model.getPropertyType());
                } else {
                    holder.tpropertyType.setVisibility(View.GONE);
                }
                if (currentUserType.equals(telecallerUser))
                    holder.assignedTo.setText(model.getAssignedTo());
                else {
                    holder.tassign.setText("Assginer");
                    holder.assignedTo.setText(model.getAssigner());
                }

                holder.expandableLinearLayout.setInRecyclerView(true);
                holder.expandableLinearLayout.setExpanded(false);

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.expandableLinearLayout.toggle();
                    }
                });

                holder.setIsRecyclable(false);


                holder.optionMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isNetworkConnected()) {
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
                                                openSalesmanFragment(model);
                                            }
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupMenu.show();
                        } else
                            showToastMessage(R.string.no_internet);
                    }
                });

                progress.dismiss();
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);*/
    }

    /*@Override
    protected void onStart() {
        if (!progress.isShowing())
            showProgressDialog("Loading..", this);
        super.onStart();
    }*/

    /*@Override
    protected void onDestroy() {
        if (profileManager.getCurrentUser() != null)
            adapter.stopListening();
        super.onDestroy();
    }*/

    @Override
    public void onBackPressed() {
        Log.i("login", String.valueOf(profileManager.checkUserExist()));
        Intent intent = new Intent();
        intent.putExtra("loggedIn", profileManager.checkUserExist());
        setResult(101, intent);
        finish();
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
                    profileManager.signOut();
                    profileManager.signOut();
                    //SharedPreferences.Editor editor = sharedPreferences.edit();
                    //editor.clear();

                    showToastMessage(R.string.logged_out);
                    //startActivity(new Intent(LeadsListActivity.this, LoginActivity.class));
                    onBackPressed();
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
                profileManager.setCurrentUserDetails(userDetails);
                currentUserdetails = userDetails;
                currentUserType = userDetails.getUserType();
                setLayoutByUser();
            }
        };
    }

    private OnFetchLeadListListener onFetchLeadListListener() {
        return new OnFetchLeadListListener() {
            @Override
            public void onSuccess(List<LeadDetails> list) {
                leadDetails.addAll(list);
                adapter.notifyDataSetChanged();
                progress.dismiss();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailer() {
                showToastMessage(R.string.no_internet);
            }
        };
    }

    private OnFetchSalesPersonListListener onFetchSalesPersonListListener() {
        return new OnFetchSalesPersonListListener() {
            @Override
            public void onListFetched(final List userDetailList, List userName) {
                progress.dismiss();
                openTelecallerFragment(userDetailList, userName);
            }
        };
    }

    private OnUpdateLeadListener onUpdateLeadListener() {
        return new OnUpdateLeadListener() {
            @Override
            public void onLeadUpdated() {
                //adapter.stopListening();
                showToastMessage(R.string.lead_update);
                progress.dismiss();
                fetch();
            }
        };
    }

    private void openSalesmanFragment(final LeadDetails model) {
        SalesmanEditLeadDetailsFragment.newInstance(new SalesmanEditLeadDetailsFragment.OnSalesmanSubmitClickListener() {
            @Override
            public void onSubmitClicked(String dialogSalesmanRemarks, String dialogSalesmanReason) {
                updateLead = model;

                updateLead.setSalesmanRemarks(dialogSalesmanRemarks);
                updateLead.setSalesmanReason(dialogSalesmanReason);

                if (dialogSalesmanRemarks.equals(customerNotInterested))
                    updateLead.setStatus("Inactive");
                else if (dialogSalesmanRemarks.equals(documentPicked))
                    updateLead.setStatus("Closed");
                else if (dialogSalesmanRemarks.equals(customerFollowUp))
                    updateLead.setStatus("Follow Up");
                else if (dialogSalesmanRemarks.equals(customerNotContactable))
                    updateLead.setStatus("Inactive");
                else if (dialogSalesmanRemarks.equals(customerInterestedButDocumentPending))
                    updateLead.setStatus("Work in Progress");
                else
                    updateLead.setStatus("Active");

                firebaseDatabaseHelper.updateLeadDetails(onUpdateLeadListener(), updateLead);
            }
        }).show(getSupportFragmentManager(), "promo");
    }

    private void openTelecallerFragment(final List arrayList, List userName) {
        EditLeadDetailsFragment.newInstance(userName, new EditLeadDetailsFragment.OnSubmitClickListener() {
            @Override
            public void onSubmitClicked(String dialogAssignedTo) {
                updateLead.setAssignedTo(dialogAssignedTo);

                userDetailsList = new ArrayList<>();
                userDetailsList = arrayList;

                String strAssignedToUId = null;
                for (UserDetails userDetails : userDetailsList) {
                    if (userDetails.getUserName().equals(dialogAssignedTo)) {
                        strAssignedToUId = userDetails.getuId();
                    }
                }
                updateLead.setAssignedToUId(strAssignedToUId);
                firebaseDatabaseHelper.updateLeadDetails(onUpdateLeadListener(), updateLead);
            }
        }).show(getSupportFragmentManager(), "promo");
    }
}