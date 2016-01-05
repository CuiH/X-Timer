package com.crossbow.app.x_timer.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.utils.FileUtils;
import com.crossbow.app.x_timer.add_app.AddAppActivity;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by CuiH on 2015/12/29.
 */
public class SettingFragment extends Fragment implements AdapterView.OnItemClickListener{

    private MainActivity mainActivity;

    private SettingAdapter settingAdapter;
    private List<SettingInfo> settingList;
    private ListView listView;

    public SettingFragment() { }

    @SuppressLint("ValidFragment")
    public SettingFragment(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);

        initSettingList();
        initAdapter(view);

        return  view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToggleButton tv = (ToggleButton)view.findViewById(R.id.settingButton);

        switch (position) {
            case 0:
                if (mainActivity.isWorking()) {
                    mainActivity.stopTickService();
                    tv.setChecked(false);
                } else {
                    mainActivity.startTickService();
                    tv.setChecked(true);
                }

                break;
            case 1:
                if (mainActivity.shouldStartWhenBoot()) {
                    mainActivity.updateShouldStartWhenBoot(false);
                    tv.setChecked(false);

                    Toast.makeText(mainActivity, "已设置不开机启动", Toast.LENGTH_SHORT).show();
                } else {
                    mainActivity.updateShouldStartWhenBoot(true);
                    tv.setChecked(true);

                    Toast.makeText(mainActivity, "已设置开机启动", Toast.LENGTH_SHORT).show();
                }

                break;
            case 2:
                if (mainActivity.shouldShowNotification()) {
                    if (mainActivity.isWorking()) {
                        mainActivity.getBinder().changeNotificationState(false);
                    } else {
                        Toast.makeText(mainActivity, "服务未启动", Toast.LENGTH_SHORT).show();
                    }

                    mainActivity.updateShouldShowNotification(false);
                    tv.setChecked(false);
                } else {
                    if (mainActivity.isWorking()) {
                        mainActivity.getBinder().changeNotificationState(true);
                    } else {
                        Toast.makeText(mainActivity, "服务未启动", Toast.LENGTH_SHORT).show();
                    }

                    mainActivity.updateShouldShowNotification(true);
                    tv.setChecked(true);
                }

                break;
            case 3:
                if (!mainActivity.isWorking()) {
                    Toast.makeText(mainActivity, "请先开启监听服务", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getContext(), AddAppActivity.class);
                startActivity(intent);

                break;
            case 4:
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
            case 5:
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
            case 6:
                Toast.makeText(mainActivity, "未实现", Toast.LENGTH_SHORT).show();

                break;
            case 7:
                Toast.makeText(mainActivity, "未实现", Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }
    }

    // handle the adapter
    private void initAdapter(View view) {
        settingAdapter = new SettingAdapter(mainActivity, R.layout.setting_item, settingList);

        listView = (ListView)view.findViewById(R.id.setting_list);
        listView.setAdapter(settingAdapter);

        listView.setOnItemClickListener(this);
    }

    // init the setting list
    private void initSettingList() {
        settingList = new ArrayList<>();
        SettingInfo setting1 = new SettingInfo("监听服务", "开启后才能监听应用使用情况", 2, 1);
        settingList.add(setting1);
        SettingInfo setting2 = new SettingInfo("开机启动", "", 2, 2);
        settingList.add(setting2);
        SettingInfo setting4 = new SettingInfo("通知栏图标", "显示或隐藏通知栏图标", 2, 1);
        settingList.add(setting4);
        SettingInfo setting3 = new SettingInfo("管理监听列表", "添加或删除要监听的应用", 1, 1);
        settingList.add(setting3);
        SettingInfo setting5 = new SettingInfo("清除历史记录", "清空所有应用使用记录", 1, 1);
        settingList.add(setting5);
        SettingInfo setting6 = new SettingInfo("清空监听列表", "", 1, 2);
        settingList.add(setting6);
        SettingInfo setting7 = new SettingInfo("已知待修复缺陷", "", 1, 2);
        settingList.add(setting7);
        SettingInfo setting8 = new SettingInfo("帮助", "如何开启权限", 1, 1);
        settingList.add(setting8);
    }

}
