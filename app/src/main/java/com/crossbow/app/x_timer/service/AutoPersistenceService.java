package com.crossbow.app.x_timer.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crossbow.app.x_timer.receiver.AlarmReceiver;

import java.util.List;

/**
 * Created by CuiH on 2016/7/12.
 */
public class AutoPersistenceService extends Service {

	private static final String TAG = "AutoPersistenceService";

	// 自动保存周期，秒
	private static final int PERIOD = 600;

	// 联系service
	private TickTrackerService.UsageBinder usageBinder;
	private ServiceConnection connection;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate:" + this.toString());

		super.onCreate();

		initConnection();
	}

	@Override
	public int onStartCommand(Intent intent, int a, int b) {
		Log.d(TAG, "onStartCommand:" + this.toString());

		if (isWorking()) {
			bindTickService();
		}

		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		long period = SystemClock.elapsedRealtime() + PERIOD*1000;
		Intent in = new Intent(this, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, in, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, period, pi);

		return super.onStartCommand(intent, a, b);
	}

	// init the connection
	private void initConnection() {
		connection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.d(TAG, "onBinded:");

				usageBinder = (TickTrackerService.UsageBinder) service;

				usageBinder.manuallySaveData();

				Log.d(TAG, "auto persist success" );

				unbindTickService();
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
