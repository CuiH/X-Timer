package com.crossbow.app.x_timer.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.service.TickTrackerService;
import com.crossbow.app.x_timer.utils.FileUtils;

/**
 * Created by CuiH on 2015/12/29.
 */
public class MyFragment3 extends Fragment {

    private MainActivity mainActivity;

    public MyFragment3() { }

    @SuppressLint("ValidFragment")
    public MyFragment3(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_page3, container, false);

        Button deleteall = (Button)view.findViewById(R.id.deleteall);
        deleteall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtils fileUtils = new FileUtils(getContext());
                fileUtils.deleteALLFiles();
            }
        });

        Button showall = (Button)view.findViewById(R.id.showall);
        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtils fileUtils = new FileUtils(getContext());
                fileUtils.showALLFiles();
            }
        });

        final Button stopNoti = (Button)view.findViewById(R.id.stop_noti);

        stopNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return view;
    }


}
