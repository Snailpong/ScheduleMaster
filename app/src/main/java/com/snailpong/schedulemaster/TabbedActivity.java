package com.snailpong.schedulemaster;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.snailpong.schedulemaster.fragment.AddFriendFragment;
import com.snailpong.schedulemaster.fragment.AlarmFragment;
import com.snailpong.schedulemaster.fragment.CalendarFragment;
import com.snailpong.schedulemaster.fragment.FriendFragment;
import com.snailpong.schedulemaster.fragment.MypageFragment;
import com.snailpong.schedulemaster.fragment.SettingFragment;

public class TabbedActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private CalendarFragment menu1Fragment = new CalendarFragment();
    private Fragment menu2Fragment = new AlarmFragment();
    private MypageFragment menu3Fragment;
    private SettingFragment menu4Fragment = new SettingFragment();
    private FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        // 첫 화면 지정
        transaction = fragmentManager.beginTransaction();
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
                        menu3Fragment = new MypageFragment();
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

    public void onFragmentChange(int index) {
        Fragment fragment;
        if (index == 0) {
            fragment = new FriendFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commitAllowingStateLoss();
        }
        else if (index == 1) {
            fragment = new AddFriendFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commitAllowingStateLoss();
        }
    }
}
