package com.crossbow.app.x_timer.cloud;

/**
 * Created by kinsang on 16-1-8.
 */
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.crossbow.app.x_timer.R;

public class CloudActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);
        setTabLayout();
        initStatusBar();
    }

    private void initStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    private void setTabLayout() {
        viewPager = (ViewPager)findViewById(R.id.sign_view_pager);
        viewPager.setAdapter(new SignFragmentPagerAdapter(getSupportFragmentManager(),
                CloudActivity.this));

        tabLayout = (TabLayout)findViewById(R.id.sign_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }
}