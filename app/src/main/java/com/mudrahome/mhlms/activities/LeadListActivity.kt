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
import com.mudrahome.mhlms.databinding.DrawerLeadListBinding
import com.mudrahome.mhlms.firebase.Authentication
import com.mudrahome.mhlms.firebase.Firestore
import com.mudrahome.mhlms.fragments.ChangePasswordFragment
import com.mudrahome.mhlms.fragments.LeadDetailsFragment
import com.mudrahome.mhlms.fragments.LeadListFragment
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces
import com.mudrahome.mhlms.managers.ProfileManager
import com.mudrahome.mhlms.model.LeadDetails
import com.mudrahome.mhlms.model.UserDetails
import com.mudrahome.mhlms.sharedPreferences.UserDataSharedPreference


class LeadListActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var profileManager: ProfileManager? = null
    private var firestore: Firestore? = null
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private var intnt: Intent? = null
    private var binding: DrawerLeadListBinding? = null
    private var userDataSharedPreference: UserDataSharedPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.drawer_lead_list)

        binding!!.activityLead.toolbarLeadList.inflateMenu(R.menu.lead_list_menu)
        setSupportActionBar(binding!!.activityLead.toolbarLeadList)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        profileManager = ProfileManager()
        firestore = Firestore()

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            binding?.drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        binding?.drawerLayout?.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()
        binding!!.navigationView.setNavigationItemSelectedListener(this)

        intnt = intent

        firestore!!.getUsers(object : FirestoreInterfaces.OnGetUserDetails {
            override fun onSuccess(userDetails: UserDetails) {
                profileManager!!.currentUserDetails = userDetails

                if (profileManager!!.currentUserType.contains(getString(R.string.admin)) &&
                    profileManager!!.currentUserType.contains(getString(R.string.salesman))
                ) {
                    checkNotification(R.string.admin_and_salesman)

                } else if (profileManager!!.currentUserType.contains(getString(R.string.telecaller)) &&
                    profileManager!!.currentUserType.contains(getString(R.string.teleassigner))
                ) {
                    checkNotification(R.string.telecaller_and_teleassigner)

                } else if (profileManager!!.currentUserType.contains(getString(R.string.telecaller))) {
                    checkNotification(R.string.telecaller)

                } else if (profileManager!!.currentUserType.contains(getString(R.string.admin))) {
                    checkNotification(R.string.admin)

                } else if (profileManager!!.currentUserType.contains(getString(R.string.business_associate))) {
                    checkNotification(R.string.business_associate)

                } else if (profileManager!!.currentUserType.contains(getString(R.string.teleassigner))) {
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
        if (intnt!!.hasExtra("UIDNotification")) {
            val uid = intnt!!.getStringExtra("UIDNotification")

            firestore!!.getLeadDetails(FirestoreInterfaces.OnLeadDetails { leadDetails ->
                openLeadDetailsFragment(
                    leadDetails!!,
                    getString(userType)
                )
            }, uid!!)
        } else {
            if (userType == R.string.admin_and_salesman || userType == R.string.telecaller_and_teleassigner)
                openViewPager(userType)
            else {
                openFragment(userType)
            }
        }
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
        binding!!.activityLead.tabLayout.visibility = View.VISIBLE

        if (userType == R.string.telecaller_and_teleassigner) {
            binding!!.activityLead.tabLayout.getTabAt(0)?.text = "Caller"
            binding!!.activityLead.tabLayout.getTabAt(1)?.text = "Assign"
        }

        val adapterViewPager = LeadListPagerAdapter(
            supportFragmentManager, userType, 2
        )
        binding!!.activityLead.pager.adapter = adapterViewPager

        binding!!.activityLead.pager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(binding!!.activityLead.tabLayout)
        )

        binding!!.activityLead.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding!!.activityLead.pager.currentItem = tab.position

                if (tab.position == 0) {
                    if (userType == R.string.admin_and_salesman)
                        openFragment(R.string.admin)
                    else
                        openFragment(R.string.telecaller)
                } else {
                    if (userType == R.string.admin_and_salesman)
                        openFragment(R.string.salesman)
                    else {
                        openFragment(R.string.teleassigner)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
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
                userDataSharedPreference!!.clearSharePreference()                                      // Clear data from cache
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
        val userDataSharedPreference = UserDataSharedPreference(this@LeadListActivity)

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