package com.crossbow.app.x_timer;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.RadioButton;

import android.widget.Toast;

import com.crossbow.app.x_timer.service.TickTrackerService;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, Toolbar.OnMenuItemClickListener, ViewPager.OnPageChangeListener {

    private final String TAG = "main";

    public static TickTrackerService.UsageBinder usageBinder;

    private ServiceConnection connection;

    private ViewPager viewPager;

    private RadioButton tab_home;
    private RadioButton tab_history;
    private RadioButton tab_detail;
    private RadioButton tab_setting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        initToolbar();

        // fragment and radio group
        initTab();

        // permission
        if (!hasPermission()) {
            requestPermission();
        }

        // connection
        initConnection();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        if (isWorking()) bindTickService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");

        if (isWorking()) unbindTickService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    // handle navigation menu
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_main) {

        } else if (id == R.id.nav_cloud) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_advice) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_check) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // handle button
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radio_home:
                viewPager.setCurrentItem(0);

                break;

            case R.id.radio_history:
                viewPager.setCurrentItem(1);

                break;

            case R.id.radio_detail:
                viewPager.setCurrentItem(2);

                break;

            case R.id.radio_setting:
                viewPager.setCurrentItem(3);

                break;

            default:
                break;
        }
    }

    // handle view change
    @Override
    public void onPageSelected(int id){
        switch(id){
            case 0:
                changeAllTabColor();

                tab_home.setChecked(true);
                tab_home.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.radio_home_active, 0, 0);

                break;

            case 1:
                changeAllTabColor();

                tab_history.setChecked(true);
                tab_history.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.radio_history_active, 0, 0);

                break;

            case 2:
                changeAllTabColor();

                tab_detail.setChecked(true);
                tab_detail.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.radio_detail_active, 0, 0);

                break;

            case 3:
                changeAllTabColor();

                tab_setting.setChecked(true);
                tab_setting.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.radio_setting_active, 0, 0);

                break;

            default:
                break;
        }
    }

    // not used
    @Override
    public void onPageScrolled(int arg0,float arg1,int arg2) { }

    // not used
    @Override
    public void onPageScrollStateChanged(int arg0) { }

    // handle toolbar menu
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_share:
                Toast.makeText(this, "未实现", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return true;
    }

    // get the viewpager instance
    public ViewPager getViewPager() {
        return viewPager;
    }

    // return the service binder
    public TickTrackerService.UsageBinder getBinder() {
        return usageBinder;
    }

    // init the connection
    private void initConnection() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                usageBinder = (TickTrackerService.UsageBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    // init the toolbar
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        toolbar.setOnMenuItemClickListener(this);
    }

    // init the tab
    private void initTab() {
        // radio group
        tab_home = (RadioButton) findViewById(R.id.radio_home);
        tab_history = (RadioButton) findViewById(R.id.radio_history);
        tab_detail = (RadioButton) findViewById(R.id.radio_detail);
        tab_setting = (RadioButton) findViewById(R.id.radio_setting);
        tab_home.setOnClickListener(this);
        tab_history.setOnClickListener(this);
        tab_detail.setOnClickListener(this);
        tab_setting.setOnClickListener(this);

        // fragment
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), this));
        viewPager.addOnPageChangeListener(this);
        // viewpager缓存数
        viewPager.setOffscreenPageLimit(4);
    }

    // change all tab to unactive
    private void changeAllTabColor() {
        tab_home.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.radio_home, 0, 0);
        tab_history.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.radio_history, 0, 0);
        tab_detail.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.radio_detail, 0, 0);
        tab_setting.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.radio_setting, 0, 0);
    }

    // check if the user has system permission
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    // TODO not finished
    // if no permission, show info
    private void requestPermission() {
        Toast.makeText(this, "no permission", Toast.LENGTH_LONG).show();
    }

    // start the TickTracker Service
    public void startTickService() {
        Intent intent = new Intent(this, TickTrackerService.class);
        startService(intent);
        bindTickService();

        Toast.makeText(this, "started", Toast.LENGTH_LONG).show();
    }

    // stop the TickTracker Service
    public void stopTickService() {
        Intent intent = new Intent(this, TickTrackerService.class);
        stopService(intent);
        unbindTickService();

        Toast.makeText(this, "stopped", Toast.LENGTH_LONG).show();
    }

    // bind the service
    public void bindTickService() {
        Intent intent = new Intent(this, TickTrackerService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    // unbind the service
    public void unbindTickService() {
        unbindService(connection);
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
