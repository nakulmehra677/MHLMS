package com.mudrahome.mhlms.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.fragments.LeadListFragment

class LeadListPagerAdapter(fm: FragmentManager, private val userType: Int, private val tabs: Int) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        return if (userType == R.string.admin_and_salesman) {
            when (position) {
                0 -> LeadListFragment(R.string.admin)
                1 -> LeadListFragment(R.string.salesman)
                else -> null
            }
        } else {
            when (position) {
                0 -> LeadListFragment(R.string.telecaller)
                1 -> LeadListFragment(R.string.teleassigner)
                else -> null
            }
        }
    }

    override fun getCount(): Int {
        return tabs
    }
}
