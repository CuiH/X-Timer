package com.crossbow.app.x_timer.day_detail;

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
        return transferLongToDate(duration);
    }

    private String transferLongToDate(Long millSec){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date= new Date(millSec);

        return sdf.format(date);
    }
}
