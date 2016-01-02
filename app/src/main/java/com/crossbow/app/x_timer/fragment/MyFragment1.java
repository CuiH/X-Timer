package com.crossbow.app.x_timer.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.crossbow.app.x_timer.add_app.AddAppActivity;
import com.crossbow.app.x_timer.service.AppUsage;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by CuiH on 2015/12/29.
 */
public class MyFragment1 extends Fragment {
    private final String TAG = "MyFragment1";

    private Button mShowInfoButton;
    private Button controlService;
    private Button addApp;
    private Button clearFile;
    private Button showFile;
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

        addApp = (Button)view.findViewById(R.id.add_app_button);

        if (mainActivity.isWorking()) {
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
                    AppUsage.History today = entry.getValue().getUsingHistory().get(AppUsage.getDateInString(new Date()));
                    if (today == null) {
                        builder.append("not used\n");
                    } else {
                        builder.append("使用次数："+today.getUsedCount()+":\n");
                        builder.append("使用总时长："+today.getTotalTime()+":\n");
                        ArrayList<AppUsage.History.Record> records = today.getUsingRecord();
                        for (int i = 1; i <= records.size(); i++) {
                            builder.append("第"+i+"次持续："+records.get(i-1).getDuration()+"\n");
                        }
                    }
                }
                mTestInfoTextView.setText(builder.toString());
            }
        });

        controlService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity.isWorking()) {
                    mainActivity.stopTickService();
                    controlService.setText("start service");
                    
                } else {
                    mainActivity.startTickService();
                    controlService.setText("stop service");
                }
            }
        });

        addApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mainActivity.isWorking()) {
                    Toast.makeText(mainActivity, "请先开启服务", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getContext(), AddAppActivity.class);
                startActivity(intent);
            }
        });

        clearFile = (Button)view.findViewById(R.id.clearFile);
        clearFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File root = new File("/data/data/com.crossbow.app.x_timer/files");
                File[] files = root.listFiles();
                for (File file : files) {
                    file.delete();
                }
            }
        });

        showFile = (Button)view.findViewById(R.id.showFile);
        showFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File root = new File("/data/data/com.crossbow.app.x_timer/files");
                File[] files = root.listFiles();
                for (File file : files) {
                    System.out.println("we have file: "+file.getName());
                }
            }
        });

        return view;
    }

}
