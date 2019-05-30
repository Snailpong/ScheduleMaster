package com.snailpong.schedulemaster.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.snailpong.schedulemaster.LoginActivity;
import com.snailpong.schedulemaster.MapActivity;
import com.snailpong.schedulemaster.R;
import com.snailpong.schedulemaster.SyncActivity;


public class SettingFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        final TextView goo = view.findViewById(R.id.setting_goo);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            goo.setText("로그아웃");
        }
        // 테스트 버튼
        LinearLayout testTableClickedBtn = (LinearLayout) view.findViewById(R.id.setting_alarmsound);
        testTableClickedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // 구글 계정 연동 로그인 버튼
        LinearLayout loginBtn = (LinearLayout) view.findViewById(R.id.setting_google);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    FirebaseAuth.getInstance().signOut();
                    goo.setText("계정 연동");
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }

            }
        });
        // Inflate the layout for this fragment

        LinearLayout sync = (LinearLayout) view.findViewById(R.id.setting_sync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SyncActivity.class);
                startActivity(intent);
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    goo.setText("로그아웃");
                }
            }
        };

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}