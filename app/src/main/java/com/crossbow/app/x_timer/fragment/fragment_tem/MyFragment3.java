package com.crossbow.app.x_timer.fragment.fragment_tem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.detail.app_detail.AppDayItem;
import com.crossbow.app.x_timer.detail.app_detail.AppDetailActivity;
import com.crossbow.app.x_timer.utils.FileUtils;

/**
 * Created by CuiH on 2015/12/29.
 */
public class MyFragment3 extends Fragment {

    private MainActivity mainActivity;

    public MyFragment3() { }

    @SuppressLint("ValidFragment")
    public MyFragment3(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_page3, container, false);

        Button deleteall = (Button)view.findViewById(R.id.deleteall);
        deleteall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtils fileUtils = new FileUtils(getContext());
                fileUtils.deleteALLFiles();
            }
        });

        Button showall = (Button)view.findViewById(R.id.showall);
        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtils fileUtils = new FileUtils(getContext());
                fileUtils.showALLFiles();
            }
        });

        final Button wx = (Button)view.findViewById(R.id.weixin);

        wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, AppDetailActivity.class);
                intent.putExtra("pkgName", "com.tencent.mm");
                intent.putExtra("realName", "微信");
                startActivity(intent);
            }
        });
        return view;
    }


}
