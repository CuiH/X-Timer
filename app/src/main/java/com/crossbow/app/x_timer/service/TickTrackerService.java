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
import com.crossbow.app.x_timer.Utils.FileUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TickTrackerService extends Service {
    private final String TAG = "service";

    // system service
    public UsageStatsManager usageStatsManager;

    // file
    private FileUtils fileUtils;

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
            if (isInWatchingList(appName)) return false;

            watchingList.put(appName, new AppUsage(appName));
            updateNotification();
            return true;
        }

        // remove a app from watching list
        public boolean removeAppFromWatchingLise(String appName) {
            if (!isInWatchingList(appName)) return false;

            watchingList.remove(appName);
            updateNotification();
            return true;
        }

        // check whether a app is in the watching list or not
        public boolean isInWatchingList(String appName) {
            if (!watchingList.containsKey(appName)) return false;
            else return true;
        }

        // manually save data
        public void manuallySaveData() {
            storeAppInformation();
            storeWatchingList();
        }
    }


    // thread that keeps watching apps
    private class WatchingForegroundAppThread extends Thread {
        @Override
        public void run() {
            while (running) {
                long ts = System.currentTimeMillis();
                List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats
                        (UsageStatsManager.INTERVAL_BEST, ts - 2000, ts);

                if (queryUsageStats != null && !queryUsageStats.isEmpty()) {
                    UsageStats currentApp = null;
                    // find current app
                    for (UsageStats usageStats : queryUsageStats) {
                        if (currentApp == null || currentApp.getLastTimeUsed() <
                                usageStats.getLastTimeUsed()) {
                            currentApp = usageStats;
                        }
                    }

                    // check if app switched
                    if (currentApp != null && (lastApp == null ||
                            !lastApp.getPackageName().equals(currentApp.getPackageName()))) {
                        if (lastApp != null)
                        // check if last app is in the watching list
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

        initVariables();
        initWatchingList();
        initWatchingThread();

        startNotification();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: " + this.toString());

        stopWatchingThread();

        storeAppInformation();
        storeWatchingList();

        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: Unbind " + this.toString());
        return super.onUnbind(intent);
    }

    private void initVariables() {
        running = true;
        lastApp = null;

        watchingList = new HashMap<>();
        usageBinder = new UsageBinder();
        fileUtils = new FileUtils(this);

        //API Level 21 需要使用硬编码;
        usageStatsManager = (UsageStatsManager) getSystemService("usagestats");
    }

    // init the watching list
    private void initWatchingList() {
        for (String appName : fileUtils.getAppList()) {
            watchingList.put(appName, fileUtils.loadAppInfo(appName));
        }
    }

    // init the thread
    private void initWatchingThread() {
        watchingForegroundAppThread = new WatchingForegroundAppThread();
        watchingForegroundAppThread.start();
    }

    // stop the thread
    private void stopWatchingThread() {
        watchingForegroundAppThread.interrupt();
        running = false;
    }

    // update the old app when app switched
    private void onAppSwitched() {
        AppUsage targetApp = watchingList.get(lastApp.getPackageName());

        String today = AppUsage.getDateInString(new Date());

        targetApp.updateUsingHistory(today,
                System.currentTimeMillis()-lastApp.getLastTimeUsed(),
                System.currentTimeMillis());

        Log.d(TAG, "上个 app: " + targetApp.getPackageName());
        Log.d(TAG, "上次用时: "+ targetApp.getUsingHistory().get(today).getUsingRecord().get(targetApp.getUsingHistory().get(today).getUsingRecord().size() - 1).getDuration());
        Log.d(TAG, "总次数: " + targetApp.getUsingHistory().get(today).getUsedCount());
        Log.d(TAG, "总用时: " + targetApp.getUsingHistory().get(today).getTotalTime());
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
                .setContentText("Watching " + watchingList.size() + " apps")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .build();

        startForeground(1, notification);
    }

    // store app information when exit
    private void storeAppInformation() {
        for (Map.Entry<String, AppUsage> entry: watchingList.entrySet()) {
            fileUtils.storeAppInfo(entry.getValue());
        }
    }

    // store the watching list
    private void storeWatchingList() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (Map.Entry<String, AppUsage> app : watchingList.entrySet()) {
            arrayList.add(app.getKey());
        }

        fileUtils.storeAppList(arrayList);
    }
}