package com.mudrahome.MHLMS.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.mudrahome.MHLMS.Adapters.LeadListPagerAdapter;
import com.mudrahome.MHLMS.Firebase.Authentication;
import com.mudrahome.MHLMS.Firebase.Firestore;
import com.mudrahome.MHLMS.Fragments.ChangePasswordFragment;
import com.mudrahome.MHLMS.Fragments.LeadListFragment;
import com.mudrahome.MHLMS.Interfaces.FirestoreInterfaces;
import com.mudrahome.MHLMS.Interfaces.OnPasswordChange;
//import com.mudrahome.MHLMS.Managers.LeadManager;
import com.mudrahome.MHLMS.Managers.ProfileManager;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.R;
import com.mudrahome.MHLMS.SharedPreferences.UserDataSharedPreference;


public class LeadListActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ProfileManager profileManager;
    private Toolbar toolbar;
    private Firestore firestore;
    private TabLayout tabLayout;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout_leadlist);

        toolbar = findViewById(R.id.toolbarLeadList);
        toolbar.inflateMenu(R.menu.lead_list_menu);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationdrawer_dashboard);

        profileManager = new ProfileManager();
        firestore = new Firestore();

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);

        firestore.getUsers(new FirestoreInterfaces.OnGetUserDetails() {
            @Override
            public void onSuccess(UserDetails userDetails) {
                profileManager.setCurrentUserDetails(userDetails);

                if (profileManager.getCurrentUserType().contains(getString(R.string.admin)) &&
                        profileManager.getCurrentUserType().contains(getString(R.string.salesman))) {
                    openViewPager(R.string.admin_and_salesman);
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

    private void openViewPager(int userType) {
        tabLayout.setVisibility(View.VISIBLE);
        openFragment(R.string.admin);
        final ViewPager vpPager = findViewById(R.id.pager);
        LeadListPagerAdapter adapterViewPager = new LeadListPagerAdapter(getSupportFragmentManager(), 2);
        vpPager.setAdapter(adapterViewPager);

        vpPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    openFragment(R.string.admin);
                } else {
                    openFragment(R.string.salesman);
                }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawers();

        switch (menuItem.getItemId()) {
            case R.id.profileDetails:
                startActivity(new Intent(LeadListActivity.this, ProfileDetailsActivity.class));
                break;

            case R.id.change_password:
                if (isNetworkConnected()) {
                    showPasswordFragment();
                } else {
                    showToastMessage(R.string.no_internet);
                }
                break;

            case R.id.logout:
                if (isNetworkConnected()) {
                    showLogOutWarning();

                } else {
                    showToastMessage(R.string.no_internet);
                }
                break;
        }
        return true;
    }

    private void showLogOutWarning() {
        AlertDialog.Builder build = new AlertDialog.Builder(LeadListActivity.this);
        build.setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int u) {
                        profileManager.signOut();
                        showToastMessage(R.string.logged_out);
//                        kghv k
                        //SharedPreferences.Editor editor = sharedPreferences.edit();
                        //editor.clear();
                        onBackPressed();
                    }
                }).setNegativeButton("No", (dialogInterface, u) -> {

        });
        androidx.appcompat.app.AlertDialog alert = build.create();
        alert.show();
    }

    private void showPasswordFragment() {
        final UserDataSharedPreference userDataSharedPreference =
                new UserDataSharedPreference(LeadListActivity.this);

        ChangePasswordFragment.newInstance((oldPassword, newPassword) -> {

            showProgressDialog("Please wait...", LeadListActivity.this);
            Authentication authentication = new Authentication(LeadListActivity.this);
            authentication.UpdatePassword(
                    oldPassword,
                    newPassword,
                    userDataSharedPreference.getUserEmail(),
                    new OnPasswordChange() {
                        @Override
                        public void onSucess(String result) {

                            hideKeyboard(LeadListActivity.this);
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            dismissProgressDialog();

                        }
                    });
        }).show(getSupportFragmentManager(), "changepassword");
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
            case R.id.notification:
                if (isNetworkConnected()) {
                    Intent intent = new Intent(LeadListActivity.this, NotificationActivity.class);
                    startActivity(intent);
                } else
                    showToastMessage(R.string.no_internet);
                break;

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}