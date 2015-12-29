package com.crossbow.app.x_timer.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.service.AppUsage;
import com.crossbow.app.x_timer.service.TickTrackerService;

import java.util.Map;

/**
 * Created by CuiH on 2015/12/29.
 */
public class MyFragment1 extends Fragment {
    private static final String TAG = "xyz";

    private Button mShowInfoButton;
    private TextView mTestInfoTextView;
    private Map<String, AppUsage> watchingList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page1, container, false);
        mShowInfoButton = (Button)view.findViewById(R.id.mShowInfoButton);
        mTestInfoTextView = (TextView)view.findViewById(R.id.mTestInfoTextView);

        mShowInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchingList = MainActivity.usageBinder.getWatchingList();
                Log.d(TAG, "onClick: " + watchingList.toString());
                mTestInfoTextView.setText(watchingList.get("com.tencent.mm")
                        .getPackageName() + watchingList.get("com.tencent.mm")
                        .getTotalTimeUsed() + "");
            }
        });

        return view;
    }
}
