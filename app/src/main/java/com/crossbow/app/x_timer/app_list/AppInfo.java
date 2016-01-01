package com.crossbow.app.x_timer.app_list;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by wanglx on 2016/1/1.
 */
public class AppInfo {
    // 包名
    private String packageName;
    // 应用名
    private String appName;
    // 图标
    private Drawable icon;

    public AppInfo(String pName, String aName, Drawable i) {
        packageName = pName;
        appName = aName;
        icon = i;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getIcon() {
        return icon;
    }
}
