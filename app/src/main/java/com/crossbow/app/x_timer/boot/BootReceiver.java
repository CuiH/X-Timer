package com.crossbow.app.x_timer.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.crossbow.app.x_timer.service.TickTrackerService;

/**
 * Created by CuiH on 2016/01/05.
 */
public class BootReceiver extends BroadcastReceiver {

    // 本地存储
    private SharedPreferences pref;

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        if (shouldStartWhenBoot()) {
            Intent startService = new Intent(context, TickTrackerService.class);
            startService.putExtra("showNotification", shouldShowNotification());
            mContext.startService(startService);

            Toast.makeText(context, "监听已开启", Toast.LENGTH_LONG).show();
        }
    }

    // check if should start when boot
    public boolean shouldStartWhenBoot() {
        if (pref == null) pref = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);

        return pref.getBoolean("boot", true);
    }

    // check if should start a notification
    public boolean shouldShowNotification() {
        if (pref == null) pref = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);

        return pref.getBoolean("show", true);
    }
}
