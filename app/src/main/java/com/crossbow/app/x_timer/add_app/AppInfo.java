package com.crossbow.app.x_timer.add_app;

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

    private boolean selected;

    public AppInfo(String pName, String aName, Drawable i, boolean s) {
        packageName = pName;
        appName = aName;
        icon = i;

        selected = s;
    }

    public void setSelected(boolean s) {
        selected = s;
    }

    public boolean getSelected() {
        return selected;
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
