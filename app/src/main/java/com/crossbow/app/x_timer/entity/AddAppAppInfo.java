package com.crossbow.app.x_timer.entity;

import android.graphics.drawable.Drawable;

/**
 * Created by wanglx on 2016/1/1.
 */
public class AddAppAppInfo {
    // 包名
    private String packageName;
    // 应用名
    private String appName;
    // 图标
    private Drawable icon;

    private boolean selected;

    public AddAppAppInfo(String pName, String aName, Drawable i, boolean s) {
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
