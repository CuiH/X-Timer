package com.crossbow.app.x_timer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.util.listview.AnimatedExpandableListView;
import com.crossbow.app.x_timer.entity.AppDayItem;
import com.crossbow.app.x_timer.entity.UsageItem;

import java.util.List;

/**
 * Created by CuiH on 2016/1/6.
 */
public class AppDetailAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;

    private List<AppDayItem> items;


    public AppDetailAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<AppDayItem> items) {
        this.items = items;
    }

    @Override
    public UsageItem getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).getAppUsages().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
        UsageHolder holder;
        UsageItem item = getChild(groupPosition, childPosition);
        if (convertView == null) {
            holder = new UsageHolder();
            convertView = inflater.inflate(R.layout.day_detail_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.textTitle);
            holder.hint1 = (TextView) convertView.findViewById(R.id.textHint1);
            holder.hint2 = (TextView) convertView.findViewById(R.id.textHint2);
            convertView.setTag(holder);
        } else {
            holder = (UsageHolder) convertView.getTag();
        }

        holder.title.setText("单次时长：" + item.getDurationInString());
        holder.hint1.setText("开始时间："+item.getBeginInString());
        holder.hint2.setText("结束时间："+item.getEndInString());

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).getAppUsages().size();
    }

    @Override
    public AppDayItem getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        AppHolder holder;
        AppDayItem item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new AppHolder();
            convertView = inflater.inflate(R.layout.app_detail_group, parent, false);
            holder.date = (TextView) convertView.findViewById(R.id.textDate);
            holder.count = (TextView) convertView.findViewById(R.id.textCount);
            holder.time = (TextView) convertView.findViewById(R.id.textTime);
            convertView.setTag(holder);
        } else {
            holder = (AppHolder)convertView.getTag();
        }

        holder.date.setText(item.getDate());
        holder.count.setText("总次数:" + item.getTotalCount());
        holder.time.setText("总时长："+item.getTotalTimeInString());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }


    private static class UsageHolder {
        TextView title;
        TextView hint1;
        TextView hint2;
    }

    private static class AppHolder {
        TextView date;
        TextView count;
        TextView time;
    }
}
