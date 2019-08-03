package com.development.mhleadmanagementsystemdev.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.development.mhleadmanagementsystemdev.Adapters.LeadsItemAdapter;
import com.development.mhleadmanagementsystemdev.Firebase.Firestore;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchLeadListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnGetUserDetails;
import com.development.mhleadmanagementsystemdev.Managers.ProfileManager;
import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LeadListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton fab;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ProgressBar progressBar, firstPageProgressBar;

    private Firestore firestore;

    private SharedPreferences sharedPreferences;
    private ProfileManager profileManager;
    private Fragment fragment = null;

    private List<LeadDetails> leadDetailsList = new ArrayList<>();
    private LeadsItemAdapter adapter;
    private boolean isSrolling;
    private boolean isLastItemFetched;
    private DocumentSnapshot bottomVisibleItem = null;

    private String assignerFilter = "All";
    private String assigneeFilter = "All";
    private String locationFilter = "All";
    private String statusFilter = "All";
    private String loanTypeFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_list);

        recyclerView = findViewById(R.id.recycler_view);
        fab = findViewById(R.id.fab);
        mySwipeRefreshLayout = findViewById(R.id.swiperefresh);
        progressBar = findViewById(R.id.progressBar);
        firstPageProgressBar = findViewById(R.id.first_page_progressBar);
        //showProgressDialog("Loading..", this);

        profileManager = new ProfileManager();
        firestore = new Firestore(this);

        firestore.getUsers(onFetchUserDetailsByUId(), profileManager.getuId());

        //sharedPreferences = getSharedPreferences(sharedPreferenceUserDetails, Activity.MODE_PRIVATE);

        //currentUserType = sharedPreferences.getString(sharedPreferenceUserType, "Salesman");
        //currentUserName = sharedPreferences.getString(sharedPreferenceUserName, "");

        // Setting up the recyclerView //
        linearLayoutManager = new LinearLayoutManager(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                firstPageProgressBar.setVisibility(View.GONE);
            }
        }, 5000);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isSrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {

                    long firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                    long visibleItemCount = linearLayoutManager.getChildCount();
                    long totalItemCount = linearLayoutManager.getItemCount();

                    if (isSrolling && (firstVisibleItem + visibleItemCount == totalItemCount) && !isLastItemFetched) {
                        isSrolling = false;
                        progressBar.setVisibility(View.VISIBLE);
                        fetchLeads();
                    }
                }
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {

                    @Override
                    public void onRefresh() {
                        leadDetailsList.clear();
                        adapter.notifyDataSetChanged();
                        isLastItemFetched = false;
                        bottomVisibleItem = null;
                        firstPageProgressBar.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                firstPageProgressBar.setVisibility(View.GONE);
                            }
                        }, 5000);
                        //linearLayoutManager = new LinearLayoutManager(LeadListActivity.this);
                        fetchLeads();
                    }
                }
        );

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
    }

    @SuppressLint("RestrictedApi")
    private void setLayoutByUser() {
        //if (sharedPreferences.getString(sharedPreferenceUserType, "Salesman").equals("Telecaller"))
        if (profileManager.getCurrentUserType().equals(getString(R.string.telecaller)))
            fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LeadListActivity.this, FeedCustomerDetailsActivity.class));
            }
        });
        leadDetailsList.clear();
        fetchLeads();
    }

    private void fetchLeads() {
        String currentUserType = profileManager.getCurrentUserType();
        String s;
        if (currentUserType.equals(getString(R.string.telecaller)))
            s = "assigner";
        else if (currentUserType.equals(getString(R.string.salesman)))
            s = "assignedTo";
        else
            s = "Admin";

        firestore.getLeadList(onFetchLeadListListener(),
                s, profileManager.getCurrentUserDetails().getUserName(), bottomVisibleItem,
                locationFilter, assignerFilter, assigneeFilter, loanTypeFilter, statusFilter);
    }

    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.sort_by:
                break;

            case R.id.filter:
                Intent intent = new Intent(LeadListActivity.this, FilterActivity.class);
                startActivityForResult(intent, 201);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("loggedIn", profileManager.checkUserExist());
        setResult(101, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lead_list_menu, menu);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                mySwipeRefreshLayout.setEnabled(false);
                mySwipeRefreshLayout.setRefreshing(false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /*for (int i = 0; i < leadDetailsList.size(); i++) {
                    if (!leadDetailsList.get(i).getName().toLowerCase().contains(newText))
                        leadDetailsList.remove(i);
                }
                adapter.notifyDataSetChanged();*/
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mySwipeRefreshLayout.setEnabled(true);
                leadDetailsList.clear();
                fetchLeads();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                if (isNetworkConnected()) {

                    AlertDialog.Builder build = new androidx.appcompat.app.AlertDialog.Builder(LeadListActivity.this);
                    build.setMessage("Are you sure you want to logout?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int u) {
                                    profileManager.signOut();
                                    showToastMessage(R.string.logged_out);
                                    //SharedPreferences.Editor editor = sharedPreferences.edit();
                                    //editor.clear();
                                    onBackPressed();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int u) {

                        }
                    });
                    androidx.appcompat.app.AlertDialog alert = build.create();
                    alert.show();

                } else
                    showToastMessage(R.string.no_internet);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private OnGetUserDetails onFetchUserDetailsByUId() {
        return new OnGetUserDetails() {
            @Override
            public void onSuccess(UserDetails userDetails) {
                profileManager.setCurrentUserDetails(userDetails);
                adapter = new LeadsItemAdapter(leadDetailsList, LeadListActivity.this, profileManager.getCurrentUserType());
                recyclerView.setAdapter(adapter);
                setLayoutByUser();
            }

            @Override
            public void fail() {

            }
        };
    }

    private OnFetchLeadListListener onFetchLeadListListener() {
        return new OnFetchLeadListListener() {
            @Override
            public void onLeadAdded(List<LeadDetails> l, DocumentSnapshot lastVisible) {
                if (l.size() < 20)
                    isLastItemFetched = true;

                bottomVisibleItem = lastVisible;

                leadDetailsList.addAll(l);
                adapter.notifyDataSetChanged();

                if (mySwipeRefreshLayout.isRefreshing())
                    mySwipeRefreshLayout.setRefreshing(false);

                progressBar.setVisibility(View.GONE);
                firstPageProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLeadChanged(LeadDetails l) {
                for (int i = 0; i < leadDetailsList.size(); i++) {
                    if (l.getKey().equals(leadDetailsList.get(i).getKey())) {
                        leadDetailsList.set(i, l);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailer() {
                showToastMessage(R.string.no_internet);

                //if (progress.isShowing())
                //  progress.dismiss();
                mySwipeRefreshLayout.setRefreshing(false);
                firstPageProgressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (resultCode == 201) {
            assignerFilter = data.getStringExtra("assigner_filter");
            assigneeFilter = data.getStringExtra("assignee_filter");
            locationFilter = data.getStringExtra("location_filter");
            loanTypeFilter = data.getStringExtra("loan_type_filter");
            statusFilter = data.getStringExtra("status_filter");

            leadDetailsList.clear();
            adapter.notifyDataSetChanged();
            isLastItemFetched = false;
            bottomVisibleItem = null;
            firstPageProgressBar.setVisibility(View.VISIBLE);

            fetchLeads();
        }
    }
}