package com.snailpong.schedulemaster;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditDeadlineActivity extends AppCompatActivity {

    private int year, month, day, hour, min;
    private LinearLayout timelayout;
    private LinearLayout alarmlayout;
    private Button addBtn;
    private Button cancelBtn;
    private Button deleteBtn;
    private TextView time;
    private TextView alarm;
    private TextView subject;
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
    private int id;
    private PendingIntent pendingintent;
    private AlarmManager alarm_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deadline);
        setTitle("마감 수정");
        timelayout = (LinearLayout) findViewById(R.id.deadline_ed_timelayout);
        alarmlayout = (LinearLayout) findViewById(R.id.deadline_ed_alarmlayout);
        time = (TextView) findViewById(R.id.deadline_ed_time);
        alarm = (TextView) findViewById(R.id.deadline_ed_alarm);
        subject = (TextView) findViewById(R.id.deadline_ed_subject);
        title = (EditText) findViewById(R.id.deadline_ed_title);
        addBtn = (Button) findViewById(R.id.deadline_ed_edit);
        cancelBtn = (Button) findViewById(R.id.deadline_ed_cancel);
        deleteBtn = (Button) findViewById(R.id.deadline_ed_delete);
        helper = new DBHelper(EditDeadlineActivity.this, "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);

        Cursor query = db.query("deadline", null, "_id="+String.valueOf(id), null, null, null, null);
        query.moveToNext();

        title.setText(query.getString(1));
        subject.setText(intent.getStringExtra("name"));
        year = query.getInt(3);
        month = query.getInt(4);
        day = query.getInt(5);
        hour = query.getInt(6);
        min = query.getInt(7);
        int prev = query.getInt(8);
        for(int i=0; i!=5; ++i)
            if(selectedAlarmTime[i] == prev) selectedAlarmItem = i;

        stringBuilder = new StringBuilder();
        date = String.format("%d%02d%d", year, month+1, day);
        stringBuilder.append(String.format("%d월 %d일 (%s) ", month+1, day, getDateDay(date, "yyyyMMdd")));
        stringBuilder.append(String.format("%d시 %02d분", hour, min));
        time.setText(stringBuilder.toString());
        alarm.setText(alarmTimeItems[selectedAlarmItem] + " 전");

        timelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditDeadlineActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        alarmlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditDeadlineActivity.this);
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
                values.put("year", year);
                values.put("month", month);
                values.put("day", day);
                values.put("hour", hour);
                values.put("min", min);
                values.put("prev", selectedAlarmTime[selectedAlarmItem]);
                db.update("deadline", values, "_id="+String.valueOf(id),null);
                final Intent service_intent = new Intent(getApplicationContext(),AlarmSetService.class); // 이동할 컴포넌트
                startService(service_intent);
                finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // deadline의 db제거 및 알람 제거
                Cursor cursor = db.rawQuery("SELECT * FROM deadline WHERE whatid='" + id + "';", null);
                cursor.moveToFirst();
                while(cursor.moveToNext()) {
                    final Intent my_intent = new Intent(EditDeadlineActivity.this, NotificationReceiver.class);
                    alarm_manager = (AlarmManager) EditDeadlineActivity.this.getSystemService(ALARM_SERVICE);
                    pendingintent = PendingIntent.getBroadcast(EditDeadlineActivity.this
                            , 1000 + cursor.getColumnIndex("_id"), my_intent, 0);
                    alarm_manager.cancel(pendingintent);
                }
                db.delete("deadline","_id="+String.valueOf(id),null);

            }
        });
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {
            year = _year;
            month = _month;
            day = _day;
            date = String.format("%d%02d%d", year, month+1, day);
            stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("%d월 %d일 (%s) ", month+1, day, getDateDay(date, "yyyyMMdd")));
            new TimePickerDialog(EditDeadlineActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
