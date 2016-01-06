package com.crossbow.app.x_timer.splash;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.service.TickTrackerService;

import java.util.List;

/**
 * Created by CuiH on 2016/1/6.
 */
public class SplashActivity extends AppCompatActivity {
    private final String TAG = "SplashActivity";
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    // if exit the splash
    private boolean status;

    private ServiceConnection connection;
    private TickTrackerService.UsageBinder usageBinder;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash_layout);

        initScreen();
        initDelay();

        checkWorking();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");

        if (isWorking()) unbindTickService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");

       status = false;
    }

    // set the activity full screen
    private void initScreen() {
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
    }

    // init delay, after SPLASH_DISPLAY_LENGTH, main activity will be started
    private void initDelay() {
        status = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (status) {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    SplashActivity.this.finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    // check if the service is working, if so, manually save data
    // (it should be finished in SPLASH_DISPLAY_LENGTH)
    private void checkWorking() {
        if (isWorking()) {
            initConnection();
            bindTickService();
        }
    }

    // init the connection
    private void initConnection() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onBinded: ");

                // if is working, manually save data
                usageBinder = (TickTrackerService.UsageBinder) service;
                usageBinder.manuallySaveData();
                System.out.println("save finished");
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
