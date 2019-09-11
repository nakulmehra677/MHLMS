package com.mudrahome.MHLMS.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.mudrahome.MHLMS.Managers.ProfileManager;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.R;
import com.mudrahome.MHLMS.SharedPreferences.UserDataSharedPreference;

public class LeadListActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener , View.OnClickListener {

    private ProfileManager profileManager;
    private Toolbar toolbar;
    private Firestore firestore;
    private TabLayout tabLayout;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FloatingActionButton filter, fab;
    private int userType1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout_leadlist);

        toolbar = findViewById(R.id.toolbarLeadList);
        toolbar.inflateMenu(R.menu.lead_list_menu);
        setSupportActionBar(toolbar);


        filter = findViewById(R.id.filter1);
        fab = findViewById(R.id.fab);

        filter.setOnClickListener(this);

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
                    userType1 = R.string.telecaller;
                } else if (profileManager.getCurrentUserType().contains(getString(R.string.admin))) {
                    openFragment(R.string.admin);
                    userType1 = R.string.admin;
                } else {
                    openFragment(R.string.salesman);
                    userType1 = R.string.salesman;
                }
            }

            @Override
            public void fail() {

            }
        }, profileManager.getuId());
    }


    @SuppressLint("RestrictedApi")
    private void setFabButtonByUser(int userType) {

        if (userType == R.string.telecaller) {
            fab.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.ic_add_white_24dp);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(LeadListActivity.this, FeedCustomerDetailsActivity.class));
                }
            });
        } else if (userType == R.string.admin) {
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
        } else {
            /*fab.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));*/

            fab.setVisibility(View.GONE);
        }


    }

    private void openFragment(int userType) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, new LeadListFragment(userType));
        ft.commit();
    }

    private void openViewPager(int userType) {
        tabLayout.setVisibility(View.VISIBLE);
        userType1 = R.string.admin;
        setFabButtonByUser(userType1);
        final ViewPager vpPager = findViewById(R.id.pager);
        LeadListPagerAdapter adapterViewPager = new LeadListPagerAdapter(getSupportFragmentManager(), 2);
        vpPager.setAdapter(adapterViewPager);

        vpPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0)
                    userType1 = R.string.admin;
                else
                    userType1 = R.string.salesman;

                setFabButtonByUser(userType1);
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
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int u) {

            }
        });
        androidx.appcompat.app.AlertDialog alert = build.create();
        alert.show();
    }

    private void showPasswordFragment() {
        final UserDataSharedPreference userDataSharedPreference =
                new UserDataSharedPreference(LeadListActivity.this);

        ChangePasswordFragment.newInstance(new ChangePasswordFragment.OnPasswordChangedClicked() {
            @Override
            public void onPasswordChange(String oldPassword, String newPassword) {

                showProgressDialog("Please wait...", LeadListActivity.this);
                Authentication authentication = new Authentication(LeadListActivity.this);
                authentication.UpdatePassword(
                        oldPassword,
                        newPassword,
                        userDataSharedPreference.getUserEmail(),
                        new OnPasswordChange() {
                            @Override
                            public void onSucess(String result) {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                dismissProgressDialog();
                                hideKeyboard(LeadListActivity.this);

                            }
                        });
            }
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

    @Override
    public void onClick(View view) {
        if (isNetworkConnected()) {
            switch (view.getId()) {
                case R.id.filter1:
                    Intent intent = new Intent(LeadListActivity.this, FilterActivity.class);
                    intent.putExtra("userType", userType1);
                    /*startActivity(intent);*/
                    startActivityForResult(intent, 201);
                    break;
            }
        }
    }

}