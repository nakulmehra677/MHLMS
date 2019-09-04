package com.mudrahome.MHLMS.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mudrahome.MHLMS.Adapters.LeadsItemAdapter;
import com.mudrahome.MHLMS.Interfaces.Firestore;
import com.mudrahome.MHLMS.Managers.ProfileManager;
import com.mudrahome.MHLMS.Models.LeadDetails;
import com.mudrahome.MHLMS.Models.OfferDetails;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LeadListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton fab;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ProgressBar progressBar, firstPageProgressBar;

    private com.mudrahome.MHLMS.Firebase.Firestore firestore;

    private SharedPreferences sharedPreferences;
    private ProfileManager profileManager;
    private Fragment fragment = null;

    private List<Object> leadDetailsList = new ArrayList<>();
    private LeadsItemAdapter adapter;
    private boolean isSrolling;
    private boolean isLastItemFetched;
    private DocumentSnapshot bottomVisibleItem = null;

    private String assignerFilter = "All";
    private String assigneeFilter = "All";
    private String locationFilter = "All";
    private String statusFilter = "All";
    private String loanTypeFilter = "All";

    private Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_list);

        sharedPreferences = getApplicationContext().getSharedPreferences("com.mudrahome.MHLMS", MODE_PRIVATE);

        if(sharedPreferences.getBoolean("UpdateAvlb",false)){
            new AlertDialog.Builder(LeadListActivity.this, R.style.AppCompatAlertDialogStyle)
                    .setTitle("Update Available")
                    .setMessage("It looks like you are missing out some new features, kindly Update app to get a better experience")
                    .setPositiveButton("Update now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    })
                    .setNegativeButton("Later", null)
                    .show();
        }

        toolbar = findViewById(R.id.toolbarLeadList);
        toolbar.inflateMenu(R.menu.lead_list_menu);
        setSupportActionBar(toolbar);


        recyclerView = findViewById(R.id.recycler_view);
        fab = findViewById(R.id.fab);
        mySwipeRefreshLayout = findViewById(R.id.swiperefresh);
        progressBar = findViewById(R.id.progressBar);
        firstPageProgressBar = findViewById(R.id.first_page_progressBar);
        //showProgressDialog("Loading..", this);

        profileManager = new ProfileManager();
        firestore = new com.mudrahome.MHLMS.Firebase.Firestore(this);

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
                        getOffer();
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
        if (profileManager.getCurrentUserType().equals(getString(R.string.telecaller))) {
            fab.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.ic_add_white_24dp);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(LeadListActivity.this, FeedCustomerDetailsActivity.class));
                }
            });
        } else if (profileManager.getCurrentUserType().equals(getString(R.string.admin))) {
            fab.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.megaphone);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNetworkConnected()) {
                        startActivity(new Intent(LeadListActivity.this, StartOfferActivity.class));
                    } else
                        showToastMessage(R.string.no_internet);
                }
            });
        }
        leadDetailsList.clear();
        getOffer();
    }

    private void getOffer() {

        Set<String> list = new HashSet<>();
        /*for(int i = 0 ; i < profileManager.getCurrentUserType().size(); i++){
            list.add(profileManager.getCurrentUserType().get(i));

        }*/
        list.add(profileManager.getCurrentUserDetails().getUserType());

        firestore.getOffers(new Firestore.FetchOffer() {
                                @Override
                                public void onSuccess(List<OfferDetails> details) {
                                    if (details.size() > 0) {
                                        leadDetailsList.addAll(details);
                                        adapter.notifyDataSetChanged();
                                    }
                                    fetchLeads();
                                }

                                @Override
                                public void onFail() {
                                    fetchLeads();
                                }
                            },
                profileManager.getCurrentUserDetails().getUserName(),
                list,
                true);
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

        firestore.getLeadList(onFetchLeadList(),
                s, profileManager.getCurrentUserDetails().getUserName(), bottomVisibleItem,
                locationFilter, assignerFilter, assigneeFilter, loanTypeFilter, statusFilter);
    }

    public void onButtonClicked(View view) {
        switch (view.getId()) {

            case R.id.filter:
                Intent intent = new Intent(LeadListActivity.this, FilterActivity.class);
                startActivityForResult(intent, 201);
                break;
        }
    }

  /*  @Override
    public void onBackPressed() {
        //super.onBackPressed();

    }*/

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

                    AlertDialog.Builder build = new AlertDialog.Builder(LeadListActivity.this);
                    build.setMessage("Are you sure you want to logout?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int u) {
                                    profileManager.signOut();
                                    showToastMessage(R.string.logged_out);
                                    //SharedPreferences.Editor editor = sharedPreferences.edit();
                                    //editor.clear();
                                    /*onBackPressed();*/
                                    startActivity(new Intent(LeadListActivity.this,LoginActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
                break;

            case R.id.notification:
                if (isNetworkConnected()) {
                    Intent intent = new Intent(LeadListActivity.this, NotificationActivity.class);
                    startActivity(intent);
                } else
                    showToastMessage(R.string.no_internet);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Firestore.OnGetUserDetails onFetchUserDetailsByUId() {
        return new Firestore.OnGetUserDetails() {
            @Override
            public void onSuccess(UserDetails userDetails) {
                profileManager.setCurrentUserDetails(userDetails);

                Set<String> set = new HashSet<>();


                /*for(int i =0;i<profileManager.getCurrentUserType().size();i++){
                    set.add(profileManager.getCurrentUserType().get(i));
                }*/

                adapter = new LeadsItemAdapter(leadDetailsList, LeadListActivity.this, profileManager.getCurrentUserDetails().getUserType());
                recyclerView.setAdapter(adapter);
                setLayoutByUser();
            }

            @Override
            public void fail() {

            }
        };
    }

    private Firestore.OnFetchLeadList onFetchLeadList() {
        return new Firestore.OnFetchLeadList() {
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
                    LeadDetails details = (LeadDetails) leadDetailsList.get(i);
                    if (l.getKey().equals(details.getKey())) {
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent();
            intent.putExtra("loggedIn", profileManager.checkUserExist());
            setResult(101, intent);
            finish();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}