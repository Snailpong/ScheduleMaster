package com.snailpong.schedulemaster;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.eunsiljo.timetablelib.data.TimeData;
import com.github.eunsiljo.timetablelib.data.TimeTableData;
import com.github.eunsiljo.timetablelib.view.TimeTableView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.snailpong.schedulemaster.fragment.CalendarFragment.getMillis;

public class FriendScheduleActivity extends AppCompatActivity {

    private TimeTableView timeTable;
    private ArrayList<TimeTableData> mShortSamples = new ArrayList<>();
    private List<String> mShortHeaders = Arrays.asList("월", "화", "수", "목", "금", "토", "일");
    private List<Integer> mColors = Arrays.asList(R.color.color_table_1_light, R.color.color_table_2_light, R.color.color_table_3_light,
            R.color.color_table_4_light, R.color.color_table_5_light, R.color.color_table_6_light, R.color.color_table_7_light);
    String uid;
    DatabaseReference mDatabase;
    ArrayList<TimeTableData> tables = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_schedule);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        setTitle(intent.getStringExtra("name")+"의 시간표");
        timeTable = (TimeTableView) findViewById(R.id.timeTable2);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("weekly");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<ArrayList<TimeData>> valuelist = new ArrayList<ArrayList<TimeData>>();
                for(int i=0; i!=7; ++i) valuelist.add(new ArrayList<TimeData>());

                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    int id = d.child("id").getValue(Integer.class);
                    String name = d.child("name").getValue(String.class);
                    int day = d.child("day").getValue(Integer.class);
                    String starttime = d.child("starttime").getValue(String.class);
                    String endtime = d.child("endtime").getValue(String.class);

                    int week = day;
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

                timeTable.setTimeTable(getMillis("2017-11-10 00:00:00"), mShortSamples);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });



    }
}
