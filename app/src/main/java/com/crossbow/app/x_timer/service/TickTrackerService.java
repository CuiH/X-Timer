package com.crossbow.app.x_timer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CuiH on 2015/12/29.
 */
public class TickTrackerService extends Service {
    private final String TAG = "service";

    // system service
    public UsageStatsManager usageStatsManager;

    // thread
    private boolean running;
    private WatchingForegroundAppThread watchingForegroundAppThread;

    // app info
    private UsageStats lastApp;
    private Map<String, AppUsage> watchingList;

    // binder
    private UsageBinder usageBinder;

    public class UsageBinder extends Binder {
        // get the watching list
        public Map<String, AppUsage> getWatchingList() {
            return watchingList;
        }

        // add a app to watching list
        public boolean addAppToWatchingList(String appName) {
            if (watchingList.containsKey(appName)) return false;
            watchingList.put(appName, new AppUsage());
            watchingList.get(appName).setPackageName(appName);

            updateNotification();
            return true;
        }

        // remove a app from watching list
        public boolean removeAppFromWatchingLise(String appName) {
            if(!watchingList.containsKey(appName)) return false;
            watchingList.remove(appName);

            updateNotification();
            return true;
        }
    }

    private class WatchingForegroundAppThread extends Thread {
        @Override
        public void run() {
            while (running) {
                long ts = System.currentTimeMillis();
                List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats
                        (UsageStatsManager.INTERVAL_BEST, ts - 2000, ts);

                if (queryUsageStats != null && !queryUsageStats.isEmpty()) {
                    UsageStats currentApp = null;

                    // find current app;
                    for (UsageStats usageStats : queryUsageStats) {
                        if(currentApp == null || currentApp.getLastTimeUsed() <
                                usageStats.getLastTimeUsed()) {
                            currentApp = usageStats;
                        }
                    }

                    // check if app switched
                    if (currentApp != null && (lastApp == null ||
                            !lastApp.getPackageName().equals(currentApp.getPackageName()))) {
                        // check if the new app is in the watching list
                        if (lastApp != null && watchingList.containsKey(lastApp.getPackageName())) {
                            onAppSwitched();
                        }
                        lastApp = currentApp;
                    }
                }

                // observe every second
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: bind " + this.toString());
        return usageBinder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: Created " + this.toString());
        super.onCreate();

        // init
        running = true;
        lastApp = null;
        watchingList = new HashMap<>();
        usageBinder = new UsageBinder();
        usageStatsManager = (UsageStatsManager)getSystemService(Context.USAGE_STATS_SERVICE);

        initWatchingList();
        initWatchingThread();
        startNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + this.toString());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: " + this.toString());

        // stop thread
        watchingForegroundAppThread.interrupt();
        running = false;

        storeInformation();

        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: Unbind " + this.toString());
        return super.onUnbind(intent);
    }

    // init the watching list
    private void initWatchingList() {
        //for test
        watchingList.put("com.tencent.mm", new AppUsage());
        watchingList.get("com.tencent.mm").setPackageName("com.tencent.mm");
        watchingList.put("com.tencent.mobileqq", new AppUsage());
        watchingList.get("com.tencent.mobileqq").setPackageName("com.tencent.mobileqq");
    }

    // init the thread
    private void initWatchingThread() {
        watchingForegroundAppThread  = new WatchingForegroundAppThread();
        watchingForegroundAppThread.start();
    }

    // store app information when exit
    private void storeInformation() {

    }

    // operations when app switched
    private void onAppSwitched() {
        // update last app's usage time
        AppUsage targetApp = watchingList.get(lastApp.getPackageName());
        targetApp.setLastTimeUsed(lastApp.getLastTimeUsed());
        targetApp.setLastTimeQuit(System.currentTimeMillis());
        long total = targetApp.getTotalTimeUsed() +
                targetApp.getLastTimeQuit() - targetApp.getLastTimeUsed();
        targetApp.setTotalTimeUsed(total);

        // log
        Log.d(TAG, targetApp.getPackageName()+" onAppSwitched: 上次启动时间" + lastApp.getLastTimeUsed());
        Log.d(TAG, targetApp.getPackageName()+" onAppSwitched: 上次用时" + (System.currentTimeMillis() -
                lastApp.getLastTimeUsed()));
        Log.d(TAG, targetApp.getPackageName()+" onAppSwitched: 总用时" + total);
        Log.d(TAG, watchingForegroundAppThread.toString() + this.toString());
    }

    // update the notification (stop and reshow)
    private void updateNotification() {
        stopForeground(true);

        startNotification();
    }

    // start the foreground service - notification
    private void startNotification() {
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("X-Timer is working")
                .setContentText("Watching "+watchingList.size()+" apps")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .build();

        startForeground(1, notification);
    }
}