package com.crossbow.app.x_timer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.crossbow.app.x_timer.activity.MainActivity;
import com.crossbow.app.x_timer.fragment.HistoryFragment;
import com.crossbow.app.x_timer.fragment.HomeFragment;
import com.crossbow.app.x_timer.fragment.SettingFragment;
import com.crossbow.app.x_timer.fragment.StatisticFragment;

/**
 * Created by CuiH on 2015/12/29.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] {"今日", "历史", "详情", "设置"};
    private MainActivity mainActivity;

    private HomeFragment fragment1;
    private HistoryFragment fragment2;
    private StatisticFragment fragment3;
    private SettingFragment fragment4;

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
                fragment1 = new HomeFragment(mainActivity);
                return  fragment1;
            } else {
                return fragment1;
            }
        } else if (position == 1) {
            if (fragment2 == null) {
                fragment2 = new HistoryFragment(mainActivity);
                return fragment2;
            } else {
                return fragment2;
            }
        } else if (position == 2) {
            if (fragment3 == null) {
                fragment3 = new StatisticFragment(mainActivity);
                return  fragment3;
            } else {
                return fragment3;
            }
        } else if (position == 3) {
            if (fragment4 == null) {
                fragment4 = new SettingFragment(mainActivity);
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

    public void setAllFirstTime(boolean flag) {
        fragment1.setForceRefresh(flag);
        fragment2.setForceRefresh(flag);
        fragment3.setForceRefresh(flag);
        fragment4.setForceRefresh(flag);
    }

}