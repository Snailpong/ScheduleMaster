package com.snailpong.schedulemaster;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddDeadlineActivity extends AppCompatActivity {

    private int year, month, day, hour, min;
    private LinearLayout timelayout;
    private LinearLayout alarmlayout;
    private LinearLayout addBtn;
    private LinearLayout cancelBtn;
    private TextView time;
    private TextView alarm;
    private EditText title;
    private StringBuilder stringBuilder;
    private Calendar calendar = Calendar.getInstance();
    private String date;
    private String timeString;
    private int selectedAlarmItem = 0;
    private String[] alarmTimeItems = new String[]{"1시간", "3시간", "6시간", "12시간", "하루"};
    private int[] selectedAlarmTime = new int[]{1, 3, 6, 12, 24};
    private DBHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deadline);
        setTitle("마감 추가");
        timelayout = (LinearLayout) findViewById(R.id.deadline_timelayout);
        alarmlayout = (LinearLayout) findViewById(R.id.deadline_alarmlayout);
        time = (TextView) findViewById(R.id.deadline_time);
        alarm = (TextView) findViewById(R.id.deadline_alarm);
        title = (EditText) findViewById(R.id.deadline_title);
        addBtn = (LinearLayout) findViewById(R.id.deadline_add);
        cancelBtn = (LinearLayout) findViewById(R.id.deadline_cancel);
        helper = new DBHelper(AddDeadlineActivity.this, "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        timelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddDeadlineActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        alarmlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(AddDeadlineActivity.this);
                dialog.setTitle("알람 시간을 선택하세요.")
                        .setSingleChoiceItems(alarmTimeItems,
                                0,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedAlarmItem = which;
                                    }
                                })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alarm.setText(alarmTimeItems[selectedAlarmItem] + " 전");
                            }
                        }).create().show();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put("name", title.getText().toString());
                values.put("whatid", getIntent().getIntExtra("id", 0));
                values.put("day", date);
                values.put("endtime", timeString);
                values.put("prev", selectedAlarmTime[selectedAlarmItem]);
                db.insert("deadline", null, values);
                finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {
            year = _year;
            month = _month;
            day = _day;
            date = String.format("%d%02d%d", year, month, day);
            stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("%d월 %d일 (%s) ", month, day, getDateDay(date, "yyyyMMdd")));
            new TimePickerDialog(AddDeadlineActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int _hour, int _min) {
            hour = _hour;
            min = _min;
            timeString = String.format("%02d:%02d", hour, min);
            stringBuilder.append(String.format("%d시 %d분", hour, min));
            time.setText(stringBuilder.toString());
        }
    };

    public static String getDateDay(String date, String dateType){

        String day = new String();
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType);
        Date nDate = null;
        try {
            nDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);
        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;
        }
        return day;
    }
}

