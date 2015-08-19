package com.ciao.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ciao.app.views.fragments.Splash1Fragment;
import com.ciao.app.views.fragments.Splash2Fragment;

/**
 * Created by rajat on 22/1/15.
 * 
 * This Adapter is used in Splash screen.
 */
 
public class SplashPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;
    public SplashPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Splash1Fragment();
            case 1:
                return new Splash2Fragment();
            /*case 2:
                return new Splash2Fragment();
            case 3:
                return new Splash2Fragment();*/
            default:
                return new Splash1Fragment();
        }

    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
