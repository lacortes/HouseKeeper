package com.application.cortesluis.housekeeper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by luis_cortes on 6/27/17.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;

    public ViewPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment one = HomeFragment.newInstance();
                return one;
            case 1:
                Fragment two = TasksFragment.newInstance();
                return two;
            case 2:
                Fragment three = BillsFragment.newInstance();
                return three;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return this.tabCount;
    }
}
