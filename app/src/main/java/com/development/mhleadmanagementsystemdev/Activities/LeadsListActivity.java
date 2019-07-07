package com.development.mhleadmanagementsystemdev.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
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
    private ProgressBar progressBar;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    private SharedPreferences sharedPreferences;
    private ProfileManager profileManager;

    private List<LeadDetails> leadDetails = new ArrayList<>();
    private LeadListItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_list);

        recyclerView = findViewById(R.id.recycler_view);
        fab = findViewById(R.id.fab);
        progressBar = findViewById(R.id.progressBar);

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
        else
            s = "assignedTo";
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                if (isNetworkConnected()) {
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
                Log.i("UserDetails", userDetails.getUserType());
                adapter = new LeadListItemAdapter(leadDetails, LeadsListActivity.this, profileManager.getCurrentUserType());
                recyclerView.setAdapter(adapter);
                setLayoutByUser();
            }
        };
    }

    private OnFetchLeadListListener onFetchLeadListListener() {
        return new OnFetchLeadListListener() {
            @Override
            public void onSuccess(LeadDetails l) {
                leadDetails.add(l);
                adapter.notifyDataSetChanged();
                //progress.dismiss();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailer() {
                showToastMessage(R.string.no_internet);
                //progress.dismiss();
                progressBar.setVisibility(View.GONE);
            }
        };
    }
}