package com.mudrahome.MHLMS.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.mudrahome.MHLMS.Adapters.LeadListPagerAdapter;
import com.mudrahome.MHLMS.Firebase.Authentication;
import com.mudrahome.MHLMS.Firebase.Firestore;
import com.mudrahome.MHLMS.Fragments.ChangePasswordFragment;
import com.mudrahome.MHLMS.Fragments.LeadListFragment;
import com.mudrahome.MHLMS.Interfaces.OnPasswordChange;
import com.mudrahome.MHLMS.Managers.ProfileManager;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.R;
import com.mudrahome.MHLMS.SharedPreferences.UserDataSharedPreference;

public class LeadListActivity extends BaseActivity {

    private ProfileManager profileManager;
    private Toolbar toolbar;
    LeadListFragment fragment;
    private Firestore firestore;
    private TabLayout tabLayout;
    private TabItem adminItem, salesItem;

    private Authentication authentication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads_list);

        toolbar = findViewById(R.id.toolbarLeadList);
        toolbar.inflateMenu(R.menu.lead_list_menu);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        adminItem = findViewById(R.id.admin_item);
        salesItem = findViewById(R.id.sales_item);

        profileManager = new ProfileManager();
        firestore = new Firestore();

        firestore.getUsers(new com.mudrahome.MHLMS.Interfaces.Firestore.OnGetUserDetails() {
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

            case R.id.changePassword:
                if (isNetworkConnected()) {

                    Log.d("changed password", "onOptionsItemSelected: button selected");
                    final UserDataSharedPreference userDataSharedPreference = new UserDataSharedPreference(LeadListActivity.this);
                    ChangePasswordFragment.newInstance(new ChangePasswordFragment.OnPasswordChangedClicked() {
                        @Override
                        public void onPasswordChange(String oldPassword, String newPassword) {

                            showProgressDialog("Please wait...",LeadListActivity.this);
                            Authentication authentication = new Authentication(LeadListActivity.this);
                            authentication.UpdatePassword(oldPassword, newPassword, userDataSharedPreference.getUserEmail(), new OnPasswordChange() {
                                @Override
                                public void onSucess(String result) {
                                    Log.d("Password Updated", "onSucess: " + result);
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    dismissProgressDialog();
                                }
                            });
                        }
                    }).show(getSupportFragmentManager(),"changepassword");


                }
        }
        return super.onOptionsItemSelected(item);
    }

    /*private void checkconfirmpassword(String newpass, String confirmpass, String currentapss, ProgressDialog progressDialog,AlertDialog alertDialog) {

        hideKeyboard(LeadListActivity.this);

        UserDataSharedPreference userDataSharedPreference = new UserDataSharedPreference(LeadListActivity.this);

        if(newpass.isEmpty() && confirmpass.isEmpty() && currentapss.isEmpty()){
            showToastMessage(R.string.fill_all_fields);
        }else {

            if(newpass.matches(confirmpass)){

                authentication = new Authentication(LeadListActivity.this);

                authentication.UpdatePassword(currentapss,newpass,userDataSharedPreference.getUserEmail(),progressDialog,alertDialog);
            }else{
                Toast.makeText(getApplicationContext(), "Password doestn't matched", Toast.LENGTH_SHORT).show();
            }


        }

    }*/
}