package com.crossbow.app.x_timer.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.crossbow.app.x_timer.MainActivity;

/**
 * Created by CuiH on 2015/12/29.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] {"今日", "历史", "详情", "设置"};
    private MainActivity mainActivity;

    private MyFragment1 fragment1;
    private MyFragment2 fragment2;
    private MyFragment3 fragment3;
    private MyFragment4 fragment4;

    public MyPagerAdapter(FragmentManager fm, MainActivity activity) {
        super(fm);
        mainActivity = activity;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (fragment1 == null) {
                fragment1 = new MyFragment1(mainActivity);
                return  fragment1;
            } else {
                return fragment1;
            }
        } else if (position == 1) {
            if (fragment2 == null) {
                fragment2 = new MyFragment2(mainActivity);
                return fragment2;
            } else {
                return fragment2;
            }
        } else if (position == 2) {
            if (fragment3 == null) {
                fragment3 = new MyFragment3();
                return  fragment3;
            } else {
                return fragment3;
            }
        } else if (position == 3) {
            if (fragment4 == null) {
                fragment4 = new MyFragment4(mainActivity);
                return  fragment4;
            } else {
                return fragment4;
            }
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}