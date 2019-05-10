package com.snailpong.schedulemaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
                CalendarAddDialog dialog = new CalendarAddDialog();
                dialog.setArguments(args); // 데이터 전달
                dialog.show(getActivity().getSupportFragmentManager(),"tag");
            }
        });

        initData();
        initListener();
        return view;
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
        ContentValues values = new ContentValues();
        values.put("name", "신호및시스템");
        values.put("day", 5);
        values.put("starttime", "10:30");
        values.put("endtime", "11:45");
        db.insert("weekly", null, values);

        values = new ContentValues();
        values.put("name", "운영체제");
        values.put("day", 5);
        values.put("starttime", "13:30");
        values.put("endtime", "14:45");
        db.insert("weekly", null, values);

        values = new ContentValues();
        values.put("name", "컴퓨터구조");
        values.put("day", 5);
        values.put("starttime", "15:00");
        values.put("endtime", "16:15");
        db.insert("weekly", null, values);

        values = new ContentValues();
        values.put("name", "제어공학(I)");
        values.put("day", 10);
        values.put("starttime", "10:30");
        values.put("endtime", "11:45");
        db.insert("weekly", null, values);

        values = new ContentValues();
        values.put("name", "전기기기(I)");
        values.put("day", 10);
        values.put("starttime", "13:30");
        values.put("endtime", "14:45");
        db.insert("weekly", null, values);

        values = new ContentValues();
        values.put("name", "마이크로프로세서응용");
        values.put("day", 10);
        values.put("starttime", "15:00");
        values.put("endtime", "16:15");
        db.insert("weekly", null, values);


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
                if(week % 2 == 1) valuelist.get(i).add(new TimeData(id, name, mColors.get(id % 7), getMillis("2017-11-10 "+ starttime +":00"), getMillis("2017-11-10 "+ endtime + ":00")));
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
        //initShortSamples();
        initLongSamples();
        timeTable.setTimeTable(getMillis("2017-11-10 00:00:00"), mShortSamples);
    }

    private void initShortSamples() {
        mShortSamples = new ArrayList<>();
        ArrayList<TimeData> values = new ArrayList<>();
        values.add(new TimeData(0, "신호및시스템", R.color.color_table_1_light, getMillis("2017-11-10 10:30:00"), getMillis("2017-11-10 11:45:00")));
        values.add(new TimeData(1, "운영체제", R.color.color_table_2_light, getMillis("2017-11-10 13:30:00"), getMillis("2017-11-10 14:45:00")));
        values.add(new TimeData(2, "컴퓨터구조", R.color.color_table_3_light, getMillis("2017-11-10 15:00:00"), getMillis("2017-11-10 16:15:00")));

        ArrayList<TimeData> values2 = new ArrayList<>();
        values2.add(new TimeData(3, "제어공학(I)", R.color.color_table_4_light, getMillis("2017-11-10 10:30:00"), getMillis("2017-11-10 11:45:00")));
        values2.add(new TimeData(4, "전기기기(I)", R.color.color_table_5_light, getMillis("2017-11-10 13:30:00"), getMillis("2017-11-10 14:45:00")));
        values2.add(new TimeData(5, "마이크로프로세서응용", R.color.color_table_6_light, getMillis("2017-11-10 15:00:00"), getMillis("2017-11-10 16:15:00")));

        ArrayList<TimeTableData> tables = new ArrayList<>();
        tables.add(new TimeTableData("월", values));
        tables.add(new TimeTableData("화", values2));
        tables.add(new TimeTableData("수", values));
        tables.add(new TimeTableData("목", values2));
        tables.add(new TimeTableData("금", values));
        //tables.add(new TimeTableData("토", new ArrayList<TimeData>()));
        //tables.add(new TimeTableData("일", new ArrayList<TimeData>()));

        mShortSamples.addAll(tables);
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
