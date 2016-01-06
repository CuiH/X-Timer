package com.crossbow.app.x_timer.detail.app_detail;

import com.crossbow.app.x_timer.detail.UsageItem;

import java.util.List;

/**
 * Created by CuiH on 2016/1/6.
 */
public class AppDayItem {
    private String date;
    private long totalTime;
    private int totalCount;
    private List<UsageItem> appUsages;

    public AppDayItem(String d, long tTime, int tCount, List<UsageItem> usages) {
        date = d;
        totalTime = tTime;
        totalCount = tCount;
        appUsages = usages;
    }

    public String getDate() {
        return date;
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
