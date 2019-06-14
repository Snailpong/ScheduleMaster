package com.snailpong.schedulemaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;

public class AlarmSetService extends Service {
    private PendingIntent pendingIntent;
    private DBHelper helper;
    private SQLiteDatabase db;
    private Calendar calendar = Calendar.getInstance();
    private Cursor c;
    private AlarmManager alarm_manager;
    int year, month, day, dayOfWeek;
    // weekly DB table의 day와 calendar의 DAY_OF_WEEK 비교용
    int[] mask = {0, 0b1000000, 0b0000001, 0b0000010, 0b0000100, 0b0001000, 0b0010000, 0b0100000};
    String days;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        helper = new DBHelper(this, "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        days = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);

        // 알람매니저 설정
        alarm_manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // 전 날의 db 초기화
        db.execSQL("delete from " + "alarmset");

        // AlarmReceiver intent 설정
        final Intent my_intent = new Intent(this, RingTonePlayingReceiver.class);

        // (1) 정기 일정 넣기

        c = db.rawQuery("SELECT * FROM weekly", null);
        while (c.moveToNext()) {
            int day = c.getInt(c.getColumnIndex("day")) & mask[dayOfWeek];
            if (day != 0) {
                // DB에 넣기
                AddCalendarDB(c, "vib on", "starttime");
                AddCalendarDB(c, "vib off", "endtime");
            }
        }

        // (1) 정기 일정 넣기 -- 수정(deadline, noclass 일정 넣기임)
        /*
        c = db.rawQuery("SELECT * FROM weekly WHERE year='" + year + "' AND month='" + month + "';", null);
        while (c.moveToNext()) {
            int day = c.getInt(c.getColumnIndex("day")) & mask[dayOfWeek];
            if (day != 0) {
                // DB에 넣기
                AddCalendarDB(c, "vib on", "starttime");
                AddCalendarDB(c, "vib off", "endtime");
            }
        }
        */
        // (2) 비정기 일정 넣기
        c = db.rawQuery("SELECT * FROM daily WHERE day='" + days + "';", null);
        while (c.moveToNext()) {
            // DB에 넣기
            AddCalendarDB(c, "vib on", "starttime");
            AddCalendarDB(c, "vib off", "endtime");
        }

        // (3) 일정을 알람매니저에 넣기
        c = db.query("alarmset", null, null, null, null, null, null);
        c.moveToFirst();

        while (c.moveToNext()) {
            String state = c.getString(c.getColumnIndex("state"));
            ;
            int hour = c.getInt(c.getColumnIndex("hour"));
            int min = c.getInt(c.getColumnIndex("min"));

            calendar.set(year, month, day, hour, min, 0);

            my_intent.putExtra("state", state);
            pendingIntent = PendingIntent.getBroadcast(this, c.getInt(c.getColumnIndex("_id")),
                    my_intent, PendingIntent.FLAG_ONE_SHOT);
            alarm_manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    pendingIntent);
        }
        Log.d("Alarmsetservice", "on startcommand() 호출");
        stopSelf();
        return START_NOT_STICKY;
    }

    private void AddCalendarDB (Cursor c, String state, String columnName) {
        ContentValues values = new ContentValues();
        String[] HourMin = (c.getString(c.getColumnIndex(columnName))).split(":");
        values.put("state", state);
        values.put("name", c.getString(c.getColumnIndex("name")));
        values.put("hour", Integer.valueOf(HourMin[0]));
        values.put("min", Integer.valueOf(HourMin[1]));
        db.insert("alarmset", null, values);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        Log.d("Alarmsetservice", "on destroy() 호출");
    }
}
