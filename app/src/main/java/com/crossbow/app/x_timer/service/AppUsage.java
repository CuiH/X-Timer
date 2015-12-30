package com.crossbow.app.x_timer.service;

import java.util.ArrayList;

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
     * 记录应用的打开和关闭历史
     */
    private ArrayList<History> usingHistory;

    public AppUsage() {
        usingHistory = new ArrayList<>();
        totalTimeUsed = 0;
    }

    public AppUsage(String pkgName) {
        this();
        packageName = pkgName;
    }

    public long getTotalTimeUsed() {
        return totalTimeUsed;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setTotalTimeUsed(long totalTimeUsed) {
        this.totalTimeUsed = totalTimeUsed;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void addUsingRecord(long start, long end) {
        usingHistory.add(new History(start, end));
    }

    public ArrayList<History> getUsingRecord() {
        return usingHistory;
    }


    public static class History {
        private long startTime;
        private long endTime;

        public History(long startTime, long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }
    }
}