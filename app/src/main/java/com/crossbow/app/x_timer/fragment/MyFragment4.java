package com.crossbow.app.x_timer.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.Utils.FileUtils;
import com.crossbow.app.x_timer.add_app.AddAppActivity;

import java.util.logging.LogRecord;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * Created by CuiH on 2015/12/29.
 */
public class MyFragment4 extends Fragment implements View.OnClickListener {

    private MainActivity mainActivity;

    private FancyButton setting_service;
    private FancyButton setting_auto;
    private FancyButton setting_list;
    private FancyButton setting_notification;
    private FancyButton setting_clear;
    private FancyButton setting_help;
    private FancyButton setting_clear_list;


    public MyFragment4() { }

    @SuppressLint("ValidFragment")
    public MyFragment4(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page4, container, false);

        initFancyButtons(view);
        initText();

        return  view;
    }

    // init all FancyButtons
    private void initFancyButtons(View view) {
        setting_service = (FancyButton)view.findViewById(R.id.setting_service);
        setting_auto = (FancyButton)view.findViewById(R.id.setting_auto);
        setting_clear = (FancyButton)view.findViewById(R.id.setting_clear);
        setting_clear_list = (FancyButton)view.findViewById(R.id.setting_clear_list);
        setting_help = (FancyButton)view.findViewById(R.id.setting_help);
        setting_list = (FancyButton)view.findViewById(R.id.setting_list);
        setting_notification = (FancyButton)view.findViewById(R.id.setting_notification);

        setting_help.setOnClickListener(this);
        setting_auto.setOnClickListener(this);
        setting_service.setOnClickListener(this);
        setting_clear_list.setOnClickListener(this);
        setting_list.setOnClickListener(this);
        setting_notification.setOnClickListener(this);
        setting_clear.setOnClickListener(this);
    }

    // init the text in the FancyButtons
    private void initText() {
        if (mainActivity.isWorking()) {
            setting_service.setText("关闭");
        } else {
            setting_service.setText("启动");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.setting_service:
                if (mainActivity.isWorking()) {
                    mainActivity.stopTickService();
                    setting_service.setText("启动");
                } else {
                    mainActivity.startTickService();
                    setting_service.setText("关闭");
                }

                break;
            case R.id.setting_clear:
                if (mainActivity.isWorking()) {
                    Toast.makeText(mainActivity, "请先关闭监听服务", Toast.LENGTH_SHORT).show();
                    return;
                }

                new SweetAlertDialog(mainActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定要清空记录吗")
                        .setContentText("该操作不可恢复")
                        .setConfirmText("确认")
                        .setCancelText("手滑了")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                FileUtils fileUtils = new FileUtils(mainActivity);
                                fileUtils.deleteAllAppInfo();

                                sDialog.setTitleText("已删除")
                                        .setContentText("应用历史记录已清空")
                                        .setConfirmText("好的")
                                        .setConfirmClickListener(null)
                                        .showCancelButton(false)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        }).show();

                break;
            case R.id.setting_clear_list:
                if (mainActivity.isWorking()) {
                    Toast.makeText(mainActivity, "请先关闭监听服务", Toast.LENGTH_SHORT).show();
                    return;
                }

                new SweetAlertDialog(mainActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定要清空列表吗")
                        .setContentText("该操作不可恢复")
                        .setConfirmText("确认")
                        .setCancelText("手滑了")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                FileUtils fileUtils2 = new FileUtils(mainActivity);
                                fileUtils2.deleteAppList();

                                sDialog.setTitleText("已删除")
                                        .setContentText("应用监听列表已清空")
                                        .setConfirmText("好的")
                                        .setConfirmClickListener(null)
                                        .showCancelButton(false)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        }).show();

                break;
            case R.id.setting_auto:
                Toast.makeText(mainActivity, "未实现", Toast.LENGTH_SHORT).show();

                break;
            case R.id.setting_help:
                Toast.makeText(mainActivity, "未实现", Toast.LENGTH_SHORT).show();

                break;
            case R.id.setting_notification:
                Toast.makeText(mainActivity, "未实现", Toast.LENGTH_SHORT).show();

                break;
            case R.id.setting_list:
                if (!mainActivity.isWorking()) {
                    Toast.makeText(mainActivity, "请先开启监听服务", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getContext(), AddAppActivity.class);
                startActivity(intent);

                break;
            default:
                break;
        }
    }
}
