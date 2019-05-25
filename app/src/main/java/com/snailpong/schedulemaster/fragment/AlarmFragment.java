package com.snailpong.schedulemaster.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snailpong.schedulemaster.AlarmAdapter;
import com.snailpong.schedulemaster.AlarmClass;
import com.snailpong.schedulemaster.R;

import java.util.ArrayList;

public class AlarmFragment extends Fragment {

    ArrayList<AlarmClass> list;

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_alarm_list) ;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        list = new ArrayList<AlarmClass>();
        // for Test;
        list.add(new AlarmClass(1,1,"마감 알림", "4월 23일 23:59 컴퓨터알고리즘 HW#9 마감입니다.", 1490958393191L));
        list.add(new AlarmClass(2,2,"휴일 알림", "4월 25일 운영체제 휴강입니다.", 1490358393191L));
        list.add(new AlarmClass(3,3,"일정 알림", "4월 26일 17:00 꼬부기 약속입니다.", 1490158393191L));

        AlarmAdapter adapter = new AlarmAdapter(list) ;
        recyclerView.setAdapter(adapter) ;
        return view;
    }

}
