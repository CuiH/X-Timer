package com.crossbow.app.x_timer.entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.crossbow.app.x_timer.activity.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.entity.SettingInfo;


import java.util.List;

/**
 * Created by wanglx on 2016/1/3.
 */
public class SettingAdapter extends ArrayAdapter<SettingInfo> {
    private int resourceId;
    // 已选的
    private MainActivity mainActivity;

    public SettingAdapter(Context context, int textViewResourceId, List<SettingInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        mainActivity = (MainActivity)context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SettingInfo settingInfo = getItem(position);
        View view;
        ViewHolder vh = new ViewHolder();
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            vh.tv1 = (TextView)view.findViewById(R.id.settingText);
            vh.tv2 = (TextView)view.findViewById(R.id.settingDescription);
            vh.tb = (ToggleButton)view.findViewById(R.id.settingButton);
            view.setTag(vh);
        } else {
            view = convertView;
            vh = (ViewHolder)view.getTag();
        }

        vh.tv1.setText(settingInfo.getSettingName());

        if (settingInfo.hasDescription()) {
            vh.tv2.setText(settingInfo.getSettingDescription());
            vh.tv2.setVisibility(View.VISIBLE);
        } else {
            vh.tv2.setVisibility(View.GONE);
        }

        if (position == 0) {          // 监听服务
            if (mainActivity.isWorking()) vh.tb.setChecked(true);
            else vh.tb.setChecked(false);
        } else if (position == 1) {   // 开机启动
            if (mainActivity.shouldStartWhenBoot())  vh.tb.setChecked(true);
            else  vh.tb.setChecked(false);
        } else if (position == 2) {   // 通知栏图标
            if (mainActivity.shouldShowNotification()) vh.tb.setChecked(true);
            else vh.tb.setChecked(false);
        }

        if (settingInfo.getSettingStyle() == 1) {
            vh.tb.setVisibility(View.GONE);
        } else {
            vh.tb.setVisibility(View.VISIBLE);
        }

        return view;
    }



    private static class ViewHolder {
        TextView tv1;
        TextView tv2;
        ToggleButton tb;
    }
}
