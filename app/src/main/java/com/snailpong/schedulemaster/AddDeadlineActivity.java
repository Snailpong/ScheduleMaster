package com.snailpong.schedulemaster;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddDeadlineActivity extends AppCompatActivity {

    private int year, month, day, hour, min;
    private LinearLayout timelayout;
    private LinearLayout alarmlayout;
    private TextView time;
    private TextView alarm;
    private StringBuilder stringBuilder;
    private Calendar calendar = Calendar.getInstance();
    private int nSelectItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deadline);
        setTitle("마감 추가");
        timelayout = (LinearLayout) findViewById(R.id.deadline_timelayout);
        alarmlayout = (LinearLayout) findViewById(R.id.deadline_alarmlayout);
        time = (TextView) findViewById(R.id.deadline_time);
        alarm = (TextView) findViewById(R.id.deadline_alarm);

        timelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddDeadlineActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        alarmlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] oItems = {"하나", "둘", "셋", "넷", "다셋"};


                AlertDialog.Builder oDialog = new AlertDialog.Builder(AddDeadlineActivity.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

                oDialog.setTitle("색상을 선택하세요")
                        .setSingleChoiceItems(oItems, -1, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                nSelectItem = which;
                            }
                        })
                        .setNeutralButton("선택", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (which >= 0)
                                    Toast.makeText(getApplicationContext(),
                                            oItems[nSelectItem], Toast.LENGTH_LONG).show();
                            }
                        })
                        .setCancelable(false)
                        .show();
                System.out.println("선택 " + nSelectItem);
            }
        });

    }
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {
            year = _year;
            month = _month;
            day = _day;
            String date = String.format("%d%02d%d", year, month, day);
            stringBuilder = new StringBuilder();
            stringBuilder.append(month + "월 " + day + "일 (" + getDateDay(date, "yyyyMMdd") +") ");
            new TimePickerDialog(AddDeadlineActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int _hour, int _min) {
            hour = _hour;
            min = _min;
            stringBuilder.append(hour + "시 " + min + "분");
            time.setText(stringBuilder.toString());
        }
    };

    public static String getDateDay(String date, String dateType){

        String day = "";

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

