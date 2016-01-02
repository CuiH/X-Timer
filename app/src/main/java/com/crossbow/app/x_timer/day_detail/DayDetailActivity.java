package com.crossbow.app.x_timer.day_detail;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.Utils.FileUtils;
import com.crossbow.app.x_timer.add_app.AppInfo;
import com.crossbow.app.x_timer.service.AppUsage;
import com.crossbow.app.x_timer.service.TickTrackerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wanglx on 2016/1/2.
 */
public class DayDetailActivity extends AppCompatActivity {
    private AnimatedExpandableListView listView;
    private DayDetailAdapter adapter;
    private List<PackageInfo> packages;
    private TickTrackerService.UsageBinder usageBinder;
    private FileUtils fileUtils;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_detail_main);

        initToolbar();
        initStatusBar();
        initInstalledAppInfo();


        initAdapter();
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

    // init the adapter and list view
    private void initAdapter() {
        adapter = new DayDetailAdapter(this);

        List<AppItem> items = new ArrayList<>();

        // if the service is working, save date first;
        if (isWorking()) manuallySaveData();

        // read file
        fileUtils = new FileUtils(this);

        // if the app has been used in the certain date
        for (AppUsage app: fileUtils.getAllStoredApp()) {
            Map<String, AppUsage.History> history = app.getUsingHistory();
            if (history.containsKey(date)) {
                AppItemCopy itemCopy = findAppInfo(app.getPackageName());

                List<UsageItem> usages = new ArrayList<>();
                AppUsage.History theDay = history.get(date);
                ArrayList<AppUsage.History.Record> records = theDay.getUsingRecord();

                for (AppUsage.History.Record r: records) {
                    UsageItem usage = new UsageItem(r.getStartTime(), r.getEndTime(), r.getDuration());
                    usages.add(usage);
                }

                AppItem item = new AppItem(itemCopy.appName, itemCopy.appIcon, records.size(), usages);
                items.add(item);
            }
        }

        adapter.setData(items);

        listView = (AnimatedExpandableListView) findViewById(R.id.listView);
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
    }

    // get all installed app info
    private void initInstalledAppInfo() {
        packages = getPackageManager().getInstalledPackages(0);
    }

    // save data first
    private void manuallySaveData() {
        usageBinder = MainActivity.usageBinder;
        usageBinder.manuallySaveData();
    }

    // get the app info
    private AppItemCopy findAppInfo(String pkgName) {
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            String packageName = packageInfo.packageName;
            if (packageName.equals(pkgName)) {
                String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
                AppItemCopy copy = new AppItemCopy(appName, appIcon);

                return copy;
            }
        }

        return new AppItemCopy(pkgName, null);
    }

    // check if the service is working
    private boolean isWorking() {
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

    // only record the name and icon
    private static class AppItemCopy {
        String appName;
        Drawable appIcon;

        AppItemCopy(String name, Drawable icon) {
            appName = name;
            appIcon = icon;
        }
    }
}
