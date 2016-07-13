package com.crossbow.app.x_timer.entity;

import android.graphics.drawable.Drawable;

import com.crossbow.app.x_timer.entity.UsageItem;

import java.util.List;

/**
 * Created by wanglx on 2016/1/2.
 */
public class DayAppItem {
    private String appName;
    private Drawable appIcon;
    private long totalTime;
    private int totalCount;
    private List<UsageItem> appUsages;

    public DayAppItem(String name, Drawable icon, long tTime, int tCount, List<UsageItem> usages) {
        appName = name;
        appIcon = icon;
        totalTime = tTime;
        totalCount = tCount;
        appUsages = usages;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public int getTotalCount() {
        return totalCount;
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
