package com.crossbow.app.x_timer.service;

/**
 * Created by CuiH on 2015/12/29.
 */
public class AppUsage {
    /**
     * 应用在前台的总时间
     */
    private long totalTimeUsed;
    /**
     * 应用包名
     */
    private String packageName;
    /**
     * 最近一次到前台的时间
     */
    private long lastTimeUsed;
    /**
     * 最近一次退出前台的时间
     */
    private long lastTimeQuit;

    public long getTotalTimeUsed() {
        return totalTimeUsed;
    }

    public String getPackageName() {
        return packageName;
    }

    public long getLastTimeUsed() {
        return lastTimeUsed;
    }

    public long getLastTimeQuit() {
        return lastTimeQuit;
    }

    public void setLastTimeQuit(long lastTimeQuit) {
        this.lastTimeQuit = lastTimeQuit;
    }

    public void setTotalTimeUsed(long totalTimeUsed) {
        this.totalTimeUsed = totalTimeUsed;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setLastTimeUsed(long lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }
}