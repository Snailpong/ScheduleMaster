package com.snailpong.schedulemaster.dialog;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.snailpong.schedulemaster.AddDeadlineActivity;
import com.snailpong.schedulemaster.AlarmSetReceiver;
import com.snailpong.schedulemaster.AlarmSetService;
import com.snailpong.schedulemaster.DBHelper;
import com.snailpong.schedulemaster.NotificationReceiver;
import com.snailpong.schedulemaster.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class CancelAddDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    // noclass DB에 추가하는 역할
    private DBHelper helper;
    private SQLiteDatabase db;
    private final Calendar calendar = Calendar.getInstance();
    private AlarmManager alarm_manager;
    private PendingIntent pendingintent;;
    private String subject_name, alarmDate, text;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //final Calendar calendar = Calendar.getInstance();
        helper = new DBHelper(getActivity(), "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(getActivity(),this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day){
        ContentValues values = new ContentValues();
        values.put("whatid", getArguments().getInt("id"));
        values.put("year",year);
        values.put("month",month);
        values.put("day",day);
        db.insert("noclass", null, values);

        // id를 사용해 과목명 불러오기
        Cursor c = db.query("weekly", null
                , "_id="+String.valueOf(getArguments().getInt("id")), null,
                null, null, null, null);
        //c.moveToFirst();

        // 과목명
        subject_name = c.getString(c.getColumnIndex("name"));

        // pendingintent 식별을 위한 db 쿼리
        c = db.query("noclass", null
                , "whatid=" + getArguments().getInt("id"), null,
                null, null, null, null);
        //c.moveToFirst();
        // 알람 세팅
        final Intent my_intent = new Intent(getActivity(), NotificationReceiver.class);

        alarm_manager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        calendar.set(year, month, day, 0, 0, 0);

        alarmDate = String.format("%d%02d%d", year, month + 1, day);
        text = (month + 1) + "월 " + day + "일 "  + " " + subject_name + " " + " 휴강입니다.";

        // receiver에 string 값 넘겨주기
        my_intent.putExtra("title", "휴강 알림");
        my_intent.putExtra("text", text);

        // 0시에 한번 notification 설정
        pendingintent = PendingIntent.getBroadcast(getActivity(), 2000 + c.getInt(c.getColumnIndex("_id")),
                my_intent, 0);
        alarm_manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                pendingintent);
        /*
        my_intent.putExtra("year", year);
        my_intent.putExtra("month", month);
        my_intent.putExtra("day", day);
        my_intent.putExtra("hour", 0);
        my_intent.putExtra("min", 0);
        my_intent.putExtra("whatid", getArguments().getInt("id"));
        */

        // Intent intent = new Intent(getActivity(), AlarmSetService.class);
        // getActivity().startService(intent);
        db.close();
    }
}