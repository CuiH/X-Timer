package com.crossbow.app.x_timer.day_detail;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by wanglx on 2016/1/2.
 */
public class AppItem {
    private String appName;
    private Drawable appIcon;
    private long totalTime;
    private List<UsageItem> appUsages;

    public AppItem(String name, Drawable icon, long tTime, List<UsageItem> usages) {
        appName = name;
        appIcon = icon;
        totalTime = tTime;
        appUsages = usages;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public List<UsageItem> getAppUsages() {
        return appUsages;
    }

    public String getTotalTimeInString() {
        return transferLongToTime(totalTime);
    }

    // 转换为时分秒
    private String transferLongToTime(Long millSec) {
        if (millSec < 60000) {
            return ""+millSec/1000+"秒";
        } else if (millSec < 3600000){
            long min = millSec/60000;
            long sec = millSec%60000;
            return ""+min+"分"+sec/1000+"秒";
        } else {
            long hour = millSec/3600000;
            millSec %= 3600000;
            long min = millSec/60000;
            long sec = millSec%60000;
            return ""+hour+"小时"+min+"分"+sec/1000+"秒";
        }
    }
}
