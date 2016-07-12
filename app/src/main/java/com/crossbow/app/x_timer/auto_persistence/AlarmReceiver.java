package com.crossbow.app.x_timer.auto_persistence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crossbow.app.x_timer.service.AutoPersistenceService;

/**
 * Created by CuiH on 2016/7/12.
 */
public class AlarmReceiver extends BroadcastReceiver {

	private static final String TAG = "BroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive:" + this.toString());

		Intent in = new Intent(context, AutoPersistenceService.class);
		context.startService(in);
	}

}
