package com.crossbow.app.x_timer.service;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.splash.SplashActivity;
import com.crossbow.app.x_timer.utils.FileUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TickTrackerService extends Service {

    private static final String TAG = "service";
    private static final int TIME_LIMIT = 1;

    // system service
    public UsageStatsManager usageStatsManager;

    // file
    private FileUtils fileUtils;

    // thread
    private WatchingForegroundAppThread watchingForegroundAppThread;

    // app info
    private TickAppInfo lastApp, currentApp;
    private Map<String, AppUsage> watchingList;

    // binder
    private UsageBinder usageBinder;

    // watching screen status;
    private ScreenStatusReceiver mScreenStatusReceiver;
    private boolean hasExperiencedScreenChanged;
    private KeyguardManager mKeyguardManager;


    public class UsageBinder extends Binder {
        // get the watching list
        public Map<String, AppUsage> getWatchingList() {
            return watchingList;
        }

        // add a app to watching list
        public boolean addAppToWatchingList(String appName, boolean shouldShow) {
            if (isInWatchingList(appName)) return false;

            watchingList.put(appName, fileUtils.loadAppInfo(appName));

            if (shouldShow) updateNotification();

            return true;
        }

        // remove a app from watching list
        public boolean removeAppFromWatchingLise(String appName, boolean shouldShow) {
            if (!isInWatchingList(appName)) return false;

            watchingList.remove(appName);

            if (shouldShow) updateNotification();

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

        // update notification
        public void changeNotificationState(boolean flag) {
            if (flag == false) stopForeground(true);
            else startNotification();
        }

        // set limit to a app
        public boolean setLimitToApp(String pkgName, long limit) {
            AppUsage app = watchingList.get(pkgName);
            if (app == null) return false;

            if (limit == 0) {
                app.setLimitLength(Integer.MAX_VALUE);
                app.setPrompted(false);
                app.setHasLimit(false);

                return true;
            } else {
                app.setLimitLength(limit);
                app.setPrompted(false);
                app.setHasLimit(true);

                return true;
            }
        }

    }

    // thread that keeps watching apps
    private class WatchingForegroundAppThread extends Thread {

        private boolean running = true;

        private void onThreadWait() {
            try {
                synchronized (this) {
                    Log.d(TAG, "onThreadWait: 线程等待");
                    this.wait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void checkLimit() {
            AppUsage appUsage = watchingList.get(currentApp.getPkgName());
            if (appUsage.isHasLimit() && !appUsage.getPrompted()) {
                long todayTotalTime;

                Map<String, AppUsage.History> historyMap = appUsage.getUsingHistory();
                if (historyMap.size() <= 0) {
                    todayTotalTime = 0;
                } else {
                    AppUsage.History history = historyMap.get(AppUsage
                            .getDateInString(new Date()));
                    if (history == null) {
                        todayTotalTime = 0;
                    } else {
                        todayTotalTime = history.getTotalTime();
                    }
                }

                long lastTimeStamp = currentApp.getStartTime();

                Log.d(TAG, "checkLimit: " + appUsage.getLimitLength());
                if (hasExperiencedScreenChanged) {
                    lastTimeStamp = mScreenStatusReceiver.getScreenOnTime();
                }

                if (todayTotalTime + System.currentTimeMillis()
                        - lastTimeStamp >= appUsage.getLimitLength()) {
                    Message message = new Message();
                    message.what = TIME_LIMIT;
                    timeLimitHandler.sendMessage(message);
                    appUsage.setPrompted(true);
                }
            }
        }

        public synchronized void onThreadPause() {
            Log.d(TAG, "onThreadPause: 线程暂停");
            running = false;
        }

        public synchronized void onThreadResume() {
            Log.d(TAG, "onThreadResume: 线程恢复");
            running = true;
            this.notify();
        }

        @Override
        public void run() {
            while (true) {
                if (running) {
                    long nowTime = System.currentTimeMillis();
                    List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats
                            (UsageStatsManager.INTERVAL_BEST, nowTime - 2000, nowTime);

                    if (currentApp.isHasInstance() && currentApp.isInWatchingList()) {
                        checkLimit();
                    }

                    if (queryUsageStats != null && !queryUsageStats.isEmpty()) {
                        // find the last used app in the second
                        UsageStats theLastAppInOneSecond = null;
                        for (UsageStats usageStats : queryUsageStats) {
                            if (theLastAppInOneSecond == null || theLastAppInOneSecond
                                    .getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                                theLastAppInOneSecond = usageStats;
                            }
                        }

                        if (theLastAppInOneSecond != null) {
                            // if no last, last = now
                            if (!lastApp.isHasInstance() || !theLastAppInOneSecond
                                    .getPackageName().equals(lastApp.getPkgName())) {
                                if (watchingList
                                        .containsKey(theLastAppInOneSecond.getPackageName())) {
                                    currentApp.setAll(theLastAppInOneSecond.getPackageName(),
                                            theLastAppInOneSecond.getLastTimeUsed(), true);
                                } else {
                                    currentApp.setAll(theLastAppInOneSecond.getPackageName(),
                                            theLastAppInOneSecond.getLastTimeUsed(), false);
                                }

                                if (lastApp.isInWatchingList()) onAppSwitched();

                                lastApp.setAll(currentApp.getPkgName(),
                                        currentApp.getStartTime(), currentApp.isInWatchingList());

                                Log.d(TAG, "app changed to" + currentApp.getPkgName() + " at " + currentApp.getStartTime());
                            }
                        }
                    }
                    // observe every second
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    onThreadWait();
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
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: Unbind " + this.toString());

        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate:" + this.toString());

        super.onCreate();

        initVariables();
        initWatchingList();
        initWatchingThread();
        registerScreenStatusReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int a, int b) {
        Log.d(TAG, "onStartCommand" + this.toString());
        if (intent != null) {
            boolean shouldShow = intent.getBooleanExtra("showNotification", true);
            if (shouldShow) startNotification();
        }

        return super.onStartCommand(intent, a, b);
    }

    @Override
    public void onDestroy() {
        watchingForegroundAppThread.onThreadPause();
        storeAppInformation();
        storeWatchingList();

        unregisterReceiver(mScreenStatusReceiver);
        Log.d(TAG, "onDestroy: 服务结束");
        super.onDestroy();
    }

    private void initVariables() {
        hasExperiencedScreenChanged = false;
        lastApp = new TickAppInfo();
        currentApp = new TickAppInfo();

        watchingList = new HashMap<>();
        usageBinder = new UsageBinder();
        fileUtils = new FileUtils(this);
        mKeyguardManager = (KeyguardManager) this.getSystemService(Context
                .KEYGUARD_SERVICE);

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

    // update the old app when app switched
    private void onAppSwitched() {
        AppUsage targetApp = watchingList.get(lastApp.getPkgName());

        String today = AppUsage.getDateInString(new Date());

        // 如果这个应用使用期间经历了锁屏，只计算上次开启屏幕到当前的时间
        long nowTime = System.currentTimeMillis();
        if (hasExperiencedScreenChanged) {
            targetApp.updateUsingHistory(today, nowTime -
                    mScreenStatusReceiver.getScreenOnTime(), nowTime);

            hasExperiencedScreenChanged = false;

            Log.d(TAG, "上个app: " + targetApp.getPackageName());
            Log.d(TAG, "上次开始（屏幕亮起）: " + mScreenStatusReceiver.getScreenOnTime());
            Log.d(TAG, "上次用时： " + (nowTime - mScreenStatusReceiver.getScreenOnTime()));
            Log.d(TAG, "总次数： " + targetApp.getUsingHistory().get(today).getUsedCount());
            Log.d(TAG, "======================================================");
        } else {
            targetApp.updateUsingHistory(today, nowTime - lastApp.getStartTime(), nowTime);

            Log.d(TAG, "上个app: " + targetApp.getPackageName());
            Log.d(TAG, "上次开始（正常开启）: " + lastApp.getStartTime());
            Log.d(TAG, "上次用时： " + (nowTime - lastApp.getStartTime()));
            Log.d(TAG, "总次数： " + targetApp.getUsingHistory().get(today).getUsedCount());
            Log.d(TAG, "======================================================");
        }
    }

    // update the notification (stop and reshow)
    private void updateNotification() {
        stopForeground(true);

        startNotification();
    }

    // start the foreground service - notification
    private void startNotification() {
        Intent i = new Intent(this, SplashActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);

        String text;
        if (watchingList.size() == 0) {
            text = "没有需要监听的应用";
        } else {
            String appName = "";
            for (Map.Entry<String, AppUsage> app : watchingList.entrySet()) {
                appName = app.getValue().getRealName();
            }

            if (watchingList.size() == 1) {
                text = "正在监听 " + appName;
            } else {
                text = "正在监听 " + appName + " 等" + watchingList.size() + "个应用";
            }
        }

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("X-Timer已启动")
                .setContentText(text)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .build();

        startForeground(1, notification);
    }

    // store app information when exit
    private void storeAppInformation() {
        for (Map.Entry<String, AppUsage> entry : watchingList.entrySet()) {
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

    //注册监听器
    private void registerScreenStatusReceiver() {
        mScreenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        screenStatusIF.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenStatusReceiver, screenStatusIF);
    }


    //response for screen state changed
    private class ScreenStatusReceiver extends BroadcastReceiver {
        private static final String USER_PRESENT = "android.intent.action" +
                ".USER_PRESENT";
        private static final String SCREEN_OFF = "android.intent.action.SCREEN_OFF";
        private static final String SCREEN_ON = "android.intent.action" +
                ".SCREEN_ON";

        private long screenOffTime, screenOnTime;

        private boolean isLocked = false;

        public long getScreenOffTime() {
            return screenOffTime;
        }

        public long getScreenOnTime() {
            return screenOnTime;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            long nowTime = System.currentTimeMillis();

            if ((SCREEN_ON.equals(intent.getAction()) &&
                    !mKeyguardManager.inKeyguardRestrictedInputMode())
                    || USER_PRESENT.equals(intent.getAction())) {      // 检测到打开屏幕(无锁屏)或解锁
                isLocked = false;

                screenOnTime = nowTime;
                // 线程休眠
                watchingForegroundAppThread.onThreadResume();

                Log.d(TAG, "onReceive: 打开屏幕或解锁at" + screenOnTime);

            } else if (SCREEN_OFF.equals(intent.getAction()) && !isLocked) {  // 检测到关闭屏幕
                isLocked = true;

                // 线程重启
                watchingForegroundAppThread.onThreadPause();

                if (currentApp.isInWatchingList()) {   // 如果需要更新当前应用使用时间
                    String today = AppUsage.getDateInString(new Date());
                    screenOffTime = nowTime;

                    Log.d(TAG, "onReceive: 关闭屏幕at" + screenOffTime);

                    AppUsage targetApp = watchingList.get(currentApp.getPkgName());
                    if (hasExperiencedScreenChanged) {  // 当前应用不止一次经历屏幕开闭
                        targetApp.updateUsingHistory(today,
                                screenOffTime - screenOnTime, screenOffTime);

                        Log.d(TAG, "onReceive: app：" + currentApp.getPkgName());
                        Log.d(TAG, "onReceive: 上次开始时间（屏幕亮起）：" + screenOnTime);
                        Log.d(TAG, "onReceive: 上次用时：" + (screenOffTime - screenOnTime));
                        Log.d(TAG, "总次数： " + targetApp.getUsingHistory().get(today).getUsedCount());
                        Log.d(TAG, "======================================================");
                    } else {      // 应用第一次经历屏幕开闭
                        targetApp.updateUsingHistory(today,
                                screenOffTime - currentApp.getStartTime(), screenOffTime);

                        Log.d(TAG, "onReceive: app：" + currentApp.getPkgName());
                        Log.d(TAG, "onReceive: 上次开始时间（应用启动）：" + currentApp.getStartTime());
                        Log.d(TAG, "onReceive: 上次用时：" + (screenOffTime - currentApp.getStartTime()));
                        Log.d(TAG, "总次数： " + targetApp.getUsingHistory().get(today).getUsedCount());
                        Log.d(TAG, "======================================================");
                    }

                    hasExperiencedScreenChanged = true;
                }
            }
        }
    }

    private void showBox() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示");
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.setCancelable(false);
        AlertDialog mDialog = dialog.create();
        mDialog.getWindow().setType(WindowManager.LayoutParams
                .TYPE_SYSTEM_ALERT);//设定为系统级警告，关键
        mDialog.show();
    }

    private Handler timeLimitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TIME_LIMIT) {
                showBox();
            }
        }
    };

}
