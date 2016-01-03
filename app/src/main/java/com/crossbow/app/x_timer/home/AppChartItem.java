package com.crossbow.app.x_timer.home;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by wanglx on 2016/1/2.
 */
public class AppChartItem {
    private String appName;
    private long totalTime;
    private int totalCount;

    public AppChartItem(String name, long tTime, int tCount) {
        appName = name;
        totalTime = tTime;
        totalCount = tCount;
    }

    public String getAppName() {
        return appName;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public float getTotalTimeInSecond() {
        return (float)totalTime/1000;
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
