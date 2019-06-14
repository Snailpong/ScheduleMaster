package com.snailpong.schedulemaster.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snailpong.schedulemaster.AlarmAdapter;
import com.snailpong.schedulemaster.AlarmClass;
import com.snailpong.schedulemaster.DBHelper;
import com.snailpong.schedulemaster.R;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmFragment extends Fragment {
    // DB에 저장된 알람 list들을 가져오기
    private ArrayList<AlarmClass> list;
    private String[] category_name = {"", "마감 알림", "휴일 알림", "일정 알림"};
    private DBHelper helper;
    private SQLiteDatabase db;
    private String subject_name;
    private String timeString;
    private String content;
    private Calendar calendar = Calendar.getInstance();
    private int category;

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

        helper = new DBHelper(getActivity(), "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        list = new ArrayList<AlarmClass>();
        // 마감 알림 관련 처리
        Cursor c = db.query("alarmset", null, null, null, null, null, null);
        c.moveToFirst();
        category = 1;
        /*
        while(deadline_c.moveToNext()) {
            int id = deadline_c.getInt(deadline_c.getColumnIndex("_id"));
            String name = deadline_c.getString(deadline_c.getColumnIndex("name"));
            int whatid = deadline_c.getInt(deadline_c.getColumnIndex("whatid"));
            int year = deadline_c.getInt(deadline_c.getColumnIndex("year"));
            int month = deadline_c.getInt(deadline_c.getColumnIndex("month"));
            int day = deadline_c.getInt(deadline_c.getColumnIndex("day"));
            int hour = deadline_c.getInt(deadline_c.getColumnIndex("hour"));
            int min = deadline_c.getInt(deadline_c.getColumnIndex("min"));
            int prev = deadline_c.getInt(deadline_c.getColumnIndex("prev"));
            long time;

            calendar.set(year, month, day, hour, min, 0);
            calendar.add(calendar.HOUR_OF_DAY, -prev);
            String title = category_name[category];

            findSubjectName(whatid);

            timeString = String.format("%02d:%02d", hour, min);
            content = year + "년 " + (month + 1) + "월 " + day + "일 " + timeString
                    + " " + subject_name + " " + name + " 마감입니다.";

            time = calendar.getTimeInMillis();

            list.add(new AlarmClass(id, category, title, content, time));
        }

        // 휴일 알림 관련 처리
        Cursor noclass_c = db.query("noclass", null, null, null, null, null, null);
        //noclass_c.moveToFirst();
        category = 2;

        while(noclass_c.moveToNext()) {
            int id = noclass_c.getInt(deadline_c.getColumnIndex("_id"));
            int whatid = noclass_c.getInt(deadline_c.getColumnIndex("whatid"));
            int year = noclass_c.getInt(deadline_c.getColumnIndex("year"));
            int month = noclass_c.getInt(deadline_c.getColumnIndex("month"));
            int day = noclass_c.getInt(deadline_c.getColumnIndex("day"));
            long time;

            String title = category_name[category];
            findSubjectName(whatid);

            content = year + "년 " + (month + 1) + "월 " + day + "일 " + timeString
                    + " " + subject_name + " " + " 휴강입니다.";

            calendar.set(year, month, day, 0, 0, 0);
            time = calendar.getTimeInMillis();

            list.add(new AlarmClass(id, category, title, content, time));
        }*/
        // for Test;
        /*
        list.add(new AlarmClass(1,1,"마감 알림", "4월 23일 23:59 컴퓨터알고리즘 HW#9 마감입니다.", 1490958393191L));
        list.add(new AlarmClass(2,2,"휴일 알림", "4월 25일 운영체제 휴강입니다.", 1490358393191L));
        list.add(new AlarmClass(3,3,"일정 알림", "4월 26일 17:00 꼬부기 약속입니다.", 1490158393191L));
        */

        // 일정 알림 관련 처리
        /*
        Cursor daily_c = db.query("daily", null, null, null, null, null, null);
        daily_c.moveToFirst();
        category = 3;
        while(daily_c.moveToNext()) {
            int year, month, day;
            String name = deadline_c.getString(daily_c.getColumnIndex("name"));
            String days = deadline_c.getString(daily_c.getColumnIndex("days"));
            int starttime = deadline_c.getInt(daily_c.getColumnIndex("starttime"));
            int endtime = deadline_c.getInt(daily_c.getColumnIndex("endtime"));
            long time;

            String title = category_name[category];

            list.add(new AlarmClass(id, category, title, content, time));
        }
        */

        AlarmAdapter adapter = new AlarmAdapter(list) ;
        recyclerView.setAdapter(adapter) ;
        return view;
    }

    public void findSubjectName (int id) {
        // id를 사용해 과목명 불러오기 (함수처리)
        Cursor c = db.query("weekly", null
                , "_id=" + String.valueOf(id), null,
                null, null, null, null);
        c.moveToFirst();

        subject_name = c.getString(c.getColumnIndex("name"));
    }

}