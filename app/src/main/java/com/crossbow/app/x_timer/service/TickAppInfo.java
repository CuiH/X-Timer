package com.crossbow.app.x_timer.service;

/**
 * Created by CuiH on 2016/1/7.
 */
public class TickAppInfo {
    private String pkgName;
    private long startTime;

    private boolean inWatchingList;

    private boolean hasInstance;

    public TickAppInfo() {
        hasInstance = false;
    }

    public void setAll(String pName, long sTime, boolean flag) {
        pkgName = pName;
        startTime = sTime;
        inWatchingList = flag;

        if (!hasInstance) hasInstance = true;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getPkgName() {
        return pkgName;
    }

    public boolean isInWatchingList() {
        return inWatchingList;
    }

    public boolean isHasInstance() {
        return hasInstance;
    }

}
