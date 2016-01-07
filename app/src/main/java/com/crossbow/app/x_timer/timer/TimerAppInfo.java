package com.crossbow.app.x_timer.timer;

import android.graphics.drawable.Drawable;

/**
 * Created by CuiH on 2016/1/8.
 */
public class TimerAppInfo {
    private String pkgName;
    private String realName;
    private Drawable icon;
    private long totalTime;

    private boolean hasTimer;
    private long limit;

    public TimerAppInfo(String pName, String rName, Drawable i, long tTime, boolean flag, long l) {
        pkgName = pName;
        realName = rName;
        icon = i;
        totalTime = tTime;
        hasTimer = flag;
        limit = l;
    }

    public void setHasTimer(boolean flag) {
        hasTimer = flag;
    }

    public void setLimit(long time) {
        limit = time;
    }

    public String getPkgName() {
        return pkgName;
    }

    public String getRealName() {
        return realName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public boolean isHasTimer() {
        return hasTimer;
    }

    public long getLimit() {
        return limit;
    }

    public String getTotalTimeInString() {
        return transferLongToTime(totalTime);
    }

    public String getLimitInString() {
        return transferLongToTime(limit);
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
