package com.crossbow.app.x_timer.timer;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.add_app.AppInfo;
import com.crossbow.app.x_timer.add_app.AppInfoAdapter;
import com.crossbow.app.x_timer.service.AppUsage;
import com.crossbow.app.x_timer.service.TickTrackerService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by CuiH on 2016/1/8.
 */
public class TimerActivity extends AppCompatActivity {

    private final String TAG = "TimerActivity";

    private List<TimerAppInfo> appList;
    private TimerAdapter timerAdapter;
    private ListView listView;

    private List<PackageInfo> packages;

    private MaterialDialog mMaterialDialog1;
    private MaterialDialog mMaterialDialog2;
    private View dialogView;

    // 联系service
    private TickTrackerService.UsageBinder usageBinder;
    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.timer_main);

        initConnection();
        initToolbar();
        initStatusBar();

        initHelpButton();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");

        super.onResume();

        if (isWorking()) bindTickService();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");

        super.onPause();

        if (isWorking()) unbindTickService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        super.onBackPressed();
        finish();
    }

    // handle toolbar
    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.timer_toolbar);
        setSupportActionBar(toolbar);

        // back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // handle the status bar
    private void initStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    private void initHelpButton() {
        Button help = (Button)findViewById(R.id.timer_help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog2 = new MaterialDialog(TimerActivity.this)
                        .setTitle("关于定时提醒")
                        .setMessage("只可以为监听中的应用设置定时提醒。为应用设置定时提醒时间后，" +
                                "如果您当日使用该应用的时间超过了" +
                                "您为其设定的时长，您将会收到提醒（同一个应用同一天只会收到" +
                                "一次超时提醒）。")
                        .setPositiveButton("知道了", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog2.dismiss();
                            }
                        });

                mMaterialDialog2.show();
            }
        });
    }

    // things to do after binded
    private void afterBinded() {
        initAppList();
        initAdapter();

        handleListView();
    }

    // init the connection
    private void initConnection() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onBinded: ");

                usageBinder = (TickTrackerService.UsageBinder) service;

                // 异步
                afterBinded();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "service error");

                usageBinder = null;
            }
        };
    }

    private void initAppList() {
        appList = new ArrayList<>();

        Map<String, AppUsage> watchingList = usageBinder.getWatchingList();
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, AppUsage> app : watchingList.entrySet()) {
            AppUsage nowApp = app.getValue();

            long usedTime;
            if (nowApp.getUsingHistory().size() <= 0) {
                usedTime = 0;
            } else {
                AppUsage.History history = nowApp.getUsingHistory()
                        .get(AppUsage.getDateInString(new Date()));
                if (history == null) {
                    usedTime = 0;
                } else {
                    usedTime = history.getTotalTime();
                }
            }

            if (nowApp.isHasLimit()) {
                TimerAppInfo appInfo = new TimerAppInfo(nowApp.getPackageName(),
                        nowApp.getRealName(), findAppIcon(nowApp.getPackageName()),
                        usedTime, true, nowApp.getLimitLength());
                appList.add(appInfo);
            } else {
                TimerAppInfo appInfo = new TimerAppInfo(nowApp.getPackageName(),
                        nowApp.getRealName(), findAppIcon(nowApp.getPackageName()),
                        usedTime, false, 0);
                appList.add(appInfo);
            }
        }
    }

    // handle the adapter
    private void initAdapter() {
        timerAdapter = new TimerAdapter(this, R.layout.timer_app_item, appList);

        listView = (ListView)findViewById(R.id.timer_app_list);
        listView.setAdapter(timerAdapter);
    }

    private void handleListView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                dialogView = LayoutInflater.from(TimerActivity.this)
                        .inflate(R.layout.timer_dialog, null);

                mMaterialDialog1 = new MaterialDialog(TimerActivity.this)
                        .setTitle(appList.get(position).getRealName())
                        .setMessage("")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!isWorking()) {
                                    Toast.makeText(TimerActivity.this, "失败，请先开启监听服务",
                                            Toast.LENGTH_SHORT).show();

                                    return;
                                }

                                TextView textView = (TextView) dialogView
                                        .findViewById(R.id.dialog_time);
                                String timeInString = textView.getText().toString();
                                if (!isNumeric(timeInString) || timeInString.isEmpty()) {
                                    Toast.makeText(TimerActivity.this, "失败，请输入数字！",
                                            Toast.LENGTH_SHORT).show();

                                    return;
                                }

                                long time = Long.parseLong(timeInString);

                                TimerAppInfo nowApp = appList.get(position);
                                if (usageBinder.setLimitToApp(nowApp.getPkgName(), time*1000)) {
                                    Toast.makeText(TimerActivity.this, "成功",
                                            Toast.LENGTH_SHORT).show();

                                    if (time == 0) {
                                        nowApp.setHasTimer(false);
                                    } else {
                                        nowApp.setHasTimer(true);
                                        nowApp.setLimit(time * 1000);
                                    }

                                    timerAdapter.notifyDataSetChanged();

                                    setResult(RESULT_OK);
                                } else {
                                    Toast.makeText(TimerActivity.this, "失败",
                                            Toast.LENGTH_SHORT).show();
                                }

                                mMaterialDialog1.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog1.dismiss();
                            }
                        });

                mMaterialDialog1.setContentView(dialogView);
                mMaterialDialog1.show();
            }
        });
    }

    // bind the service
    public void bindTickService() {
        Log.d(TAG, "onBinding:");

        Intent intent = new Intent(this, TickTrackerService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    // unbind the service
    public void unbindTickService() {
        unbindService(connection);

        Log.d(TAG, "onUnbinded:");
    }

    // check if the service is working
    public boolean isWorking() {
        ActivityManager myAM = (ActivityManager)getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);

        if (myList.size() <= 0) {
            return false;
        }

        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals("com.crossbow.app.x_timer.service.TickTrackerService")) {
                return true;
            }
        }
        return false;
    }

    // get the app info
    private Drawable findAppIcon(String pkgName) {
        if (packages == null ) if (packages == null )packages
                = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            String packageName = packageInfo.packageName;
            if (packageName.equals(pkgName)) {
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());

                return appIcon;
            }
        }

        return null;
    }

    // 判断字符串是不是数字
    public static boolean isNumeric(String str){
        for (int i = 0; i < str.length(); i++){
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

}
