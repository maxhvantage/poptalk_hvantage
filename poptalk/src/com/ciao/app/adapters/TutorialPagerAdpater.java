package com.ciao.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ciao.app.views.fragments.Tutorial1Fragment;
import com.ciao.app.views.fragments.Tutorial2Fragment;
import com.ciao.app.views.fragments.Tutorial3Fragment;


/*
 * This Adapter is used in screen where user can see the tutorila screen.
 */
public class TutorialPagerAdpater extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;
    public TutorialPagerAdpater(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Tutorial1Fragment();
            case 1:
                return new Tutorial2Fragment();
            case 2:
            	return new Tutorial3Fragment();
            default:
                return new Tutorial1Fragment();
        }

    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
