package com.snailpong.schedulemaster;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.eunsiljo.timetablelib.data.TimeData;
import com.github.eunsiljo.timetablelib.data.TimeTableData;
import com.github.eunsiljo.timetablelib.view.TimeTableView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalendarFragment extends Fragment {

    private TimeTableView timeTable;

    private ArrayList<TimeTableData> mShortSamples = new ArrayList<>();
    private ArrayList<TimeTableData> mLongSamples = new ArrayList<>();

    private FloatingActionButton fab;
    private boolean select = false;

    private List<String> mTitles = Arrays.asList("A","B","C","D","E","F","G");
    private List<String> mLongHeaders = Arrays.asList("Plan", "Do");
    private List<String> mShortHeaders = Arrays.asList("월", "화", "수", "목", "금", "토", "일");
    private List<Integer> mColors = Arrays.asList(R.color.color_table_1_light, R.color.color_table_2_light, R.color.color_table_3_light,
            R.color.color_table_4_light, R.color.color_table_5_light, R.color.color_table_6_light, R.color.color_table_7_light);

    private long mNow = 0;

    DBHelper helper;
    SQLiteDatabase db;

    public CalendarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        timeTable = (TimeTableView) view.findViewById(R.id.timeTable);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        Button addBtn = (Button) view.findViewById(R.id.calender_button);

        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("key", "value");
                CalendarRegularAddActivity dialog = new CalendarRegularAddActivity();
                Intent intent = new Intent(getActivity(), CalendarRegularAddActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        initData();
        initListener();
        super.onResume();
    }

    private void initListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select = !select;
                if(select) {
                    timeTable.setStartHour(0);
                    timeTable.setShowHeader(false);
                    timeTable.setTableMode(TimeTableView.TableMode.LONG);
                    timeTable.setTimeTable(getMillis("2017-11-10 00:00:00"), mLongSamples);

                } else {
                    timeTable.setStartHour(9);
                    timeTable.setShowHeader(true);
                    timeTable.setTableMode(TimeTableView.TableMode.SHORT);
                    timeTable.setTimeTable(getMillis("2017-11-10 00:00:00"), mShortSamples);
                }
            }
        });
    }

    private void initData() {
        helper = new DBHelper(getActivity(), "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        /*
        helper.addRegular(db, "신호및시스템".toString(), 5, "10:30", "11:45");
        helper.addRegular(db, "운영체제".toString(), 5, "13:30", "14:45");
        helper.addRegular(db, "컴퓨터구조".toString(), 5, "15:00", "16:15");
        helper.addRegular(db, "제어공학(I)".toString(), 10, "10:30", "11:45");
        helper.addRegular(db, "전기기기(I)".toString(), 10, "13:30", "14:45");
        helper.addRegular(db, "마이크로프로세서응용".toString(), 10, "15:00", "16:15");
        helper.addRegular(db, "소프트웨어설계및실험".toString(), 2, "18:00", "22:00");
        helper.addRegular(db, "컴퓨터응용설계및실험".toString(), 4, "18:00", "22:00");
        helper.addRegular(db, "컴퓨터기초실험".toString(), 8, "18:00", "22:00");
        */

        mShortSamples = new ArrayList<>();
        Cursor c = db.query("weekly", null, null, null, null, null, null);

        ArrayList<ArrayList<TimeData>> valuelist = new ArrayList<ArrayList<TimeData>>();
        for(int i=0; i!=7; ++i) valuelist.add(new ArrayList<TimeData>());

        //Toast.makeText(getActivity(), String.valueOf(valuelist.size()), Toast.LENGTH_LONG).show();

        while(c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex("_id"));
            String name = c.getString(c.getColumnIndex("name"));
            int day = c.getInt(c.getColumnIndex("day"));
            String starttime = c.getString(c.getColumnIndex("starttime"));
            String endtime = c.getString(c.getColumnIndex("endtime"));

            int week = day;
            //Log.d("w",starttime);
            //Toast.makeText(getActivity(), String.valueOf(week), Toast.LENGTH_LONG).show();
            for(int i=0; i!=7; ++i) {
                if(week % 2 == 1) valuelist.get(i).add(new TimeData(id, name, mColors.get(id % 7),
                        getMillis("2017-11-10 "+ starttime +":00"), getMillis("2017-11-10 "+ endtime + ":00")));
                week /= 2;
            }
        }

        ArrayList<TimeTableData> tables = new ArrayList<>();

        for(int i=0; i!=5; ++i) {
            tables.add(new TimeTableData(mShortHeaders.get(i), valuelist.get(i)));
        }
        if(valuelist.get(5).size() != 0 || valuelist.get(6).size() != 0) tables.add(new TimeTableData(mShortHeaders.get(5), valuelist.get(5)));
        if(valuelist.get(6).size() != 0) tables.add(new TimeTableData(mShortHeaders.get(6), valuelist.get(6)));

        mShortSamples.addAll(tables);

        timeTable.setStartHour(9);
        timeTable.setShowHeader(true);
        timeTable.setTableMode(TimeTableView.TableMode.SHORT);

        DateTime now = DateTime.now();
        mNow = now.withTimeAtStartOfDay().getMillis();
        initLongSamples();
        timeTable.setTimeTable(getMillis("2017-11-10 00:00:00"), mShortSamples);
    }

    private void initLongSamples() {
        mLongSamples = new ArrayList<>();
        ArrayList<TimeData> values = new ArrayList<>();
        values.add(new TimeData(0, "신호및시스템", R.color.color_table_1_light, getMillis("2017-11-10 10:30:00"), getMillis("2017-11-10 11:45:00")));
        values.add(new TimeData(1, "운영체제", R.color.color_table_2_light, getMillis("2017-11-10 13:30:00"), getMillis("2017-11-10 14:45:00")));
        values.add(new TimeData(2, "컴퓨터구조", R.color.color_table_3_light, getMillis("2017-11-10 15:00:00"), getMillis("2017-11-10 16:15:00")));
        values.add(new TimeData(3, "교수님 상담", R.color.color_table_4_light, getMillis("2017-11-10 17:00:00"), getMillis("2017-11-10 19:00:00")));
        mLongSamples.add(new TimeTableData("a", values));
    }

    private long getMillis(String day) {
        DateTime date = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(day);
        return date.getMillis();
    }

}
