package com.crossbow.app.x_timer.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.service.AppUsage;
import com.crossbow.app.x_timer.service.TickTrackerService;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by CuiH on 2015/12/29.
 */
public class MyFragment1 extends Fragment {
    private final String TAG = "MyFragment1";

    private Button mShowInfoButton;
    private Button controlService;
    private Button addQQ;
    private Button removeQQ;
    private Button clearFile;
    private TextView mTestInfoTextView;
    private Map<String, AppUsage> watchingList;
    private MainActivity mainActivity;

    public MyFragment1() { }

    @SuppressLint("ValidFragment")
    public MyFragment1(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page1, container, false);
        mShowInfoButton = (Button)view.findViewById(R.id.mShowInfoButton);
        controlService = (Button)view.findViewById(R.id.controlService);
        mTestInfoTextView = (TextView)view.findViewById(R.id.mTestInfoTextView);

        addQQ = (Button)view.findViewById(R.id.addQQ);
        removeQQ = (Button)view.findViewById(R.id.removeQQ);

        if (isWorking()) {
            controlService.setText("stop service");
        } else {
            controlService.setText("start service");
        }

        mShowInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchingList = mainActivity.getBinder().getWatchingList();
                StringBuilder builder = new StringBuilder();

                for (Map.Entry<String, AppUsage> entry : watchingList.entrySet()) {
                    builder.append(entry.getValue().getPackageName() + ":\n");
                    AppUsage.History today = entry.getValue().getUsingHistory().get(TickTrackerService.getDateInString(new Date()));
                    builder.append("使用次数："+today.getUsedCount()+":\n");
                    builder.append("使用总时长："+today.getTotalTime()+":\n");
                    ArrayList<AppUsage.History.Record> records = today.getUsingRecord();
                    for (int i = 1; i <= records.size(); i++) {
                        builder.append("第"+i+"次持续："+records.get(i-1).getDuration()+"\n");
                    }
                }

                mTestInfoTextView.setText(builder.toString());
            }
        });

        controlService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWorking()) {
                    mainActivity.stopTickService();
                    controlService.setText("start service");
                    
                } else {
                    mainActivity.startTickService();
                    controlService.setText("stop service");
                }
            }
        });

        addQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWorking()) {
                    Toast.makeText(mainActivity, "please start service first!"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mainActivity.getBinder().addAppToWatchingList("com.tencent.mobileqq")) {
                    Toast.makeText(mainActivity, "success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mainActivity, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        });

        removeQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWorking()) {
                    Toast.makeText(mainActivity, "please start service first!"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mainActivity.getBinder().removeAppFromWatchingLise("com.tencent.mobileqq")) {
                    Toast.makeText(mainActivity, "success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mainActivity, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clearFile = (Button)view.findViewById(R.id.clearFile);
        clearFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File root = new File("/data/data/com.crossbow.app.x_timer/");
                File[] files = root.listFiles();
                for (File file : files) {
                    file.delete();
                }
            }
        });

        return view;
    }

    private boolean isWorking() {
        ActivityManager myAM = (ActivityManager) mainActivity.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);

        if (myList.size() <= 0) {
            return false;
        }

        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals("com.crossbow.app.x_timer.service.TickTrackerService")) {
                return true;
            }
        }
        return false;
    }
}
