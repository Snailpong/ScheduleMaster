package com.snailpong.schedulemaster;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
// 마감 추가시 deadline DB에 넣고, 알람세팅
public class AddDeadlineActivity extends AppCompatActivity {
    private int year, month, day, hour, min;
    private LinearLayout timelayout;
    private LinearLayout alarmlayout;
    private LinearLayout addBtn;
    private LinearLayout cancelBtn;
    private TextView subject;
    private TextView time;
    private TextView alarm;
    private EditText title;
    private StringBuilder stringBuilder, initstring;
    private Calendar calendar;
    private String date, alarmDate;
    private String timeString, alarmTimeString;
    private int selectedAlarmItem = 0;
    private String[] alarmTimeItems = new String[]{"1시간", "3시간", "6시간", "12시간", "하루"};
    private int[] selectedAlarmTime = new int[]{1, 3, 6, 12, 24};
    private DBHelper helper;
    private SQLiteDatabase db;
    private int currentid;
    private AlarmManager alarm_manager;
    private Context context;
    private String text;
    private String subject_name, deadline_name;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deadline);
        setTitle("마감 추가");

        calendar = Calendar.getInstance();
        timelayout = (LinearLayout) findViewById(R.id.deadline_timelayout);
        alarmlayout = (LinearLayout) findViewById(R.id.deadline_alarmlayout);
        time = (TextView) findViewById(R.id.deadline_time);
        alarm = (TextView) findViewById(R.id.deadline_alarm);
        title = (EditText) findViewById(R.id.deadline_title);
        addBtn = (LinearLayout) findViewById(R.id.deadline_add);
        cancelBtn = (LinearLayout) findViewById(R.id.deadline_cancel);
        subject = (TextView) findViewById(R.id.deadline_subject);
        currentid = getIntent().getIntExtra("id", 0);

        helper = new DBHelper(AddDeadlineActivity.this, "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        // id를 사용해 과목명 불러오기
        Cursor c = db.query("weekly", null
                , "_id="+String.valueOf(currentid), null,
                null, null, null, null);
        c.moveToFirst();
        // 과목명
        subject_name = c.getString(c.getColumnIndex("name"));
        subject.setText(subject_name);

        this.context = this;
        // AlarmReceiver intent 설정
        final Intent my_intent = new Intent(this, NotificationReceiver.class);
        // 알람매니저 설정
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // 현재 시각으로 초기화
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        date = String.format("%d%02d%d", year, month, day);
        timeString = String.format("%02d:%02d", hour, min);

        initstring = new StringBuilder();
        // month : 0 ~ 11 범위를 가짐
        initstring.append(String.format("%d월 %d일 (%s) ", month+1, day, getDateDay(calendar, date, "yyyyMMdd")));
        initstring.append(String.format("%d시 %d분", hour, min));
        time.setText(initstring.toString());

        // 시간 text 클릭리스너
        timelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddDeadlineActivity.this, dateSetListener, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialog.show();
            }
        });
        // 알림 text 클릭리스너
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
        // 추가 버튼 클릭리스너
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.set(year, month, day, hour, min, 0);
                date = String.format("%d%02d%d", year, month, day);
                timeString = String.format("%02d:%02d", hour, min);
                deadline_name = title.getText().toString();
                // DB에 넣기
                ContentValues values = new ContentValues();

                if(deadline_name.length() > 1) {
                    values.put("name", deadline_name);
                }
                else {
                    values.put("name", "HW");
                }
                values.put("whatid", currentid);
                values.put("year", year);
                values.put("month", month);
                values.put("day", day);
                values.put("hour", hour);
                values.put("min", min);
                values.put("prev", selectedAlarmTime[selectedAlarmItem]);
                db.insert("deadline", null, values);

                // 알람 시간 세팅
                calendar.add(calendar.HOUR_OF_DAY, -selectedAlarmTime[selectedAlarmItem]);
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                min = calendar.get(Calendar.MINUTE);
                // month : 0 ~ 11
                alarmDate = String.format("%d%02d%d", year, month + 1, day);
                alarmTimeString = String.format("%02d:%02d", hour, min);
                text = (month + 1) + "월 " + day + "일 " + timeString + " " + subject_name + " " + deadline_name + " 마감입니다.";

                // receiver에 string 값 넘겨주기
                my_intent.putExtra("title", "마감 알림");
                my_intent.putExtra("text", text);
                /*
                my_intent.putExtra("year", year);
                my_intent.putExtra("month", month);
                my_intent.putExtra("day", day);
                my_intent.putExtra("hour", hour);
                my_intent.putExtra("min", min);
                my_intent.putExtra("whatid", currentid);
                */

                // pendingintent 식별을 위한 db 쿼리
                Cursor c = db.query("deadline", null
                        , "whatid=" + currentid, null,
                        null, null, null, null);
                // 알람 세팅, _id를 이용한 pendingIntent 식별
                pendingIntent = PendingIntent.getBroadcast(AddDeadlineActivity.this, 1000 + c.getInt(c.getColumnIndex("_id")),
                        my_intent, 0);
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        pendingIntent);

                // 화면에 간단히 표시
                Toast.makeText(AddDeadlineActivity.this,"Alarm 예정 : " +
                        (month + 1) + "월 " + day + "일 " + hour + "시 " + min + "분 ", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
        // 취소 버튼 클릭리스너
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
            calendar.set(year, month, day, 0, 0, 0);
            stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("%d월 %d일 (%s) ", month+1, day, getDateDay(calendar, date, "yyyyMMdd")));
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

    public static String getDateDay(Calendar cal, String date, String dateType){
        String day = new String();
        /*
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType);
        Date nDate = null;
        try {
            nDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(nDate);
        */
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
