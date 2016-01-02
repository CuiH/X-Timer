package com.crossbow.app.x_timer.day_detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crossbow.app.x_timer.R;

import java.util.List;

/**
 * Created by wanglx on 2016/1/2.
 */
public class DayDetailAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;

    private List<AppItem> items;

    public DayDetailAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<AppItem> items) {
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
            holder.hint = (TextView) convertView.findViewById(R.id.textHint);
            convertView.setTag(holder);
        } else {
            holder = (UsageHolder) convertView.getTag();
        }

        holder.title.setText(item.getDurationInString());
        holder.hint.setText(item.getBeginInString()+" to "+item.getEndInString());

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).getAppUsages().size();
    }

    @Override
    public AppItem getGroup(int groupPosition) {
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
        AppItem item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new AppHolder();
            convertView = inflater.inflate(R.layout.day_detail_group, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.textTitle);
            convertView.setTag(holder);
        } else {
            holder = (AppHolder) convertView.getTag();
        }

        holder.title.setText(item.getAppName());

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
        TextView hint;
    }

    private static class AppHolder {
        TextView title;
    }

}
