package com.crossbow.app.x_timer.service;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crossbow.app.x_timer.Utils.FileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CuiH on 2015/12/29.
 */
public class TickTrackerService extends Service {
    public UsageStatsManager usageStatsManager;
    private boolean running = true;
    private UsageStats oldAppStatus;
    private Map<String, AppUsage> watchingList;
    private UsageBinder usageBinder = new UsageBinder();
    private String TAG = "xyz";

    public class UsageBinder extends Binder {
        public Map<String, AppUsage> getWatchingList() {
            return watchingList;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return usageBinder;
    }

    @Override
    public void onCreate() {
        oldAppStatus = null;
        Log.d(TAG, "onCreate: Service Created");
        watchingList = new HashMap<>();
        watchingList.put("com.tencent.mm", new AppUsage());
        watchingList.get("com.tencent.mm").setPackageName("com.tencent.mm");
        usageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);
        new WatchingForegroundAppThread().start();
    }

    private void onAppSwitched() {
        AppUsage appUsage = watchingList.get(oldAppStatus.getPackageName());
//        appUsage.setLastTimeUsed(oldAppStatus.getLastTimeUsed());
//        appUsage.setLastTimeQuit(System.currentTimeMillis());
        long total = appUsage.getTotalTimeUsed() + System
                .currentTimeMillis() - oldAppStatus.getLastTimeUsed();
        appUsage.setTotalTimeUsed(total);
        Log.d(TAG, "onAppSwitched: 上次启动时间" + oldAppStatus.getLastTimeUsed());
        Log.d(TAG, "onAppSwitched: 上次用时" + (System.currentTimeMillis() -
                oldAppStatus.getLastTimeUsed()));
        Log.d(TAG, "onAppSwitched: 总用时" + total);
    }

    private class WatchingForegroundAppThread extends Thread {
        @Override
        public void run() {
            while (running) {
                long ts = System.currentTimeMillis();
                List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats
                        (UsageStatsManager.INTERVAL_BEST, ts - 2000, ts);
                if (queryUsageStats != null && !queryUsageStats.isEmpty()) {
                    UsageStats recentStats = null;

                    for (UsageStats usageStats : queryUsageStats) {
                        if(recentStats == null || recentStats.getLastTimeUsed() <
                                usageStats.getLastTimeUsed()) {
                            recentStats = usageStats;
                        }
                    }
                    if (recentStats != null && (oldAppStatus == null ||
                            !oldAppStatus.getPackageName().equals(recentStats
                                    .getPackageName()))) {
                        if (oldAppStatus != null && oldAppStatus
                                .getPackageName().equals("com.tencent.mm")) {
                            onAppSwitched();
                        }
                        oldAppStatus = recentStats;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Service");
        super.onDestroy();
        //TODO 在销毁之前把数据写入数据库
        FileUtils fileUtils = new FileUtils(this);
        for (Map.Entry<String, AppUsage> entry : watchingList.entrySet()) {
            fileUtils.store(entry.getValue());
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: Unbind Service");
        return super.onUnbind(intent);
    }

    private void loadFromFile() {

    }
}