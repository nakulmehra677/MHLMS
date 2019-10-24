package com.mudrahome.mhlms.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.databinding.ActivityProfileDetailsBinding
import com.mudrahome.mhlms.firebase.Firestore
import com.mudrahome.mhlms.fragments.EditPhoneFragment
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces
import com.mudrahome.mhlms.managers.ProfileManager
import com.mudrahome.mhlms.model.Profile
import com.mudrahome.mhlms.sharedPreferences.UserDataSharedPreference

class ProfileDetailsActivity : BaseActivity() {

    private var preference: UserDataSharedPreference? = null
    private var userDesignation = ""
    private var userlocation = ""
    private var strContact: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityProfileDetailsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_profile_details)

        preference = UserDataSharedPreference(this)
        val userType: String
        userType = preference!!.userType

        if (userType == getString(R.string.admin))
            userDesignation = getString(R.string.admin)
        else if (userType == getString(R.string.telecaller))
            userDesignation = getString(R.string.telecaller)
        else if (userType == getString(R.string.business_associate))
            userDesignation = getString(R.string.business_associate)
        else if (userType == getString(R.string.teleassigner))
            userDesignation = "Caller"
        else
            userDesignation = getString(R.string.salesman)

        val locationset: Set<String>
        locationset = preference!!.location

        for (s in locationset) {
            if (userlocation == "") {
                userlocation = s
            } else {
                userlocation += ",$s"
            }
        }

        binding.user = Profile(
            preference?.userName,
            preference?.contactNumber,
            preference?.userEmail,
            userlocation,
            userDesignation
        )

        binding.editNumber.setOnClickListener { view ->
            EditPhoneFragment.newInstance(preference!!.contactNumber) { number ->
                val firestore = Firestore()
                val manager = ProfileManager()
                strContact = number
                firestore.updateUserDetails(object : FirestoreInterfaces.OnUpdateUser {
                    override fun onSuccess() {
                        preference!!.contactNumber = strContact
                        binding.user = Profile(
                            preference?.userName,
                            strContact,
                            preference?.userEmail,
                            userlocation,
                            userDesignation
                        )
                        showToastMessage(R.string.updated)
                    }

                    override fun onFail() {
                        showToastMessage(R.string.update_fail)
                    }
                }, number, manager.getuId())
            }.show(supportFragmentManager, "promo")
        }
    }
}