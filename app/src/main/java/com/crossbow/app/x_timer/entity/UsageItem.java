package com.crossbow.app.x_timer.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wanglx on 2016/1/2.
 */
public class UsageItem {
    private long beginTime;
    private long endTime;
    private long duration;

    public UsageItem(long bTime, long eTime, long dura) {
        beginTime = bTime;
        endTime = eTime;
        duration = dura;
    }

    public String getBeginInString() {
        return transferLongToDate(beginTime);
    }

    public String getEndInString() {
        return transferLongToDate(endTime);
    }

    public String getDurationInString() {
        return transferLongToTime(duration);
    }

    private String transferLongToDate(Long millSec){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date= new Date(millSec);

        return sdf.format(date);
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
