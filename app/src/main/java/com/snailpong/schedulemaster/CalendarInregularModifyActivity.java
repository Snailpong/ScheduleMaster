package com.snailpong.schedulemaster;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarInregularModifyActivity extends AppCompatActivity {

    private int starthour, startmin, endhour, endmin, ayear, amonth, aday;
    String days;

    int id;
    Button add, cancel, delete;
    EditText name;
    LinearLayout startlin, endlin, datelin;
    TextView starttxt, endtxt, datetxt;
    CheckBox chkVib, chkGPS;
    TimePickerDialog.OnTimeSetListener startTimeSetListener;
    TimePickerDialog.OnTimeSetListener endTimeSetListener;
    DatePickerDialog.OnDateSetListener dateListener;
    double y, x;

    DBHelper helper;
    SQLiteDatabase db;

    AlarmManager alarm_manager;
    PendingIntent pendingIntent;
    Context context;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_inregular_modify);
        setTitle("일정 수정");
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);

        name = (EditText) findViewById(R.id.calender_mod_inreg_name);

        chkVib = (CheckBox)findViewById(R.id.checkBox_mod_inreg_Vib);
        chkGPS = (CheckBox)findViewById(R.id.checkBox_mod_inreg_GPS);
        chkGPS.setEnabled(false);

        add = (Button)findViewById(R.id.dia_mod_inreg_mod);
        cancel = (Button)findViewById(R.id.dia_mod_inreg_cancel);
        delete = (Button)findViewById(R.id.dia_mod_inreg_delete);
        startlin = (LinearLayout) findViewById(R.id.calender_mod_inreg_starttime_lin);
        endlin = (LinearLayout) findViewById(R.id.calender_mod_inreg_endtime_lin);
        datelin = (LinearLayout) findViewById(R.id.calender_mod_inreg_date_lin);
        starttxt = (TextView)findViewById(R.id.calender_mod_inreg_starttime_txt);
        endtxt = (TextView)findViewById(R.id.calender_mod_inreg_endtime_txt);
        datetxt = (TextView) findViewById(R.id.calender_mod_inreg_date_txt);

        helper = new DBHelper(CalendarInregularModifyActivity.this, "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        this.context = this;
        // AlarmReceiver intent 생성
        final Intent Alarm_intent = new Intent(this, NotificationReceiver.class);
        // 알람매니저 설정
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Cursor query = db.query("daily", null, "_id="+String.valueOf(id), null, null, null, null);

        query.moveToNext();
        //Toast.makeText(getApplicationContext(), String.valueOf(query.getInt(0)), Toast.LENGTH_LONG).show();
        name.setText(query.getString(1));
        starttxt.setText(query.getString(3));
        endtxt.setText(query.getString(4));
        days = query.getString(2);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar c = Calendar.getInstance();

        try {
            Date date = format.parse(days);
            c.setTime(date);
            ayear = c.get(Calendar.YEAR);
            amonth = c.get(Calendar.MONTH);
            aday = c.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {}

        starthour = Integer.parseInt(query.getString(3).substring(0,2));
        startmin = Integer.parseInt(query.getString(3).substring(3,5));
        endhour = Integer.parseInt(query.getString(4).substring(0,2));
        endmin = Integer.parseInt(query.getString(4).substring(3,5));
        chkVib.setChecked((query.getInt(5)==1)?true:false);
        chkGPS.setChecked((query.getInt(6)==1)?true:false);
        if(!chkVib.isChecked()) chkGPS.setEnabled(false);
        y = query.getDouble(7);
        x = query.getDouble(8);

        datetxt.setText(String.valueOf(ayear)+"년 "+String.valueOf(amonth+1)+"월 "+String.valueOf(aday)+"일");

        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                ayear = year;
                amonth = month;
                aday = day;
                datetxt.setText(String.valueOf(year)+"년 "+String.valueOf(month+1)+"월 "+String.valueOf(day)+"일");
                days = String.valueOf(year)+"-"+String.format("%02d",month+1)+"-"+String.format("%02d",day);
                Toast.makeText(CalendarInregularModifyActivity.this, days, Toast.LENGTH_LONG).show();
            }
        };

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

        datelin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CalendarInregularModifyActivity.this, dateListener, ayear, amonth, aday).show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(starttxt.getText().toString() != endtxt.getText().toString() && name.getText().toString().length() > 1) {
                    ContentValues values = new ContentValues();
                    values.put("name", name.getText().toString());
                    values.put("day", days);
                    values.put("starttime", starttxt.getText().toString());
                    values.put("endtime", endtxt.getText().toString());
                    values.put("vib", chkVib.isChecked()?1:0);
                    values.put("gps", chkGPS.isChecked()?1:0);
                    values.put("y",y);
                    values.put("x",x);
                    db.update("daily",values,"_id="+String.valueOf(id),null);
                    if (chkVib.isChecked()) {
                        final Intent service_intent = new Intent(getApplicationContext(), AlarmSetService.class); // 이동할 컴포넌트
                        startService(service_intent);
                    }
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CalendarInregularModifyActivity.this);
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

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.delete("daily","_id="+String.valueOf(id),null);
                finish();
            }
        });

        startlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(CalendarInregularModifyActivity.this, startTimeSetListener, starthour, startmin, false).show();
            }
        });

        endlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(CalendarInregularModifyActivity.this, endTimeSetListener, endhour, endmin, false).show();
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
                    Intent intent = new Intent(CalendarInregularModifyActivity.this, MapActivity.class);
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
}
