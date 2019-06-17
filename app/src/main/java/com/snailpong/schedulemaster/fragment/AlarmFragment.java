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
/*
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int whatid, hour, min, prev;
        long timebefore;
*/
        list = new ArrayList<AlarmClass>();
        // 알람 관련 처리
        Cursor c = db.rawQuery("SELECT * FROM alarm", null);
        while(c.moveToNext()) {
            int category = c.getInt(c.getColumnIndex("category"));
            String title = c.getString(c.getColumnIndex("title"));
            String content = c.getString(c.getColumnIndex("content"));
            long time = c.getLong(c.getColumnIndex("time"));
            list.add(new AlarmClass(c.getInt(c.getColumnIndex("_id")), category, title, content, time));
        }

        AlarmAdapter adapter = new AlarmAdapter(list) ;
        recyclerView.setAdapter(adapter);
        db.close();
        return view;
    }
}