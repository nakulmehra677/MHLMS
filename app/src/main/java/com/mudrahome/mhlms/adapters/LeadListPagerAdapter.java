package com.mudrahome.mhlms.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mudrahome.mhlms.R;
import com.mudrahome.mhlms.fragments.LeadListFragment;
import com.mudrahome.mhlms.model.UserDetails;

public class LeadListPagerAdapter extends FragmentStatePagerAdapter {

    private int tabs;
    private UserDetails currentUserDetails;

    public LeadListPagerAdapter(FragmentManager fm, UserDetails currentUserDetails, int tabs) {
        super(fm);
        this.tabs = tabs;
        this.currentUserDetails = currentUserDetails;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LeadListFragment(R.string.admin, currentUserDetails);
            case 1:
                return new LeadListFragment(R.string.salesman, currentUserDetails);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabs;
    }
}
