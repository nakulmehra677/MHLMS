package com.mudrahome.mhlms.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.material.navigation.NavigationView
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.adapters.LeadListPagerAdapter
import com.mudrahome.mhlms.databinding.ActivityLeadListBinding
import com.mudrahome.mhlms.firebase.Authentication
import com.mudrahome.mhlms.firebase.Firestore
import com.mudrahome.mhlms.fragments.ChangePasswordFragment
import com.mudrahome.mhlms.fragments.LeadListFragment
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces
import com.mudrahome.mhlms.managers.ProfileManager
import com.mudrahome.mhlms.model.UserDetails
import com.mudrahome.mhlms.sharedPreferences.ProfileSP

class LeadListActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var profileManager: ProfileManager? = null
    private var firestore: Firestore? = null
    private var toggle: ActionBarDrawerToggle? = null
    private var userType: Int? = null
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

        firestore!!.getUsers(object : FirestoreInterfaces.OnGetUserDetails {
            override fun onSuccess(userDetails: UserDetails) {

                if (progress.isShowing) dismissProgressDialog()

                if (userDetails.userType!!.contains(getString(R.string.admin)) &&
                    userDetails.userType!!.contains(getString(R.string.salesman))
                ) {
                    userType = R.string.admin_and_salesman
                    openViewPager(userType!!)

                } else if (userDetails.userType!!.contains(getString(R.string.telecaller)) &&
                    userDetails.userType!!.contains(getString(R.string.teleassigner))
                ) {
                    userType = R.string.telecaller_and_teleassigner
                    openViewPager(userType!!)

                } else if (userDetails.userType!!.contains(getString(R.string.telecaller))) {
                    userType = R.string.telecaller
                    openFragment(userType!!)

                } else if (userDetails.userType!!.contains(getString(R.string.admin))) {
                    userType = R.string.admin
                    openFragment(userType!!)

                } else if (userDetails.userType!!.contains(getString(R.string.teleassigner))) {
                    userType = R.string.teleassigner
                    openFragment(userType!!)

                } else {
                    userType = R.string.salesman
                    openFragment(userType!!)
                }
            }

            override fun fail() {

            }
        }, profileManager!!.getuId())
    }

    private fun openFragment(userType: Int) {
        Log.d("dsfvdfv", userType.toString())

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, LeadListFragment(userType))
        transaction.commit()
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
}