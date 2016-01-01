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

//    public void setTotalTimeUsed(long totalTimeUsed) {
//        this.totalTimeUsed = totalTimeUsed;
//    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void addUsingRecord(String date, long time) {
        if (usingHistory.isEmpty()) {
            usingHistory.add(new History(date, time));
            return;
        }
        History history = usingHistory.get(usingHistory.size() - 1);
        if (history.getDate().equals(date)) {
            history.setTotalTime(history.getTotalTime() + time);
        } else {
            usingHistory.add(new History(date, time));
        }
        totalTimeUsed += time;
    }

    public ArrayList<History> getUsingRecord() {
        return usingHistory;
    }


    public static class History {
        private String date;

        public long getTotalTime() {
            return totalTime;
        }

        public void setTotalTime(long totalTime) {
            this.totalTime = totalTime;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        private long totalTime;

        public History(String date) {
            this.date = date;
            this.totalTime = 0;
        }
        public History(String date, long totalTime) {
            this.date = date;
            this.totalTime = totalTime;
        }
    }
}