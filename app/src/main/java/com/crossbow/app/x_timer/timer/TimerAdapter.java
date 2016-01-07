package com.crossbow.app.x_timer.timer;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.add_app.AppInfo;

import java.util.List;

/**
 * Created by CuiH on 2016/1/8.
 */
public class TimerAdapter extends ArrayAdapter<TimerAppInfo> {
    private int resourceId;
    private Context mContext;

    public TimerAdapter(Context context, int textViewResourceId, List<TimerAppInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TimerAppInfo appInfo = getItem(position);
        View view;
        ViewHolder vh = new ViewHolder();
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            vh.iv = (ImageView)view.findViewById(R.id.timer_app_icon);
            vh.tv1 = (TextView)view.findViewById(R.id.timer_app_name);
            vh.tv2 = (TextView)view.findViewById(R.id.timer_app_time);
            vh.tv3 = (TextView)view.findViewById(R.id.timer_app_timer);
            vh.ll1 = (LinearLayout)view.findViewById(R.id.timer_has_timer);
            vh.ll2 = (LinearLayout)view.findViewById(R.id.timer_no_timer);
            view.setTag(vh);
        } else {
            view = convertView;
            vh = (ViewHolder)view.getTag();
        }

        vh.iv.setImageDrawable(appInfo.getIcon());
        vh.tv1.setText(appInfo.getRealName());

        if (appInfo.isHasTimer()) {
            vh.ll1.setVisibility(View.VISIBLE);
            vh.ll2.setVisibility(View.GONE);
            vh.tv2.setText("已使用时长："+appInfo.getTotalTimeInString());
            vh.tv3.setText("已设置定时："+appInfo.getLimitInString());
        } else {
            vh.ll1.setVisibility(View.GONE);
            vh.ll2.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private static class ViewHolder {
        ImageView iv;
        TextView tv1;
        TextView tv2;
        TextView tv3;
        LinearLayout ll1;
        LinearLayout ll2;
    }

}
