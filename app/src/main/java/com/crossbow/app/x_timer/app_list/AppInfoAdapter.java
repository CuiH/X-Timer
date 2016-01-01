package com.crossbow.app.x_timer.app_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.crossbow.app.x_timer.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglx on 2016/1/1.
 */
public class AppInfoAdapter extends ArrayAdapter<AppInfo> {
    private int resourceId;
    // 已选的
    private ArrayList<String> checked;

    public AppInfoAdapter(Context context, int textViewResourceId, List<AppInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        checked = new ArrayList<>();
    }

    public void setDefault(ArrayList<String> alreadyChecked) {
        checked = (ArrayList<String>)alreadyChecked.clone();
    }

    public ArrayList<String> getCheckedList() {
        return checked;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AppInfo appInfo = getItem(position);
        View view;
        ViewHolder vh = new ViewHolder();
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            vh.iv = (ImageView)view.findViewById(R.id.app_icon);
            vh.tv = (TextView)view.findViewById(R.id.app_name);
            vh.cb = (CheckBox)view.findViewById(R.id.app_check_box);
            view.setTag(vh);
        } else {
            view = convertView;
            vh = (ViewHolder)view.getTag();
        }

        vh.iv.setImageDrawable(appInfo.getIcon());
        vh.tv.setText(appInfo.getAppName());

        // 已选的默认勾选
        if (checked.contains(getItem(position).getPackageName())) vh.cb.setChecked(true);
        else vh.cb.setChecked(false);

        return view;
    }

    class ViewHolder {
        ImageView iv;
        TextView tv;
        CheckBox cb;
    }
}
