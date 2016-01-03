package com.crossbow.app.x_timer.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.home.AppChartItem;
import com.crossbow.app.x_timer.day_detail.AppItem;
import com.crossbow.app.x_timer.day_detail.UsageItem;
import com.crossbow.app.x_timer.service.AppUsage;
import com.crossbow.app.x_timer.service.TickTrackerService;
import com.crossbow.app.x_timer.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

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
public class HomeFragment extends Fragment implements OnChartValueSelectedListener {
    private final String TAG = "HomeFragment";

    private PieChart mChart;

    private MainActivity mainActivity;

    private List<AppChartItem> list;

    private PieData data;

    public HomeFragment() { }

    @SuppressLint("ValidFragment")
    public HomeFragment(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 今天没有数据
        Date today = new Date();
        if (!initData(AppUsage.getDateInString(today))) {
            View view = inflater.inflate(R.layout.home_fragment_no_data, container, false);

            return view;
        }

        View view = inflater.inflate(R.layout.home_fragment, container, false);

        initPieChart(view);

        //initButton(view);

        return view;
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
        mChart.setTransparentCircleAlpha(110);
        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);
        // 显示中间文字
        mChart.setDrawCenterText(true);
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

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    // init data
    private boolean initData(String date) {
        if (mainActivity.isWorking()) manuallySaveData();

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

    // save data manually
    private void manuallySaveData() {
        TickTrackerService.UsageBinder usageBinder = MainActivity.usageBinder;
        usageBinder.manuallySaveData();
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

        System.out.println(dataSetIndex);

        mChart.setCenterText(list.get(e.getXIndex()).getAppName()+"\n"
                +list.get(e.getXIndex()).getTotalTimeInString());
    }

    @Override
    public void onNothingSelected() {
        mChart.setCenterText("点击图表查看");
    }

}
