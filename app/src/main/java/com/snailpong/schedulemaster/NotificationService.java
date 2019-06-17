package com.snailpong.schedulemaster;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

public class NotificationService extends Service {

    //public static final int NOTIFICATION_ID = 1;
    DBHelper helper;
    SQLiteDatabase db;

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
        // Intent로부터 전달받은 string
        String getTitle = intent.getExtras().getString("title");
        String getText = intent.getExtras().getString("text");
        /*
        int get_your_year = intent.getExtras().getInt("year");
        int get_your_month = intent.getExtras().getInt("month");
        int get_your_day = intent.getExtras().getInt("day");
        int get_your_hour = intent.getExtras().getInt("hour");
        int get_your_min = intent.getExtras().getInt("min");
        int get_your_whatid = intent.getExtras().getInt("whatid");
        */
        helper = new DBHelper(NotificationService.this, "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);


        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "default";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    // 알람 중요도 설정 (HIGH, DEFULAT ...)
                    NotificationManager.IMPORTANCE_HIGH);

            NotificationManager nm = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            // 알림 빌더 생성
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    // 알림 속성 설정
                    .setContentTitle(getTitle)
                    .setContentText(getText)
                    .setSmallIcon(R.mipmap.ic_launcher) // 아이콘 수정 필요
                    .build();

            nm.notify(1,notification);

            ContentValues values = new ContentValues();
            values.put("category",1);
            values.put("title",getTitle);
            values.put("content",getText);
            values.put("time", Calendar.getInstance().getTimeInMillis());


            db.insert("alarm", null, values);

            // 서비스 시작
            final Intent service_intent = new Intent(getApplicationContext(),AlarmSetService.class); // 이동할 컴포넌트
            startService(service_intent);
            //stopSelf();
        }

        ContentValues values = new ContentValues();
        if (getTitle == "마감 알림") {
            values.put("state", "deadline");
        }

        else if (getTitle == "휴일 알림") {
            values.put("state", "noclass");
        }
        /*
        values.put("whatid", get_your_whatid);
        values.put("year", get_your_year);
        values.put("month", get_your_month);
        values.put("day", get_your_day);
        values.put("hour", get_your_hour);
        values.put("min", get_your_min);
        */
        db.insert("alarmset", null, values);

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //mediaPlayer.stop();
        db.close();
        Log.d("onDestory() 실행", "서비스 파괴");

    }
}