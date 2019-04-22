package com.snailpong.schedulemaster;

import android.content.Context;
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
    private List<String> mShortHeaders = Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu","Fri", "Sat");

    private long mNow = 0;

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
                Log.d("a","dddd");
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
        timeTable.setStartHour(9);
        timeTable.setShowHeader(true);
        timeTable.setTableMode(TimeTableView.TableMode.SHORT);

        DateTime now = DateTime.now();
        mNow = now.withTimeAtStartOfDay().getMillis();
        initShortSamples();
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
        //tables.add(new TimeTableData("일", new ArrayList<TimeData>()));
        tables.add(new TimeTableData("월", values));
        tables.add(new TimeTableData("화", values2));
        tables.add(new TimeTableData("수", values));
        tables.add(new TimeTableData("목", values2));
        tables.add(new TimeTableData("금", values));
        //tables.add(new TimeTableData("토", new ArrayList<TimeData>()));

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
