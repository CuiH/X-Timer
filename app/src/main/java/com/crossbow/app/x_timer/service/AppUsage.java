package com.crossbow.app.x_timer.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CuiH on 2015/12/29.
 */
public class AppUsage {
    // 应用包名
    private String packageName;
    // 记录应用使用历史（按天）
    private Map<String, History> usingHistory;

    public AppUsage(String pkgName) {
        packageName = pkgName;
        usingHistory = new HashMap<>();
    }

    public String getPackageName() {
        return packageName;
    }

    public Map<String, History> getUsingHistory() {
        return usingHistory;
    }

    // 更新使用记录，同时如果是新的一天（即新增了History）返回true，否则返回false
    public boolean updateUsingHistory(String date, long duration, long endTime) {
        // 是新的一天
        if (usingHistory.isEmpty() || !usingHistory.containsKey(date)) {
            addUsingHistory(date, duration, endTime);

            return true;
        }

        History targetHistory = usingHistory.get(date);
        targetHistory.addTotalTime(duration, endTime);

        return false;
    }

    //　增加使用记录
    public void addUsingHistory(String date, long duration, long endTime) {
        History newHistory = new History(duration, endTime);

        usingHistory.put(date, newHistory);
    }


    public class History {
        // 最多记录当天使用记录数
        private final int MAX_RECORD = 5;

        // 使用总时长
        private long totalTime;
        // 使用次数
        private int usedCount;
        // 使用记录（MAX_RECORD次）
        private ArrayList<Record> usingRecord;

        public History(long duration, long eTime) {
            usedCount = 0;
            totalTime = duration;
            usingRecord = new ArrayList<>();

            addUsingRecord(duration, eTime);
        }

        public long getTotalTime() {
            return totalTime;
        }

        // 更新使用时长，同时意味着多使用了一次，多增加了一条使用记录
        public void addTotalTime(long duration, long endTime) {
            totalTime = totalTime + duration;
            addUsingRecord(duration, endTime);
        }

        public int getUsedCount() {
            return usedCount;
        }

        public void addUsedCount() {
            usedCount = usedCount+1;
        }

        public ArrayList<Record> getUsingRecord() {
            return usingRecord;
        }

        // 增加一条使用记录，同时如果在这之前已满（即删除了一条）返回true，否则返回false
        public boolean addUsingRecord(long duration, long endTime) {
            addUsedCount();

            Record record = new Record(duration, endTime);

            boolean updated = false;
            if (!usingRecord.isEmpty() && usingRecord.size() >= MAX_RECORD) {
                usingRecord.remove(0);
                updated = true;
            }
            usingRecord.add(record);

            return updated;
        }


        // 详细使用记录
        public class Record {
            // 开始使用时间
            private long duration;
            // 使用结束时间
            private long endTime;

            Record(long dura, long eTime) {
                duration = dura;
                endTime = eTime;
            }

            public long getStartTime() {
                return endTime-duration;
            }

            public long getEndTime() {
                return endTime;
            }

            public long getDuration() {
                return duration;
            }
        }
    }

    // return the date in string (xxxx-xx-xx)
    public static String getDateInString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String stringDate = formatter.format(date);

        return stringDate;
    }
}
