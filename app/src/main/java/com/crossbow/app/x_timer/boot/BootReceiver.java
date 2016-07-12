package com.crossbow.app.x_timer.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.crossbow.app.x_timer.service.AutoPersistenceService;
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
            Intent startTickTrackerService = new Intent(context, TickTrackerService.class);
			startTickTrackerService.putExtra("showNotification", shouldShowNotification());
            mContext.startService(startTickTrackerService);

            Toast.makeText(context, "监听已开启", Toast.LENGTH_LONG).show();

			// 同时开启自动保存
            Intent startAutoPersistenceService = new Intent(context, AutoPersistenceService.class);
			mContext.startService(startAutoPersistenceService);
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
