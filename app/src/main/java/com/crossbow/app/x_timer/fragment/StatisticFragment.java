package com.crossbow.app.x_timer.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crossbow.app.x_timer.activity.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.entity.AppDayItem;
import com.crossbow.app.x_timer.entity.AppUsage;
import com.crossbow.app.x_timer.persistence.FileUtils;
import com.devspark.progressfragment.ProgressFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by CuiH on 2015/12/29.
 */
public class StatisticFragment extends ProgressFragment {

	private final String TAG = "StatisticFragment";

	private MainActivity mainActivity;

	// 异步用
	private View realView;
	private boolean firstTime;
	private boolean isVisible;
	private boolean forceRefresh;

	private List<AppUsage> appUsageList;

	// 更新UI的数据
	private String dayLengthUsed;
	private String mostCountUsedAppName;
	private String mostCountUsedAppCount;
	private String mostDayUsedAppName;
	private String mostDayUsedAppDays;
	private String mostTimeUsedAppName;
	private String mostTimeUsedAppTime;



	public StatisticFragment() { }

	@SuppressLint("ValidFragment")
	public StatisticFragment(MainActivity activity) {
		mainActivity = activity;
		firstTime = true;
		forceRefresh = true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		Log.d(TAG, "onCreateView: ");

		realView = inflater.inflate(R.layout.statistic_fragment, container, false);

		return inflater.inflate(R.layout.fragment_loading, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated: ");

		super.onActivityCreated(savedInstanceState);
		setContentView(realView);
		setContentShown(false);

		if (isVisible) {
			if (firstTime) {
				new MyAsyncTask().execute();

				firstTime = false;
			}
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		Log.d(TAG, "setUserVisibleHint: ");

		if (isVisibleToUser) {
			isVisible = true;

			if (forceRefresh) {
				new MyAsyncTask().execute();

				forceRefresh = false;
			}
		} else {
			isVisible = false;
		}

		super.setUserVisibleHint(isVisibleToUser);
	}

	// 异步更新UI
	private class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			initDayLengthUsed();
			getStoredAppInfo();
			initMostCountUsedApp();
			initMostDayUsedApp();
			initMostTimeUsedApp();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			initUI();

			setContentShown(true);
		}
	}

	public void setFirstTime(boolean flag) {
		firstTime = flag;
	}

	public void setForceRefresh(boolean flag) {
		forceRefresh = flag;
	}

	// 计算共使用了X-Timer多少天
	private void initDayLengthUsed() {
		SharedPreferences pref = mainActivity
			.getSharedPreferences("settings", Context.MODE_PRIVATE);

		Date today = new Date();

		String startString = pref.getString("firstDate", AppUsage.getDateInString(today));
		Date startDate = new Date();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startDate = sdf.parse(startString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		int day1 = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.setTime(today);
		int day2 = calendar.get(Calendar.DAY_OF_YEAR);

		dayLengthUsed = "" + (day2-day1) + "\n天";
	}

	// 使用次数最多的应用
	private void initMostCountUsedApp() {
		if (appUsageList.isEmpty()) {
			mostCountUsedAppCount = "暂无数据";
			mostCountUsedAppName = "暂无数据";

			return;
		}

		AppUsage mostUsed = null;
		long mostUsedCount = -1;
		for (AppUsage appUsage: appUsageList) {
			long nowCount = 0;
			for (Map.Entry<String, AppUsage.History> historyEntry:
				appUsage.getUsingHistory().entrySet()) {
				nowCount += historyEntry.getValue().getUsedCount();
			}

			if (nowCount > mostUsedCount) {
				mostUsedCount = nowCount;
				mostUsed = appUsage;
			}
		}

		if (mostUsed != null) {
			mostCountUsedAppName = mostUsed.getRealName();
			mostCountUsedAppCount = ""+mostUsedCount + "\n次";
		} else {
			mostCountUsedAppCount = "暂无数据";
			mostCountUsedAppName = "暂无数据";
		}
	}

	// 使用天数最多的应用
	private void initMostDayUsedApp() {
		if (appUsageList.isEmpty()) {
			mostDayUsedAppDays = "暂无数据";
			mostDayUsedAppName = "暂无数据";

			return;
		}

		AppUsage mostUsed = null;
		long mostUsedCount = -1;
		for (AppUsage appUsage: appUsageList) {
			long nowCount = appUsage.getUsingHistory().entrySet().size();

			if (nowCount > mostUsedCount) {
				mostUsedCount = nowCount;
				mostUsed = appUsage;
			}
		}

		if (mostUsed != null) {
			mostDayUsedAppName = mostUsed.getRealName();
			mostDayUsedAppDays = "" + mostUsedCount + "\n天";
		} else {
			mostDayUsedAppDays = "暂无数据";
			mostDayUsedAppName = "暂无数据";
		}
	}

	// 使用时间最多的应用
	private void initMostTimeUsedApp() {
		if (appUsageList.isEmpty()) {
			mostTimeUsedAppTime = "暂无数据";
			mostTimeUsedAppName = "暂无数据";

			return;
		}

		AppUsage mostTime = null;
		long mostTimeCount = -1;
		for (AppUsage appUsage: appUsageList) {
			long nowTime = 0;
			for (Map.Entry<String, AppUsage.History> historyEntry:
				appUsage.getUsingHistory().entrySet()) {
				nowTime += historyEntry.getValue().getTotalTime();
			}

			if (nowTime > mostTimeCount) {
				mostTimeCount = nowTime;
				mostTime = appUsage;
			}
		}

		if (mostTime != null) {
			mostTimeUsedAppName = mostTime.getRealName();
			mostTimeUsedAppTime = ""+ AppDayItem.transferLongToTime(mostTimeCount);
		} else {
			mostTimeUsedAppTime = "暂无数据";
			mostTimeUsedAppName = "暂无数据";
		}
	}

	private void initUI() {
		TextView textView = (TextView)realView.findViewById(R.id.statistic_day_length_all);
		textView.setText(dayLengthUsed);

		TextView name1 = (TextView)realView.findViewById(R.id.statistic_most_count_app);
		TextView count = (TextView)realView.findViewById(R.id.statistic_most_count_app_count);
		name1.setText(mostCountUsedAppName);
		count.setText(mostCountUsedAppCount);

		TextView name2 = (TextView)realView.findViewById(R.id.statistic_most_day_app);
		TextView days = (TextView)realView.findViewById(R.id.statistic_most_day_app_count);
		name2.setText(mostDayUsedAppName);
		days.setText(mostDayUsedAppDays);

		TextView name3 = (TextView)realView.findViewById(R.id.statistic_most_time_app);
		TextView time = (TextView)realView.findViewById(R.id.statistic_most_time_app_count);
		name3.setText(mostTimeUsedAppName);
		time.setText(mostTimeUsedAppTime);

	}

	// 获取存储的所有app信息
	private void getStoredAppInfo() {
		FileUtils fileUtils = new FileUtils(mainActivity);
		appUsageList = fileUtils.getAllStoredApp();
	}

}
