package com.crossbow.app.x_timer.fragment.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.crossbow.app.x_timer.R;

import java.util.List;

/**
 * Created by CuiH on 2016/1/6.
 */
public class HistoryAppListAdapter extends ArrayAdapter<HistoryAppInfo> {

    private int resourceId;

    public HistoryAppListAdapter(Context context, int textViewResourceId, List<HistoryAppInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryAppInfo nowApp = getItem(position);

        View view;
        ViewHolder vh = new ViewHolder();
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            vh.tv = (TextView)view.findViewById(R.id.history_app_name);
            vh.rb = (RadioButton)view.findViewById(R.id.history_app_radio);
            vh.iv = (ImageView)view.findViewById(R.id.history_app_icon);
            view.setTag(vh);
        } else {
            view = convertView;
            vh = (ViewHolder)view.getTag();
        }

        vh.tv.setText(nowApp.getRealName());
        vh.iv.setImageDrawable(nowApp.getIcon());
        if (nowApp.getSelected()) vh.rb.setChecked(true);
        else vh.rb.setChecked(false);

        return view;
    }

    private static class ViewHolder {
        TextView tv;
        RadioButton rb;
        ImageView iv;
    }

}
