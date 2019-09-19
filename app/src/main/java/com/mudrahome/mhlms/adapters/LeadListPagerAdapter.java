package com.mudrahome.mhlms.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mudrahome.mhlms.fragments.LeadListFragment;
import com.mudrahome.mhlms.R;

public class LeadListPagerAdapter extends FragmentStatePagerAdapter {

    private int tabs;

    public LeadListPagerAdapter(FragmentManager fm, int tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LeadListFragment(R.string.admin);
            case 1:
                return new LeadListFragment(R.string.salesman);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabs;
    }
}
