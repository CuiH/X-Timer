package com.crossbow.app.x_timer.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.crossbow.app.x_timer.activity.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.activity.TimerActivity;
import com.crossbow.app.x_timer.entity.SettingAdapter;
import com.crossbow.app.x_timer.entity.SettingInfo;
import com.crossbow.app.x_timer.persistence.FileUtils;
import com.crossbow.app.x_timer.activity.AddAppActivity;
import com.devspark.progressfragment.ProgressFragment;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.drakeet.materialdialog.MaterialDialog;


/**
 * Created by CuiH on 2015/12/29.
 */
public class SettingFragment extends ProgressFragment implements AdapterView.OnItemClickListener {

	private final String TAG = "SettingFragment";

	private MainActivity mainActivity;

	private SettingAdapter settingAdapter;
	private List<SettingInfo> settingList;
	private ListView listView;

	private MaterialDialog dialog;

	// 异步用
	private View realView;
	private boolean firstTime;
	private boolean isVisible;
	private boolean forceRefresh;


	public SettingFragment() { }

	@SuppressLint("ValidFragment")
	public SettingFragment(MainActivity activity) {
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

		realView = inflater.inflate(R.layout.setting_fragment, container, false);

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
			try {
				Thread.sleep(200);
			} catch (Exception e) {
				e.printStackTrace();
			}

			initSettingList();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			initAdapter(realView);

			setContentShown(true);
		}
	}

	public void setFirstTime(boolean flag) {
		firstTime = flag;
	}

	public void setForceRefresh(boolean flag) {
		forceRefresh = flag;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ToggleButton tv = (ToggleButton)view.findViewById(R.id.settingButton);

		switch (position) {
			case 0:
				if (!mainActivity.hasPermission()) {
					mainActivity.requestPermission();
					
					break;
				}

				if (mainActivity.isWorking()) {
					mainActivity.stopTickService();
					tv.setChecked(false);
				} else {
					mainActivity.startTickService();
					tv.setChecked(true);
				}

				break;
			case 1:
				if (mainActivity.shouldStartWhenBoot()) {
					mainActivity.updateShouldStartWhenBoot(false);
					tv.setChecked(false);

					Toast.makeText(mainActivity, "已设置不开机启动", Toast.LENGTH_SHORT).show();
				} else {
					mainActivity.updateShouldStartWhenBoot(true);
					tv.setChecked(true);

					Toast.makeText(mainActivity, "已设置开机启动", Toast.LENGTH_SHORT).show();
				}

				break;
			case 2:
				if (mainActivity.shouldShowNotification()) {
					if (mainActivity.isWorking()) {
						mainActivity.getBinder().changeNotificationState(false);
					} else {
						Toast.makeText(mainActivity, "服务未启动", Toast.LENGTH_SHORT).show();
					}

					mainActivity.updateShouldShowNotification(false);
					tv.setChecked(false);
				} else {
					if (mainActivity.isWorking()) {
						mainActivity.getBinder().changeNotificationState(true);
					} else {
						Toast.makeText(mainActivity, "服务未启动", Toast.LENGTH_SHORT).show();
					}

					mainActivity.updateShouldShowNotification(true);
					tv.setChecked(true);
				}

				break;

			case 3:
				if (!mainActivity.isWorking()) {
					Toast.makeText(mainActivity, "请先开启监听服务", Toast.LENGTH_SHORT).show();
					return;
				}

				if (mainActivity.isStartingActivity) return;
				mainActivity.isStartingActivity = true;

				Intent intent = new Intent(mainActivity, AddAppActivity.class);
				startActivityForResult(intent, 1);

				break;

			case 4:
				if (!mainActivity.isWorking()) {
					Toast.makeText(mainActivity, "请先开启监听服务", Toast.LENGTH_SHORT).show();
					return;
				}

				if (mainActivity.isStartingActivity) return;
				mainActivity.isStartingActivity = true;

				Intent intent2 = new Intent(mainActivity, TimerActivity.class);
				startActivityForResult(intent2, 1);

				break;

			case 5:
				if (mainActivity.isWorking()) {
					Toast.makeText(mainActivity, "请先关闭监听服务", Toast.LENGTH_SHORT).show();
					return;
				}

				new SweetAlertDialog(mainActivity, SweetAlertDialog.WARNING_TYPE)
					.setTitleText("确定要清空记录吗")
					.setContentText("该操作不可恢复")
					.setConfirmText("确认")
					.setCancelText("手滑了")
					.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							FileUtils fileUtils = new FileUtils(mainActivity);
							fileUtils.deleteAllAppInfo();

							sDialog.setTitleText("已删除")
								.setContentText("应用历史记录已清空")
								.setConfirmText("好的")
								.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										mainActivity.refreshViewPager();
										sDialog.dismiss();
									}
								})
								.showCancelButton(false)
								.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
						}
					}).show();

				break;
			case 6:
				if (mainActivity.isWorking()) {
					Toast.makeText(mainActivity, "请先关闭监听服务", Toast.LENGTH_SHORT).show();
					return;
				}

				new SweetAlertDialog(mainActivity, SweetAlertDialog.WARNING_TYPE)
					.setTitleText("确定要清空列表吗")
					.setContentText("该操作不可恢复")
					.setConfirmText("确认")
					.setCancelText("手滑了")
					.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							FileUtils fileUtils2 = new FileUtils(mainActivity);
							fileUtils2.deleteAppList();

							sDialog.setTitleText("已删除")
								.setContentText("应用监听列表已清空")
								.setConfirmText("好的")
								.setConfirmClickListener(null)
								.showCancelButton(false)
								.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
						}
					}).show();

				break;

			case 7:
				dialog = new MaterialDialog(mainActivity)
					.setTitle("已知缺陷")
					.setMessage("1. 暂时不支持android5.0(api21)以下版本\n" +
						"2. 如果使用监听中的应用越过午夜12点，使用时间将会" +
						"被算在第二天\n" +
						"3. 由横屏切换竖屏会出错，建议不要使用横屏\n" +
						"4. 关机时数据可能不会被自动保存，所以建议在关机前打开一次应用界面" +
						"以实现自动保存\n" +
						"5. 暂不支持小米、魅族手机"
					)
					.setPositiveButton("知道了", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

				dialog.show();

				break;

			case 8:
				if (mainActivity.hasPermission()) {
					dialog = new MaterialDialog(mainActivity)
						.setTitle("如何使用")
						.setMessage("您应首先开启监听服务，然后配置监听列表，选择您希望监听的" +
							"应用（最好不要太多），随后即可在[首页]及[历史]页面看到应用使" +
							"用情况啦！")
						.setPositiveButton("知道了", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});

					dialog.show();
				} else {
					dialog = new MaterialDialog(mainActivity)
						.setTitle("如何获取权限")
						.setMessage("由于我们的应用将监听手机APP使用情况，您需要为其配置权限，" +
							"否则将无法使用。具体操作为：[设置 - 权限 - 可以访问使用量数据" +
							"的应用程序]（不同手机可能有所不同），然后勾选我们的应用，点击确定即可。" +
							"（我们保证不会记录您的隐私，代码已公布在github，详见“关于我们”页面）。")
						.setPositiveButton("知道了", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});

					dialog.show();
				}

				break;
			default:
				break;
		}
	}

	// handle the adapter
	private void initAdapter(View view) {
		settingAdapter = new SettingAdapter(mainActivity, R.layout.setting_item, settingList);

		listView = (ListView)view.findViewById(R.id.setting_list);
		listView.setAdapter(settingAdapter);

		listView.setOnItemClickListener(this);
	}

	// init the setting list
	private void initSettingList() {
		settingList = new ArrayList<>();
		SettingInfo setting1 = new SettingInfo("监听服务", "开启后才能监听应用使用情况", 2, 1);
		settingList.add(setting1);
		SettingInfo setting2 = new SettingInfo("开机启动", "", 2, 2);
		settingList.add(setting2);
		SettingInfo setting3 = new SettingInfo("通知栏图标", "显示或隐藏通知栏图标", 2, 1);
		settingList.add(setting3);
		SettingInfo setting4 = new SettingInfo("管理监听列表", "添加或删除要监听的应用", 1, 1);
		settingList.add(setting4);
		SettingInfo setting5 = new SettingInfo("设置定时提醒", "为应用设置使用时间提醒", 1, 1);
		settingList.add(setting5);
		SettingInfo setting6 = new SettingInfo("清除历史记录", "清空所有应用使用记录", 1, 1);
		settingList.add(setting6);
		SettingInfo setting7 = new SettingInfo("清空监听列表", "", 1, 2);
		settingList.add(setting7);
		SettingInfo setting8 = new SettingInfo("已知待修复缺陷", "", 1, 2);
		settingList.add(setting8);
		SettingInfo setting9 = new SettingInfo("帮助", "使用说明", 1, 1);
		settingList.add(setting9);
	}

}
