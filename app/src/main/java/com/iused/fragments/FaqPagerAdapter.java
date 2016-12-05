package com.iused.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class FaqPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public FaqPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                CompeteWishlistFragment tab1 = new CompeteWishlistFragment();
                return tab1;
            case 1:
                FixedPriceWishlistFragment tab2 = new FixedPriceWishlistFragment();
                return tab2;
            case 2:
                DonationsFragment tab3 = new DonationsFragment();
                return tab3;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
