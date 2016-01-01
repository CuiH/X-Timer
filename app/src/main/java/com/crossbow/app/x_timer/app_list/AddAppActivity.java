package com.crossbow.app.x_timer.app_list;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.service.TickTrackerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglx on 2016/1/1.
 */
public class AddAppActivity extends AppCompatActivity {
    private List<AppInfo> appList;
    private AppInfoAdapter appInfoAdapter;
    private ArrayList<String> selected;
    private TextView showInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_app);

        // toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.add_app_toolbar);
        setSupportActionBar(toolbar);

        // change toolbar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        appList = new ArrayList<>();
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

        // 所有应用信息
        for (int i = 0;i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            // 不要系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;

            String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            String packageName = packageInfo.packageName;
            Drawable appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
            AppInfo tmpInfo =new AppInfo(packageName, appName, appIcon);
            appList.add(tmpInfo);
        }

        appInfoAdapter = new AppInfoAdapter(this, R.layout.app_item, appList);

        // 传递已选信息
        Bundle bundle = getIntent().getExtras();
        selected = bundle.getStringArrayList("selected");

        appInfoAdapter.setDefault(selected);

        showInfo = (TextView) findViewById(R.id.add_app_text);

        ListView listView = (ListView)findViewById(R.id.app_list);
        listView.setAdapter(appInfoAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AddAppActivity.this, "111", Toast.LENGTH_SHORT).show();
                CheckBox check = (CheckBox)view.findViewById(R.id.app_check_box);
                if (check.isChecked()) {
                    check.setChecked(false);
                    selected.remove(appList.get(position).getPackageName());
                    showInfo.setText("已选"+selected.size()+"个应用");
                } else {
                    check.setChecked(true);
                    selected.add(appList.get(position).getPackageName());
                    showInfo.setText("已选"+selected.size()+"个应用");
                }
            }
        });

        showInfo.setText("已选"+selected.size()+"个应用");

        // 点击 确定
        Button ok = (Button)findViewById(R.id.add_app_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWorking()) {
                    Toast.makeText(AddAppActivity.this, "失败，请先开启服务", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                TickTrackerService.UsageBinder binder = MainActivity.usageBinder;
                for (String appName: selected) {
                    if (!binder.isInWatchingList(appName)) {
                        binder.addAppToWatchingList(appName);
                    }
                }

                Toast.makeText(AddAppActivity.this, "成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // 点击 取消
        Button cancel = (Button)findViewById(R.id.add_app_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

}
