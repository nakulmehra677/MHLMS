package com.development.mhleadmanagementsystemdev.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.support.v7.widget.SearchView;

import com.development.mhleadmanagementsystemdev.Adapters.LeadListItemAdapter;
import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchLeadListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserDetailsListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUpdateLeadListener;
import com.development.mhleadmanagementsystemdev.Managers.ProfileManager;
import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.development.mhleadmanagementsystemdev.R;

import java.util.ArrayList;
import java.util.List;

public class LeadsListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton fab;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    //private ProgressBar progressBar;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    private SharedPreferences sharedPreferences;
    private ProfileManager profileManager;

    private List<LeadDetails> leadDetailsList = new ArrayList<>();
    private LeadListItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_list);

        recyclerView = findViewById(R.id.recycler_view);
        fab = findViewById(R.id.fab);
        mySwipeRefreshLayout = findViewById(R.id.swiperefresh);
        //progressBar = findViewById(R.id.progressBar);

        //showProgressDialog("Loading..", this);

        profileManager = new ProfileManager();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

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

        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {
                    progressBar.setVisibility(View.VISIBLE);
                    /*if (!lastChapter.equals(lastFetechedChapter) && newLastChapterFeteched) {
                        scrollProgressBar.setVisibility(View.VISIBLE);
                        newLastChapterFeteched = false;
                        getData();
                    }
                    fetch();
                }
            }
        });*/

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {

                    @Override
                    public void onRefresh() {
                        leadDetailsList.clear();
                        adapter.notifyDataSetChanged();
                        fetch();
                    }
                }
        );

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setItemViewCacheSize(20);
    }

    @SuppressLint("RestrictedApi")
    private void setLayoutByUser() {
        //if (sharedPreferences.getString(sharedPreferenceUserType, "Salesman").equals("Telecaller"))
        if (profileManager.getCurrentUserType().equals(telecallerUser))
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
        String s;
        if (profileManager.getCurrentUserType().equals(telecallerUser))
            s = "assigner";
        else if (profileManager.getCurrentUserType().equals(salesmanUser))
            s = "assignedTo";
        else
            s = "Admin";
        firebaseDatabaseHelper.getLeadList(onFetchLeadListListener(),
                s, profileManager.getCurrentUserDetails().getUserName());
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
                fetch();
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

                    AlertDialog.Builder build = new AlertDialog.Builder(LeadsListActivity.this);
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
                    AlertDialog alert = build.create();
                    alert.show();

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
                Log.i("UserDetails", userDetails.getUserType());
                adapter = new LeadListItemAdapter(leadDetailsList, LeadsListActivity.this, profileManager.getCurrentUserType());
                recyclerView.setAdapter(adapter);
                setLayoutByUser();
            }
        };
    }

    private OnFetchLeadListListener onFetchLeadListListener() {
        return new OnFetchLeadListListener() {
            @Override
            public void onLeadAdded(LeadDetails l) {
                leadDetailsList.add(l);
                adapter.notifyDataSetChanged();

                //if (progress.isShowing())
                //  progress.dismiss();
                mySwipeRefreshLayout.setRefreshing(false);
                //progressBar.setVisibility(View.GONE);
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
                //progressBar.setVisibility(View.GONE);
            }
        };
    }
}