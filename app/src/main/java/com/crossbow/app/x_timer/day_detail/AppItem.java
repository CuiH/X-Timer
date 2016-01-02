package com.crossbow.app.x_timer.day_detail;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by wanglx on 2016/1/2.
 */
public class AppItem {
    private String appName;
    private Drawable appIcon;
    private int usageCount;
    private List<UsageItem> appUsages;

    public AppItem(String name, Drawable icon, int count, List<UsageItem> usages) {
        appName = name;
        appIcon = icon;
        usageCount = count;
        appUsages = usages;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public List<UsageItem> getAppUsages() {
        return appUsages;
    }
}
