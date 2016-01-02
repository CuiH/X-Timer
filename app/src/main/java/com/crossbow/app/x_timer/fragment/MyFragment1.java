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

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by CuiH on 2015/12/29.
 */
public class MyFragment1 extends Fragment {
    private final String TAG = "MyFragment1";

    private Button mShowInfoButton;
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
        mTestInfoTextView = (TextView)view.findViewById(R.id.mTestInfoTextView);

        mShowInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(mainActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定要清空记录吗")
                        .setContentText("该操作不可恢复")
                        .setConfirmText("确认")
                        .setCancelText("手滑了")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.setTitleText("已删除")
                                        .setContentText("应用历史记录已清空")
                                        .setConfirmText("好的")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        }).show();
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
