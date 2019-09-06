package com.mudrahome.MHLMS.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.mudrahome.MHLMS.Adapters.LeadListPagerAdapter;
import com.mudrahome.MHLMS.Firebase.Firestore;
import com.mudrahome.MHLMS.Fragments.LeadListFragment;
import com.mudrahome.MHLMS.Managers.ProfileManager;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.R;

public class LeadListActivity extends BaseActivity {

    private ProfileManager profileManager;
    private Toolbar toolbar;
    private Firestore firestore;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_list);

        toolbar = findViewById(R.id.toolbarLeadList);
        toolbar.inflateMenu(R.menu.lead_list_menu);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        profileManager = new ProfileManager();
        firestore = new Firestore();

        firestore.getUsers(new com.mudrahome.MHLMS.Interfaces.Firestore.OnGetUserDetails() {
            @Override
            public void onSuccess(UserDetails userDetails) {
                profileManager.setCurrentUserDetails(userDetails);

                if (profileManager.getCurrentUserType().contains(getString(R.string.admin)) &&
                        profileManager.getCurrentUserType().contains(getString(R.string.salesman))) {
                    openViewPager();
                } else if (profileManager.getCurrentUserType().contains(getString(R.string.telecaller))) {
                    openFragment(R.string.telecaller);
                } else if (profileManager.getCurrentUserType().contains(getString(R.string.admin))) {
                    openFragment(R.string.admin);
                } else {
                    openFragment(R.string.salesman);
                }
            }

            @Override
            public void fail() {

            }
        }, profileManager.getuId());
    }

    private void openFragment(int userType) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, new LeadListFragment(userType));
        ft.commit();
    }

    private void openViewPager() {
        tabLayout.setVisibility(View.VISIBLE);
        final ViewPager vpPager = findViewById(R.id.pager);
        LeadListPagerAdapter adapterViewPager = new LeadListPagerAdapter(getSupportFragmentManager(), 2);
        vpPager.setAdapter(adapterViewPager);

        vpPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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

                    AlertDialog.Builder build = new AlertDialog.Builder(LeadListActivity.this);
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
}