package com.mudrahome.mhlms.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.adapters.LeadListPagerAdapter
import com.mudrahome.mhlms.databinding.ActivityLeadListBinding
import com.mudrahome.mhlms.firebase.Authentication
import com.mudrahome.mhlms.firebase.Firestore
import com.mudrahome.mhlms.fragments.ChangePasswordFragment
import com.mudrahome.mhlms.fragments.LeadDetailsFragment
import com.mudrahome.mhlms.fragments.LeadListFragment
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces
import com.mudrahome.mhlms.managers.ProfileManager
import com.mudrahome.mhlms.model.LeadDetails
import com.mudrahome.mhlms.model.UserDetails
import com.mudrahome.mhlms.sharedPreferences.ProfileSP

class LeadListActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var profileManager: ProfileManager? = null
    private var firestore: Firestore? = null
    private var toggle: ActionBarDrawerToggle? = null
    //    private var intnt: Intent? = null
    private var binding: ActivityLeadListBinding? = null
    private var profileSP: ProfileSP? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lead_list)

        setSupportActionBar(binding!!.toolbar)

        profileManager = ProfileManager()
        firestore = Firestore()
        profileSP = ProfileSP(this)

        toggle = ActionBarDrawerToggle(
            this,
            binding?.drawerLayout,
            binding?.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        binding?.drawerLayout?.addDrawerListener(toggle!!)
        toggle!!.syncState()
        binding!!.navView.setNavigationItemSelectedListener(this)

        showProgressDialog("Loading..", this)
//        intnt = intent

        firestore!!.getUsers(object : FirestoreInterfaces.OnGetUserDetails {
            override fun onSuccess(userDetails: UserDetails) {

                if (progress.isShowing) dismissProgressDialog()

                if (userDetails.userType!!.contains(getString(R.string.admin)) &&
                    userDetails.userType!!.contains(getString(R.string.salesman))
                ) {
                    checkNotification(R.string.admin_and_salesman)

                } else if (userDetails.userType!!.contains(getString(R.string.telecaller)) &&
                    userDetails.userType!!.contains(getString(R.string.teleassigner))
                ) {
                    checkNotification(R.string.telecaller_and_teleassigner)

                } else if (userDetails.userType!!.contains(getString(R.string.telecaller))) {
                    checkNotification(R.string.telecaller)

                } else if (userDetails.userType!!.contains(getString(R.string.admin))) {
                    checkNotification(R.string.admin)

                } else if (userDetails.userType!!.contains(getString(R.string.business_associate))) {
                    checkNotification(R.string.business_associate)

                } else if (userDetails.userType!!.contains(getString(R.string.teleassigner))) {
                    checkNotification(R.string.teleassigner)

                } else {
                    checkNotification(R.string.salesman)
                }
            }

            override fun fail() {

            }
        }, profileManager!!.getuId())
    }

    private fun checkNotification(userType: Int) {
//        if (intnt!!.hasExtra("UIDNotification")) {
//            val uid = intnt!!.getStringExtra("UIDNotification")
//
//            firestore!!.getLeadDetails(FirestoreInterfaces.OnLeadDetails { leadDetails ->
//                openLeadDetailsFragment(
//                    leadDetails!!,
//                    getString(userType)
//                )
//            }, uid!!)
//        } else {
        if (userType == R.string.admin_and_salesman || userType == R.string.telecaller_and_teleassigner)
            openViewPager(userType)
        else {
            openFragment(userType)
        }
//        }
    }

    private fun openLeadDetailsFragment(model: LeadDetails, currentUserType: String) {

        val leadDetailsFragment = LeadDetailsFragment(model, currentUserType)
        leadDetailsFragment.show(this@LeadListActivity.supportFragmentManager, "f")
    }

    private fun openFragment(userType: Int) {
        try {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(
                R.id.frame_layout,
                LeadListFragment(userType)
            )
            ft.commit()
        } catch (e: Exception) {

        }

    }

    private fun openViewPager(userType: Int) {
        binding!!.tabLayout.visibility = View.VISIBLE

        val pagerAdapter = LeadListPagerAdapter(supportFragmentManager)

        val tab1title: String?
        val tab2title: String?

        val type1: Int?
        val type2: Int?

        if (userType == R.string.telecaller_and_teleassigner) {
            tab1title = "Caller"
            tab2title = "Assign"
            type1 = R.string.telecaller
            type2 = R.string.teleassigner

        } else {
            tab1title = "Admin"
            tab2title = "Sales"
            type1 = R.string.admin
            type2 = R.string.salesman
        }
        pagerAdapter.addFragment(LeadListFragment(type1), tab1title)
        pagerAdapter.addFragment(LeadListFragment(type2), tab2title)

        binding!!.pager.adapter = pagerAdapter
        binding!!.tabLayout.setupWithViewPager(binding!!.pager)
    }

    override fun onBackPressed() {
        /*super.onBackPressed();*/

        /*Intent intent = new Intent();
        intent.putExtra("loggedIn", profileManager.checkUserExist());
        Log.d("profileManager", "onBackPressed: " + profileManager.checkUserExist());
        setResult(101,intent);
        finish();*/

        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
        finish()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        binding!!.drawerLayout.closeDrawers()

        when (menuItem.itemId) {
            R.id.profileDetails -> startActivity(
                Intent(
                    this@LeadListActivity,
                    ProfileDetailsActivity::class.java
                )
            )

            R.id.change_password -> if (isNetworkConnected) {
                showPasswordFragment()
            } else {
                showToastMessage(R.string.no_internet)
            }

            R.id.logout -> if (isNetworkConnected) {
                showLogOutWarning()

            } else {
                showToastMessage(R.string.no_internet)
            }
        }
        return true
    }

    private fun showLogOutWarning() {
        val build = AlertDialog.Builder(this@LeadListActivity)
        build.setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                profileManager!!.signOut()
                profileSP!!.clearSharePreference()                                      // Clear data from cache
                showToastMessage(R.string.logged_out)
                val intent = Intent(this@LeadListActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }.setNegativeButton("No") { _, _ ->

            }
        val alert = build.create()
        alert.show()
    }

    private fun showPasswordFragment() {
        val userDataSharedPreference =
            ProfileSP(this@LeadListActivity)

        ChangePasswordFragment.newInstance { oldPassword, newPassword ->

            showProgressDialog("Please wait...", this@LeadListActivity)
            val authentication = Authentication(this@LeadListActivity)
            authentication.UpdatePassword(
                oldPassword,
                newPassword,
                userDataSharedPreference.userEmail
            ) { result ->

                hideKeyboard(this@LeadListActivity)
                Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
                dismissProgressDialog()

            }
        }.show(supportFragmentManager, "changepassword")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.lead_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.notification -> if (isNetworkConnected) {
                val intent = Intent(this@LeadListActivity, NotificationActivity::class.java)
                startActivity(intent)
            } else
                showToastMessage(R.string.no_internet)

            android.R.id.home -> binding!!.drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
}