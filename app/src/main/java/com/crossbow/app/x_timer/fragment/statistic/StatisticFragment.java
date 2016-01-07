package com.crossbow.app.x_timer.fragment.statistic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.detail.app_detail.AppDetailActivity;
import com.crossbow.app.x_timer.utils.FileUtils;
import com.devspark.progressfragment.ProgressFragment;

/**
 * Created by CuiH on 2015/12/29.
 */
public class StatisticFragment extends ProgressFragment {

    private final int SPLASH_DISPLAY_LENGTH = 500;

    private final String TAG = "StatisticFragment";

    private MainActivity mainActivity;

    private View realView;

    private boolean firstTime;

    public StatisticFragment() { }

    @SuppressLint("ValidFragment")
    public StatisticFragment(MainActivity activity) {
        mainActivity = activity;
        firstTime = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");

        realView = inflater.inflate(R.layout.statistic_fragment, container, false);

        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");

        super.onActivityCreated(savedInstanceState);
        // Setup content view
        setContentView(realView);
        // ...
        setContentShown(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "setUserVisibleHint: ");

        // 可见时
        if (isVisibleToUser) {
            if (firstTime) {
                Log.d(TAG, "im here !!!!!");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setContentShown(true);

                    }
                }, SPLASH_DISPLAY_LENGTH);

                firstTime = false;
            }
        }

        super.setUserVisibleHint(isVisibleToUser);
    }




}
