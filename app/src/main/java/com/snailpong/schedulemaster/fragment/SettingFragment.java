package com.snailpong.schedulemaster.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.snailpong.schedulemaster.LoginActivity;
import com.snailpong.schedulemaster.MapActivity;
import com.snailpong.schedulemaster.R;
import com.snailpong.schedulemaster.SyncActivity;


public class SettingFragment extends Fragment {
    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        // 테스트 버튼
        LinearLayout testTableClickedBtn = (LinearLayout) view.findViewById(R.id.setting_alarmsound);
        testTableClickedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle args = new Bundle();
//                args.putString("key", "value");
//                TableClickedDialog dialog = new TableClickedDialog();
//                dialog.setArguments(args); // 데이터 전달
//                dialog.show(getActivity().getSupportFragmentManager(),"tag");
                FirebaseAuth.getInstance().signOut();
            }
        });
        // 구글 계정 연동 로그인 버튼
        LinearLayout loginBtn = (LinearLayout) view.findViewById(R.id.setting_google);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, 3000);
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
        return view;
    }
}