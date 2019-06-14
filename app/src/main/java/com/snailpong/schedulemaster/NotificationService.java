package com.snailpong.schedulemaster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

public class NotificationService extends Service {

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

        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        helper = new DBHelper(NotificationService.this, "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "default";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    // 알람 중요도 설정 (HIGH, DEFULAT ...)
                    NotificationManager.IMPORTANCE_HIGH);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            // 알림 빌더 생성
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    // 알림 속성 설정
                    .setContentTitle(getTitle)
                    .setContentText(getText)
                    .setSmallIcon(R.mipmap.ic_launcher) // 아이콘 수정 필요
                    .build();
            // 서비스 시작
            startForeground(1, notification);
        }

        ContentValues values = new ContentValues();

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //mediaPlayer.stop();
        Log.d("onDestory() 실행", "서비스 파괴");

    }
}