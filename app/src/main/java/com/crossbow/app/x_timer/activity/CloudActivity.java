package com.crossbow.app.x_timer.activity;

/**
 * Created by kinsang on 16-1-8.
 */
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.crossbow.app.x_timer.R;
import com.crossbow.app.x_timer.adapter.SignPagerAdapter;

public class CloudActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);

        setTabLayout();
        initToolbar();
        initStatusBar();
    }

    // handle toolbar
    private void initToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.cloud_toolbar);
        setSupportActionBar(toolbar);

        // back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    private void setTabLayout() {
        viewPager = (ViewPager)findViewById(R.id.sign_view_pager);
        viewPager.setAdapter(new SignPagerAdapter(getSupportFragmentManager(),
                CloudActivity.this));

        tabLayout = (TabLayout)findViewById(R.id.sign_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }
}