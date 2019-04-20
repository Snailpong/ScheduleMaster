package com.snailpong.schedulemaster;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class TabbedActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private CalendarFragment menu1Fragment = new CalendarFragment();
    private AlarmFragment menu2Fragment = new AlarmFragment();
    private MypageFragment menu3Fragment = new MypageFragment();
    private SettingFragment menu4Fragment = new SettingFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        // 첫 화면 지정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, menu1Fragment).commitAllowingStateLoss();

        // bottomNavigationView의 아이템이 선택될 때 호출될 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.action_schedule: {
                        transaction.replace(R.id.frame_layout, menu1Fragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.action_alarm: {
                        transaction.replace(R.id.frame_layout, menu2Fragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.action_mypage: {
                        transaction.replace(R.id.frame_layout, menu3Fragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.action_setting: {
                        transaction.replace(R.id.frame_layout, menu4Fragment).commitAllowingStateLoss();
                        break;
                    }
                }

                return true;
            }
        });

    }

}
