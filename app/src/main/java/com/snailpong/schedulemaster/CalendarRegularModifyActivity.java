package com.snailpong.schedulemaster;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.List;

public class CalendarRegularModifyActivity extends AppCompatActivity {

    private int starthour, startmin, endhour, endmin;

    int id;
    Button add, cancel;
    EditText name;
    LinearLayout startlin, endlin;
    TextView starttxt, endtxt;
    CheckBox chkMon, chkTue, chkWed, chkThu, chkFri, chkSat, chkSun;
    CheckBox chkVib, chkGPS;
    List<CheckBox> chkArray;
    TimePickerDialog.OnTimeSetListener startTimeSetListener;
    TimePickerDialog.OnTimeSetListener endTimeSetListener;
    double y, x;

    DBHelper helper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_regular_modify);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        //Toast.makeText(getApplicationContext(), String.valueOf(id), Toast.LENGTH_LONG).show();

        setTitle("정기 일정 수정");

        name = (EditText) findViewById(R.id.calender_mod_reg_name);

        chkMon = (CheckBox) findViewById(R.id.checkBox_reg_mod_Mon);
        chkTue = (CheckBox) findViewById(R.id.checkBox_reg_mod_Tue);
        chkWed = (CheckBox) findViewById(R.id.checkBox_reg_mod_Wed);
        chkThu = (CheckBox) findViewById(R.id.checkBox_reg_mod_Thu);
        chkFri = (CheckBox) findViewById(R.id.checkBox_reg_mod_Fri);
        chkSat = (CheckBox) findViewById(R.id.checkBox_reg_mod_Sat);
        chkSun = (CheckBox) findViewById(R.id.checkBox_reg_mod_Sun);

        chkArray = Arrays.asList(chkMon, chkTue, chkWed, chkThu, chkFri, chkSat, chkSun);

        chkVib = (CheckBox)findViewById(R.id.checkBox_reg_mod_Vib);
        chkGPS = (CheckBox)findViewById(R.id.checkBox_reg_mod_GPS);

        add = (Button)findViewById(R.id.dia_reg_mod_add);
        cancel = (Button)findViewById(R.id.dia_reg_mod_cancel);
        startlin = (LinearLayout) findViewById(R.id.calender_mod_reg_starttime_lin);
        endlin = (LinearLayout) findViewById(R.id.calender_mod_reg_endtime_lin);
        starttxt = (TextView)findViewById(R.id.calender_mod_reg_starttime_txt);
        endtxt = (TextView)findViewById(R.id.calender_mod_reg_endtime_txt);

        helper = new DBHelper(CalendarRegularModifyActivity.this, "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        Cursor query = db.query("weekly", null, "_id="+String.valueOf(id), null, null, null, null);

        query.moveToNext();
        //Toast.makeText(getApplicationContext(), String.valueOf(query.getInt(0)), Toast.LENGTH_LONG).show();
        name.setText(query.getString(1));
        starttxt.setText(query.getString(3));
        endtxt.setText(query.getString(4));
        for(int i=0, day = query.getInt(2); i!=7; ++i) {
            if(day%2 == 1) chkArray.get(i).setChecked(true);
            day/=2;
        }

        starthour = Integer.parseInt(query.getString(3).substring(0,2));
        startmin = Integer.parseInt(query.getString(3).substring(3,5));
        endhour = Integer.parseInt(query.getString(4).substring(0,2));
        endmin = Integer.parseInt(query.getString(4).substring(3,5));
        chkVib.setChecked((query.getInt(5)==1)?true:false);
        chkGPS.setChecked((query.getInt(6)==1)?true:false);
        if(!chkVib.isChecked()) chkGPS.setEnabled(false);
        y = query.getDouble(7);
        x = query.getDouble(8);

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

                if(day != 0 || starttxt.getText().toString() == endtxt.getText().toString() || name.getText().toString().length() <= 1) {
                    ContentValues values = new ContentValues();
                    values.put("name", name.getText().toString());
                    values.put("day", day);
                    values.put("starttime", starttxt.getText().toString());
                    values.put("endtime", endtxt.getText().toString());
                    values.put("vib", chkVib.isChecked()?1:0);
                    values.put("gps", chkGPS.isChecked()?1:0);
                    values.put("y",y);
                    values.put("x",x);
                    db.update("weekly",values,"_id="+String.valueOf(id),null);
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CalendarRegularModifyActivity.this);
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
                new TimePickerDialog(CalendarRegularModifyActivity.this, startTimeSetListener, starthour, startmin, false).show();
            }
        });

        endlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(CalendarRegularModifyActivity.this, endTimeSetListener, endhour, endmin, false).show();
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
                Intent intent = new Intent(CalendarRegularModifyActivity.this, MapActivity.class);
                startActivityForResult(intent, 3000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 3000) {
            if(resultCode == RESULT_CANCELED){
                chkGPS.setChecked(false);
                Toast.makeText(this, "주소 미발견", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
