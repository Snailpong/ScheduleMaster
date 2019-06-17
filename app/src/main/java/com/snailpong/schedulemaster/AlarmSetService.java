package com.snailpong.schedulemaster;
// 정기, 비정기 일정을 넣고 휴일과 비교해서 setting, 휴일의 경우는 0시에 noti 울리기
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
    private int year, month, day, dayOfWeek, hour, min;
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
        db.delete("alarmset",  "state=?", new String[]{"vib on"});
        db.delete("alarmset",  "state=?", new String[]{"vib off"});

        // AlarmReceiver intent 설정
        final Intent dialog_intent = new Intent(this, RingTonePlayingReceiver.class);
        //final Intent notification_intent = new Intent(this, NotificationReceiver.class);

        insertRegular();
        insertInregular();
        insertNoclass();
        insertDeadline();

        // 일정을 알람매니저에 넣기
        c = db.rawQuery("SELECT * FROM alarmset WHERE state='" + "vib on" + "' OR state='" + "vib off" + "';", null);
        while (c.moveToNext()) {
            String state = c.getString(c.getColumnIndex("state"));

            hour = c.getInt(c.getColumnIndex("hour"));
            min = c.getInt(c.getColumnIndex("min"));

            Calendar cal = Calendar.getInstance();
            int mhour = cal.get(Calendar.HOUR_OF_DAY);
            int mmin = cal.get(Calendar.MINUTE);

            if(hour*60+min >= mhour*60+mmin) {
                calendar.set(year, month, day, hour, min, 0);
                Log.d("Alarmsetservice", String.format("%d %d %d %d %d %d%d", year, month, day, hour, min, mhour, mmin));
                dialog_intent.putExtra("vib_state", state);
                pendingIntent = PendingIntent.getBroadcast(this, c.getInt(c.getColumnIndex("_id")),
                        dialog_intent, PendingIntent.FLAG_ONE_SHOT);
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        pendingIntent);
            }


        }

        Log.d("Alarmsetservice", "on startcommand() 호출");
        return START_NOT_STICKY;
    }
    private void insertRegular() {
        c = db.rawQuery("SELECT * FROM weekly WHERE vib='" + 1 + "';", null);
        c.moveToFirst();
        while (c.moveToNext()) {
            int day = c.getInt(c.getColumnIndex("day")) & mask[dayOfWeek];
            if (day != 0) {
                // DB에 넣기
                Log.d("Alarmsetservice", "정기일정 처리");
                AddCalendarDB(c, "vib on", "starttime");
                AddCalendarDB(c, "vib off", "endtime");
            }
        }
    }

    private void insertInregular() {
        c = db.rawQuery("SELECT * FROM daily WHERE day='" + days + "';", null);
        c.moveToFirst();
        while (c.moveToNext()) {
            // DB에 넣기
            AddCalendarDB(c, "vib on", "starttime");
            AddCalendarDB(c, "vib off", "endtime");
        }
    }

    private void insertNoclass() {
        c = db.rawQuery("SELECT * FROM noclass WHERE year='" + year + "' AND month='" + month + "' "
                + "AND day='" + day + "';", null);
        c.moveToFirst();
        while (c.moveToNext()) {
            // DB에 넣기
            ContentValues values = new ContentValues();
            values.put("state", "noclass");
            values.put("whatid", c.getInt(c.getColumnIndex("whatid")));
            values.put("hour", 0);
            values.put("min", 0);

            db.insert("alarmset", null, values);
        }
    }

    private void insertDeadline() {
        c = db.rawQuery("SELECT * FROM deadline", null);
        c.moveToFirst();
        while (c.moveToNext()) {
            // DB에 넣기
            int _year = c.getInt(c.getColumnIndex("year"));
            int _month = c.getInt(c.getColumnIndex("month"));
            int _day = c.getInt(c.getColumnIndex("day"));
            int _hour = c.getInt(c.getColumnIndex("hour"));
            int _min = c.getInt(c.getColumnIndex("min"));
            int _prev = c.getInt(c.getColumnIndex("prev"));

            calendar.set(_year, _month, _day, _hour, _min);
            calendar.add(calendar.HOUR_OF_DAY, -_prev);

            if (year == _year && month == _month && day == _day) {
                ContentValues values = new ContentValues();
                values.put("state", "deadline");
                values.put("whatid", c.getInt(c.getColumnIndex("whatid")));
                values.put("hour", c.getInt(c.getColumnIndex("hour")));
                values.put("min", c.getInt(c.getColumnIndex("min")));

                db.insert("alarmset", null, values);
            }
        }
    }

    private void AddCalendarDB (Cursor c, String state, String columnName) {
        ContentValues values = new ContentValues();
        String[] HourMin = (c.getString(c.getColumnIndex(columnName))).split(":");
        Log.d("state", c.getString(c.getColumnIndex("name")) + state);
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
