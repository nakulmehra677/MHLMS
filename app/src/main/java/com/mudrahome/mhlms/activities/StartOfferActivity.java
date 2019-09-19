package com.mudrahome.mhlms.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import android.os.Bundle;

import com.mudrahome.mhlms.viewPagers.CustomViewPager;
import com.mudrahome.mhlms.fragments.FeedOfferDetailsFragment;
import com.mudrahome.mhlms.fragments.SelectUserForOfferFragment;
import com.mudrahome.mhlms.R;

public class StartOfferActivity extends AppCompatActivity {

    private CustomViewPager mPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_offer);

        mPager = findViewById(R.id.pager);

        mPager.disableScroll(true);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new FeedOfferDetailsFragment();
            else
                return new SelectUserForOfferFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
