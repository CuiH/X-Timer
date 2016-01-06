package com.crossbow.app.x_timer.fragment.history;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.detail.app_detail.AppDetailActivity;
import com.crossbow.app.x_timer.detail.day_detail.DayDetailActivity;
import com.crossbow.app.x_timer.service.AppUsage;
import com.crossbow.app.x_timer.spinner.NiceSpinner;
import com.crossbow.app.x_timer.utils.FileUtils;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by CuiH on 2015/12/29.
 */
public class HistoryFragment extends Fragment {

    private MainActivity mainActivity;

    private CalendarPickerView calendar;

    private RelativeLayout calendarLayout;
    private LinearLayout appLayout;

    private int nowLayout;   // 0-calendar 1-list

    private HistoryAppListAdapter adapter;
    private List<HistoryAppInfo> allStoredApp;

    private int selectedIndex;


    public HistoryFragment() { }

    @SuppressLint("ValidFragment")
    public HistoryFragment(MainActivity activity) {
        mainActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);

        initCalendar(view);
        initCalendarButton(view);
        initAppList(view);
        initDefault();
        initSpinner(view);
        initOtherButton(view);

        return view;
    }

    public void initSpinner(View view) {
        NiceSpinner niceSpinner = (NiceSpinner) view.findViewById(R.id.history_spinner);
        List<String> dataSet = new LinkedList<>(Arrays.asList("按时间查询", "按应用查询"));
        niceSpinner.attachDataSource(dataSet);
        niceSpinner.setSelectedIndex(0);

        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    calendarLayout.setVisibility(View.VISIBLE);
                    appLayout.setVisibility(View.GONE);

                    nowLayout = 0;

                } else {
                    calendarLayout.setVisibility(View.GONE);
                    appLayout.setVisibility(View.VISIBLE);

                    nowLayout = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // init the calendar to this month
    private void initCalendar(View view) {
        calendarLayout = (RelativeLayout) view.findViewById(R.id.history_calendar_layout);

        Date today = new Date();
        Date firstDayOfTheMonth = getTheFirstDayOfTheMonth(today);
        Date firstDayOfTheNextMonth = getTheFirstDayOfTheNextMonth(today);

        calendar = (CalendarPickerView) view.findViewById(R.id.calendar_view);
        calendar.init(firstDayOfTheMonth, firstDayOfTheNextMonth).withSelectedDate(today);
    }

    // add listener to calendar buttons
    private void initCalendarButton(View view) {
        Button nextMonth = (Button)view.findViewById(R.id.calendar_next_month);
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = calendar.getSelectedDate();
                Date firstDayOfTheNextMonth = getTheFirstDayOfTheNextMonth(now);
                Date firstDayOfTheNextNextMonth = getTheFirstDayOfTheNextMonth(firstDayOfTheNextMonth);

                calendar.init(firstDayOfTheNextMonth, firstDayOfTheNextNextMonth).withSelectedDate(firstDayOfTheNextMonth);
            }
        });

        Button lastMonth = (Button)view.findViewById(R.id.calendar_last_month);
        lastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = calendar.getSelectedDate();
                Date firstDayOfTheLastMonth = getTheFirstDayOfTheLastMonth(now);
                Date firstDatOfTheMonth = getTheFirstDayOfTheMonth(now);

                calendar.init(firstDayOfTheLastMonth, firstDatOfTheMonth).withSelectedDate(firstDayOfTheLastMonth);
            }
        });

        Button nextYear = (Button)view.findViewById(R.id.calendar_next_year);
        nextYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mainActivity, "未实现", Toast.LENGTH_SHORT).show();
            }
        });

        Button lastYear = (Button)view.findViewById(R.id.calendar_last_year);
        lastYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mainActivity, "未实现", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // init the app list view
    private void initAppList(View view) {
        selectedIndex = -1;

        appLayout = (LinearLayout)view.findViewById(R.id.history_app_layout);

        ListView listView = (ListView)view.findViewById(R.id.history_app_list);

        allStoredApp = getAllStoredApps();
        adapter = new HistoryAppListAdapter(mainActivity,
                R.layout.history_app_item, allStoredApp);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HistoryAppInfo nowApp = allStoredApp.get(position);
                if (!nowApp.getSelected()) {
                    nowApp.setSelected(true);
                    if (selectedIndex != -1) {
                        HistoryAppInfo lastApp = allStoredApp.get(selectedIndex);
                        lastApp.setSelected(false);
                    }
                    selectedIndex = position;
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    // init the default layout
    private void initDefault() {
        nowLayout = 0;

        calendarLayout.setVisibility(View.VISIBLE);
        appLayout.setVisibility(View.GONE);
    }

    // add listener to other buttons
    private void initOtherButton(View view) {
        Button ok = (Button)view.findViewById(R.id.history_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nowLayout == 0) {
                    Intent intent = new Intent(mainActivity, DayDetailActivity.class);

                    Date target = calendar.getSelectedDate();
                    intent.putExtra("date", AppUsage.getDateInString(target));

                    startActivity(intent);
                } else {
                    Intent intent = new Intent(mainActivity, AppDetailActivity.class);

                    if (selectedIndex != -1) {
                        HistoryAppInfo selectedApp = allStoredApp.get(selectedIndex);
                        intent.putExtra("pkgName", selectedApp.getPkgName());
                        intent.putExtra("realName", selectedApp.getRealName());

                        startActivity(intent);
                    }
                }

            }
        });
    }

    // get all apps that have been stored
    private List<HistoryAppInfo> getAllStoredApps() {
        List<HistoryAppInfo> list = new ArrayList<>();

        FileUtils fileUtils = new FileUtils(mainActivity);
        for (AppUsage appUsage: fileUtils.getAllStoredApp()) {
            HistoryAppInfo info = new HistoryAppInfo(appUsage.getRealName(),
                    appUsage.getPackageName());
            list.add(info);
        }

        return list;
    }

    // as the name of the method
    private Date getTheFirstDayOfTheMonth(Date day) {
        Calendar tem = Calendar.getInstance();
        // 当前日期
        tem.setTime(day);
        // 设置当前日期为当前月的第一天
        tem.set(Calendar.DATE, tem.getActualMinimum(Calendar.DAY_OF_MONTH));

        return tem.getTime();
    }

    // as the name of the method
    private Date getTheFirstDayOfTheLastMonth(Date day) {
        Calendar tem = Calendar.getInstance();
        // 当前日期
        tem.setTime(day);
        // 设置当前日期为当前月的第一天
        tem.set(Calendar.DATE, tem.getActualMinimum(Calendar.DAY_OF_MONTH));
        // 上个月的第一天
        tem.add(Calendar.MONTH, -1);

        return tem.getTime();
    }

    // as the name of the method
    private Date getTheFirstDayOfTheNextMonth(Date day) {
        Calendar tem = Calendar.getInstance();
        // 当前日期
        tem.setTime(day);
        // 设置当前日期为当前月的第一天
        tem.set(Calendar.DATE, tem.getActualMinimum(Calendar.DAY_OF_MONTH));
        // 下个月的第一天
        tem.add(Calendar.MONTH, 1);

        return tem.getTime();
    }
}
