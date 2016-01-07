package com.crossbow.app.x_timer.detail.day_detail;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.crossbow.app.x_timer.detail.AnimatedExpandableListView;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.detail.UsageItem;
import com.crossbow.app.x_timer.utils.FileUtils;
import com.crossbow.app.x_timer.service.AppUsage;
import com.crossbow.app.x_timer.service.TickTrackerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wanglx on 2016/1/2.
 */
public class DayDetailActivity extends AppCompatActivity {

    private final String TAG = "DayDetailActivity";

    private AnimatedExpandableListView listView;
    private DayDetailAdapter adapter;
    private List<PackageInfo> packages;
    private FileUtils fileUtils;
    private String date;

    private LinearLayout hasInfo;
    private RelativeLayout noInfo;

    // 联系service
    private TickTrackerService.UsageBinder usageBinder;
    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.day_detail_main);

        initToolbar();
        initStatusBar();
        initConnection();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");

        super.onResume();

        if (isWorking()) {
            bindTickService();
        } else {
            initLayout();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");

        super.onPause();

        if (isWorking()) unbindTickService();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");

        super.onBackPressed();

        finish();
    }

    // handle toolbar
    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.day_detail_toolbar);
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

        // set title
        date = getIntent().getStringExtra("date");
        getSupportActionBar().setTitle(date);
    }

    // handle the status bar
    private void initStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    // handle the layout
    private void initLayout() {
        hasInfo = (LinearLayout)findViewById(R.id.day_detail_yes);
        noInfo = (RelativeLayout)findViewById(R.id.day_detail_no);

        if (initAdapter()) {
            hasInfo.setVisibility(View.VISIBLE);
            noInfo.setVisibility(View.GONE);
        } else {
            hasInfo.setVisibility(View.GONE);
            noInfo.setVisibility(View.VISIBLE);
        }
    }

    // init the adapter and list view
    private boolean initAdapter() {
        adapter = new DayDetailAdapter(this);

        List<DayAppItem> items = new ArrayList<>();

        // read file
        fileUtils = new FileUtils(this);

        // if the app has been used in the certain date
        for (AppUsage app: fileUtils.getAllStoredApp()) {
            Map<String, AppUsage.History> history = app.getUsingHistory();
            if (history.containsKey(date)) {
                Drawable appIcon = findAppIcon(app.getPackageName());

                List<UsageItem> usages = new ArrayList<>();
                AppUsage.History theDay = history.get(date);
                ArrayList<AppUsage.History.Record> records = theDay.getUsingRecord();

                for (AppUsage.History.Record r: records) {
                    UsageItem usage = new UsageItem(r.getStartTime(),
                            r.getEndTime(), r.getDuration());
                    usages.add(usage);
                }

                DayAppItem item = new DayAppItem(app.getRealName(), appIcon,
                        theDay.getTotalTime(), theDay.getUsedCount(), usages);
                items.add(item);
            }
        }

        // no record that day
        if (items.isEmpty()) return false;

        adapter.setData(items);

        listView = (AnimatedExpandableListView) findViewById(R.id.day_detail_list);
        listView.setAdapter(adapter);

        // on click animation
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });

        return true;
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

    // init the connection
    private void initConnection() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onBinded: ");

                usageBinder = (TickTrackerService.UsageBinder) service;

                usageBinder.manuallySaveData();

                // 异步绑定
                initLayout();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "service error");

                usageBinder = null;
            }
        };
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
    private boolean isWorking() {
        ActivityManager myAM = (ActivityManager)getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(150);

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

}
