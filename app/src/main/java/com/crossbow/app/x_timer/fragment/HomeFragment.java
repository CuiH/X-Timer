package com.crossbow.app.x_timer.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.crossbow.app.x_timer.activity.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.entity.AppChartItem;
import com.crossbow.app.x_timer.entity.AppUsage;
import com.crossbow.app.x_timer.persistence.FileUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.devspark.progressfragment.ProgressFragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

/**
 * Created by CuiH on 2015/12/29.
 */
public class HomeFragment extends ProgressFragment implements OnChartValueSelectedListener {

	private final String TAG = "HomeFragment";

	private PieChart mChart;

	private MainActivity mainActivity;

	private List<AppChartItem> list;

	private PieData data;

	// 异步用
	private View realView;
	private boolean firstTime;
	private boolean isVisible;
	private boolean forceRefresh;


	public HomeFragment() { }

	@SuppressLint("ValidFragment")
	public HomeFragment(MainActivity activity) {
		mainActivity = activity;
		firstTime = true;
		forceRefresh = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {

		Log.d(TAG, "onCreateView: ");

		realView = inflater.inflate(R.layout.home_fragment, container, false);

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
	}

	// 异步更新UI
	private class MyAsyncTask extends AsyncTask<Void, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// 今天没有数据
			Date today = new Date();
			if (!initData(AppUsage.getDateInString(today))) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				initViewWithoutData();
			} else {
				initViewWithData();
			}
			setContentShown(true);
		}
	}

	public void setFirstTime(boolean flag) {
		firstTime = flag;
	}

	public void setForceRefresh(boolean flag) {
		forceRefresh = flag;
	}

	private void  initViewWithoutData() {
		RelativeLayout viewWithoutData = (RelativeLayout)realView.findViewById(R.id.home_no_data);
		LinearLayout viewWithDate = (LinearLayout)realView.findViewById(R.id.home_with_data);

		viewWithDate.setVisibility(View.GONE);
		viewWithoutData.setVisibility(View.VISIBLE);

		Button goToSetting = (Button)realView.findViewById(R.id.go_to_setting);
		goToSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mainActivity.getViewPager().setCurrentItem(3);
			}
		});
	}

	private void initViewWithData() {
		RelativeLayout viewWithoutData = (RelativeLayout)realView.findViewById(R.id.home_no_data);
		LinearLayout viewWithDate = (LinearLayout)realView.findViewById(R.id.home_with_data);

		viewWithDate.setVisibility(View.VISIBLE);
		viewWithoutData.setVisibility(View.GONE);

		initPieChart(realView);
	}

	// init pie chart with today's value
	private void initPieChart(View view) {
		mChart = (PieChart) view.findViewById(R.id.home_pie_chart);

		mChart.setUsePercentValues(true);
		mChart.setDescription("");
		mChart.setExtraOffsets(5, 10, 5, 5);
		mChart.setDragDecelerationFrictionCoef(0.95f);
		// 中间有洞
		mChart.setDrawHoleEnabled(true);
		mChart.setHoleColorTransparent(true);
		mChart.setTransparentCircleColor(Color.WHITE);
		// 选中块突出大小（百分比）
		mChart.setTransparentCircleAlpha(110);
		mChart.setHoleRadius(58f);
		mChart.setTransparentCircleRadius(45f);
		// 显示中间文字
		mChart.setDrawCenterText(true);
		mChart.setCenterText("点击图表查看");
		// 中间文字颜色
		mChart.setCenterTextColor(Color.parseColor("#22DDB8"));
		// 中间文字大小
		mChart.setCenterTextSize(22f);

		mChart.setRotationAngle(0);
		mChart.setRotationEnabled(true);
		mChart.setHighlightPerTapEnabled(true);
		// 不显示文字
		mChart.setDrawSliceText(false);

		mChart.setOnChartValueSelectedListener(this);

		// set data
		mChart.setData(data);
		mChart.highlightValues(null);
		mChart.invalidate();

		new Handler(mainActivity.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
			}
		}, 80);

	}

	// init data
	private boolean initData(String date) {
		list = getCertainDayData(date);

		// 没有数据，直接返回
		if (list.isEmpty()) return false;

		ArrayList<Entry> values = new ArrayList<>();
		ArrayList<String> names = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			Entry entry = new Entry(list.get(i).getTotalTimeInSecond(), i);
			values.add(entry);
			names.add(list.get(i).getAppName());
		}

		PieDataSet dataSet = new PieDataSet(values, "");
		dataSet.setSliceSpace(2f);
		dataSet.setSelectionShift(14f);

		// 不显示文字
		dataSet.setDrawValues(false);
		dataSet.setColors(getColorSet());

		data = new PieData(names, dataSet);
		data.setValueFormatter(new PercentFormatter());

		return true;
	}

	// get color set
	private ArrayList<Integer> getColorSet() {
		ArrayList<Integer> colors = new ArrayList<>();
//        final int[] selfColor = {
//                Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
//                Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)
//        };

		for (int c : ColorTemplate.VORDIPLOM_COLORS)
			colors.add(c);
		for (int c : ColorTemplate.JOYFUL_COLORS)
			colors.add(c);
		for (int c : ColorTemplate.COLORFUL_COLORS)
			colors.add(c);
		for (int c : ColorTemplate.LIBERTY_COLORS)
			colors.add(c);
		for (int c : ColorTemplate.PASTEL_COLORS)
			colors.add(c);

		colors.add(ColorTemplate.getHoloBlue());

		return colors;
	}

	// get certain day's data from file
	private List<AppChartItem> getCertainDayData(String date) {
		List<AppChartItem> list = new ArrayList<>();

		// read file
		FileUtils fileUtils = new FileUtils(mainActivity);
		// if the app has been used in the certain date
		for (AppUsage app: fileUtils.getAllStoredApp()) {
			Map<String, AppUsage.History> history = app.getUsingHistory();
			if (history.containsKey(date)) {
				AppUsage.History theDay = history.get(date);

				AppChartItem item = new AppChartItem(app.getRealName(),
					theDay.getTotalTime(), theDay.getUsedCount());

				list.add(item);
			}
		}

		return list;
	}

	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		if (e == null)
			return;

		mChart.setCenterText(list.get(e.getXIndex()).getAppName()+"\n"
			+list.get(e.getXIndex()).getTotalTimeInString());
	}

	@Override
	public void onNothingSelected() {
		mChart.setCenterText("点击图表查看");
	}

}
