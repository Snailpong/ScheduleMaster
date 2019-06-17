package com.snailpong.schedulemaster.dialog;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.snailpong.schedulemaster.DBHelper;
import com.snailpong.schedulemaster.NotificationReceiver;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class CancelAddDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    // 알람매니저 세팅, noclass DB에 insert
    private DBHelper helper;
    private SQLiteDatabase db;
    private Calendar calendar = Calendar.getInstance();
    private AlarmManager alarm_manager;
    private PendingIntent pendingintent;
    private int id;
    // 과목명, notification에 띄울 메세지
    private String subject_name, /*alarmDate,*/ text;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        helper = new DBHelper(getActivity(), "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);
        id = getArguments().getInt("id");

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(getActivity(),this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        alarm_manager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day){
        // DB에 넣기
        ContentValues values = new ContentValues();
        values.put("whatid", id);
        values.put("year", year);
        values.put("month", month);
        values.put("day", day);
        db.insert("noclass", null, values);

        // id를 사용해 과목명 불러오기
        Cursor c = db.query("weekly", null
                , "_id="+String.valueOf(id), null,
                null, null, null, null);
        c.moveToFirst();
        subject_name = c.getString(c.getColumnIndex("name"));

        // 0시에 한번 notification 설정
        calendar.set(year, month, day, 0, 0, 0);

        //alarmDate = String.format("%d%02d%d", year, month + 1, day);
        text = (month + 1) + "월 " + day + "일 "  + " " + subject_name + " " + " 휴강입니다.";

        // 알람 세팅
        final Intent my_intent = new Intent(getActivity(), NotificationReceiver.class);
        // receiver에 string 값 넘겨주기
        my_intent.putExtra("title", "휴강 알림");
        my_intent.putExtra("text", text);

        // pendingintent 식별을 위한 db 쿼리
        c = db.query("noclass", null
                , "whatid=" + id, null,
                null, null, null, null);
        c.moveToFirst();
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