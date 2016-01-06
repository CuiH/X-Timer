package com.crossbow.app.x_timer.fragment.history;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.crossbow.app.x_timer.MainActivity;
import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.detail.day_detail.DayDetailActivity;
import com.crossbow.app.x_timer.service.AppUsage;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by CuiH on 2015/12/29.
 */
public class HistoryFragment extends Fragment {

    MainActivity mainActivity;

    CalendarPickerView calendar;

    ProgressDialog dialog;

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
        initOtherButton(view);

        return view;
    }

    // init the calendar to this month
    private void initCalendar(View view) {
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

    // add listener to other buttons
    private void initOtherButton(View view) {
        Button ok = (Button)view.findViewById(R.id.calendar_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, DayDetailActivity.class);

                Date target = calendar.getSelectedDate();
                intent.putExtra("date", AppUsage.getDateInString(target));

                startActivity(intent);
            }
        });
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
