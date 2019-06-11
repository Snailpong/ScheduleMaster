package com.snailpong.schedulemaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CalendarRegularAddActivity extends AppCompatActivity {

    private int starthour, startmin, endhour, endmin;

    DBHelper helper;
    SQLiteDatabase db;

    Button add, cancel;
    EditText name;
    LinearLayout startlin, endlin;
    TextView starttxt, endtxt;
    CheckBox chkMon, chkTue, chkWed, chkThu, chkFri, chkSat, chkSun;
    CheckBox chkVib, chkGPS;
    List<CheckBox> chkArray;
    TimePickerDialog.OnTimeSetListener startTimeSetListener;
    TimePickerDialog.OnTimeSetListener endTimeSetListener;

    AlarmManager alarm_manager;
    PendingIntent pendingIntent;
    Context context;
    String text;

    final Calendar calendar = Calendar.getInstance();
    int year, month, day, hour, minute;
    double y, x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_regular_add);
        setTitle("정기 일정 추가");

        name = (EditText) findViewById(R.id.calender_add_reg_name);

        chkMon = (CheckBox) findViewById(R.id.checkBox_add_reg_Mon);
        chkTue = (CheckBox) findViewById(R.id.checkBox_add_reg_Tue);
        chkWed = (CheckBox) findViewById(R.id.checkBox_add_reg_Wed);
        chkThu = (CheckBox) findViewById(R.id.checkBox_add_reg_Thu);
        chkFri = (CheckBox) findViewById(R.id.checkBox_add_reg_Fri);
        chkSat = (CheckBox) findViewById(R.id.checkBox_add_reg_Sat);
        chkSun = (CheckBox) findViewById(R.id.checkBox_add_reg_Sun);

        chkArray = Arrays.asList(chkMon, chkTue, chkWed, chkThu, chkFri, chkSat, chkSun);

        chkVib = (CheckBox)findViewById(R.id.checkBox_add_reg_Vib);
        chkGPS = (CheckBox)findViewById(R.id.checkBox_add_reg_GPS);
        chkGPS.setEnabled(false);

        add = (Button)findViewById(R.id.dia_add_reg_add);
        cancel = (Button)findViewById(R.id.dia_add_reg_cancel);
        startlin = (LinearLayout) findViewById(R.id.calender_add_reg_starttime_lin);
        endlin = (LinearLayout) findViewById(R.id.calender_add_reg_endtime_lin);
        starttxt = (TextView)findViewById(R.id.calender_add_reg_starttime_txt);
        endtxt = (TextView)findViewById(R.id.calender_add_reg_endtime_txt);

        helper = new DBHelper(CalendarRegularAddActivity.this, "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        this.context = this;
        // AlarmReceiver intent 생성
        final Intent my_intent = new Intent(this, AlarmReceiver.class);
        // 알람매니저 설정
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                starthour = hourOfDay;
                startmin = minute;
                starttxt.setText(String.format("%02d", starthour) + ":" + String.format("%02d", startmin));
                if (endhour == 0 && endmin == 0) {
                    endhour = starthour + 1;
                    endmin = startmin;
                    endtxt.setText(String.format("%02d", endhour) + ":" + String.format("%02d", endmin));
                }
            }
        };

        endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endhour = hourOfDay;
                endmin = minute;
                endtxt.setText(String.format("%02d", endhour) + ":" + String.format("%02d", endmin));
            }
        };

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = 0;
                for(int i=0; i!=7; i++)
                    if(chkArray.get(i).isChecked()) day += (1 << i);

                if(day != 0 && starttxt.getText().toString() != endtxt.getText().toString() && name.getText().toString().length() > 1) {
                    helper.addRegular(db, name.getText().toString(), day, starttxt.getText().toString(), endtxt.getText().toString(), chkVib.isChecked(), chkGPS.isChecked(), y, x);
/*
                    if(chkVib.isChecked()) {
                        // 기준 시간 세팅
                        c.set(ayear, amonth, aday, starthour, startmin, 0);g
                        text = "진동 모드로 변경되었습니다.";
                        // receiver에 string 값 넘겨주기
                        my_intent.putExtra("state","alarm on");
                        my_intent.putExtra("category_name", "진동 모드 on");
                        my_intent.putExtra("text", text);
                        // 알람 세팅
                        pendingIntent = PendingIntent.getBroadcast(CalendarRegularAddActivity.this, 0, my_intent,
                                PendingIntent.FLAG_ONE_SHOT);
                        alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP,
                              calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY * 7, pendingIntent);

                        calendar.set(ayear, amonth, aday, starthour, startmin, 0);
                        text = "진동 모드가 해제되었습니다.";
                        // receiver에 string 값 넘겨주기
                        my_intent.putExtra("state","alarm on");
                        my_intent.putExtra("category_name", "진동 모드 off");
                        my_intent.putExtra("text", text);
                        // 알람 세팅
                        pendingIntent = PendingIntent.getBroadcast(CalendarRegularAddActivity.this, 1, my_intent,
                                PendingIntent.FLAG_ONE_SHOT);
                        alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP,
                               calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                    }
*/
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CalendarRegularAddActivity.this);
                    //builder.setTitle("오류");
                    builder.setMessage("모든 항목을 입력하세요.");
                    builder.setPositiveButton("닫기", null);
                    builder.show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        startlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(CalendarRegularAddActivity.this, startTimeSetListener, starthour, startmin, false).show();
            }
        });

        endlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(CalendarRegularAddActivity.this, endTimeSetListener, endhour, endmin, false).show();
            }
        });

        chkVib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    chkGPS.setEnabled(true);
                } else {
                    chkGPS.setChecked(false);
                    chkGPS.setEnabled(false);
                }
            }
        });

        chkGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Intent intent = new Intent(CalendarRegularAddActivity.this, MapActivity.class);
                    startActivityForResult(intent, 3000);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 3000) {
            if(resultCode == RESULT_CANCELED){
                chkGPS.setChecked(false);
                Toast.makeText(this, "주소 미발견", Toast.LENGTH_SHORT).show();
            } else if(resultCode == RESULT_OK){
                y = data.getDoubleExtra("y", 0);
                x = data.getDoubleExtra("x", 0);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void initAlarmSet () {
        //알람 시간 설정
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.YEAR, year);
        switch (month){
            case 1:
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                break;
            case 2:
                calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
                break;
            case 3:
                calendar.set(Calendar.MONTH, Calendar.MARCH);
                break;
            case 4:
                calendar.set(Calendar.MONTH, Calendar.APRIL);
                break;
            case 5:
                calendar.set(Calendar.MONTH, Calendar.MAY);
                break;
            case 6:
                calendar.set(Calendar.MONTH, Calendar.JUNE);
                break;
            case 7:
                calendar.set(Calendar.MONTH, Calendar.JULY);
                break;
            case 8:
                calendar.set(Calendar.MONTH, Calendar.AUGUST);
                break;
            case 9:
                calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
                break;
            case 10:
                calendar.set(Calendar.MONTH, Calendar.OCTOBER);
                break;
            case 11:
                calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
                break;
            case 12:
                calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                break;

        }
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long aTime = System.currentTimeMillis();
        long bTime = calendar.getTimeInMillis();

        //하루의 시간을 나타냄
        long interval = 1000 * 60 * 60  * 24;

        //만일 내가 설정한 시간이 현재 시간보다 작다면 알람이 바로 울려버리기 때문에 이미 시간이 지난 알람은 다음날 울려야 한다.
        while(aTime>bTime){
            bTime += interval;
        }
    }
}
