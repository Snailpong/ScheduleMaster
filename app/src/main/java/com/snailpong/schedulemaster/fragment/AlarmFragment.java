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
// ok
public class AlarmFragment extends Fragment {
    // DB에 저장된 알람 list들을 가져오기(state에서 마감, 휴일만)
    private ArrayList<AlarmClass> list;
    private String[] category_name = {"", "마감 알림", "휴일 알림"};
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

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int whatid, hour, min, prev;
        long timebefore;
        /* state text, name text, whatid integer, hour integer, min integer, timebefore text */
        list = new ArrayList<AlarmClass>();
        // 알람 관련 처리
        Cursor c = db.rawQuery("SELECT * FROM alarmset WHERE state='" + "deadline" + "' OR state='" + "noclass" + "';", null);
        c.moveToFirst();
        while(c.moveToNext()) {
            String state = c.getString(c.getColumnIndex("state"));
            whatid = c.getInt(c.getColumnIndex("whatid"));
            hour = c.getInt(c.getColumnIndex("hour"));
            min = c.getInt(c.getColumnIndex("min"));

            // id를 사용해 과목명 불러오기
            Cursor cursor = db.query("weekly", null
                    , "_id="+String.valueOf(whatid), null,
                    null, null, null, null);
            cursor.moveToFirst();
            // 과목명
            subject_name = c.getString(c.getColumnIndex("name"));
            timeString = String.format("%02d:%02d", hour, min);
            // 마감처리
            if (state == "deadline") {
                category = 1;
                // 알람 날짜와 마감 날짜가 다를 수도 있으므로
                year = c.getInt(c.getColumnIndex("year"));
                month = c.getInt(c.getColumnIndex("month"));
                day = c.getInt(c.getColumnIndex("day"));
                prev = c.getInt(c.getColumnIndex("prev"));

                calendar.set(year, month, day, hour, min, 0);
                calendar.add(calendar.HOUR_OF_DAY, -prev);
                timebefore = calendar.getTimeInMillis();

                content = year + "년 " + (month + 1) + "월 " + day + "일 " + timeString
                        + " " + subject_name + " " + c.getInt(c.getColumnIndex("name"))
                        + " " + "마감입니다.";
            }
            // 휴강처리
            else {
                category = 2;
                calendar.set(year, month, day, 0, 0);
                timebefore = calendar.getTimeInMillis();
                content = year + "년 " + (month + 1) + "월 " + day + "일 " + timeString
                        + " " + subject_name + " " + " 휴강입니다.";
            }

            list.add(new AlarmClass(whatid, category, category_name[category], content, timebefore));
        }

        AlarmAdapter adapter = new AlarmAdapter(list) ;
        recyclerView.setAdapter(adapter);
        db.close();
        return view;
    }
}