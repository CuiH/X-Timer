package com.crossbow.app.x_timer.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.entity.AddAppAppInfo;

import java.util.List;

/**
 * Created by wanglx on 2016/1/1.
 */
public class AppInfoAdapter extends ArrayAdapter<AddAppAppInfo> {
    private int resourceId;
    private Context mContext;

    public AppInfoAdapter(Context context, int textViewResourceId, List<AddAppAppInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AddAppAppInfo addAppAppInfo = getItem(position);
        View view;
        ViewHolder vh = new ViewHolder();
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            vh.iv = (ImageView)view.findViewById(R.id.app_icon);
            vh.tv = (TextView)view.findViewById(R.id.app_name);
            vh.cb = (CheckBox)view.findViewById(R.id.app_check_box);
            vh.rl = (RelativeLayout)view.findViewById(R.id.app_background);
            view.setTag(vh);
        } else {
            view = convertView;
            vh = (ViewHolder)view.getTag();
        }

        vh.iv.setImageDrawable(addAppAppInfo.getIcon());
        vh.tv.setText(addAppAppInfo.getAppName());

        // 已选的默认勾选
        if (addAppAppInfo.getSelected()) {
            vh.cb.setChecked(true);
            vh.rl.setBackgroundColor(ContextCompat.getColor(mContext, R.color.listItemSelected));
        } else {
            vh.cb.setChecked(false);
            vh.rl.setBackgroundColor(ContextCompat.getColor(mContext, R.color.listItemUnselected));
        }

        return view;
    }

    private static class ViewHolder {
        ImageView iv;
        TextView tv;
        CheckBox cb;
        RelativeLayout rl;
    }
}
