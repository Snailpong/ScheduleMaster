package com.snailpong.schedulemaster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

public class RingTonePlayingService extends Service {
    //MediaPlayer mediaPlayer;
    //int startId;
    //boolean isRunning;
    //Context context;

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
        String get_vib_State = intent.getExtras().getString("vib_state");
        String getTitle = intent.getExtras().getString("title");
        String getText = intent.getExtras().getString("text");

        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

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

        assert get_vib_State != null;
        switch (get_vib_State) {
            case "vib on":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);  //진동
                break;
            case "vib off":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);  //벨
                break;
            default:
                break;
        }
        /*
        // 알람음 재생 X , 알람음 시작 클릭
        if(!this.isRunning && startId == 1) {
            this.isRunning = true;
            this.startId = 0;
        }

        // 알람음 재생 O , 알람음 종료 버튼 클릭
        else if(this.isRunning && startId == 0) {
            this.isRunning = false;
            this.startId = 0;
        }

        // 알람음 재생 X , 알람음 종료 버튼 클릭
        else if(!this.isRunning && startId == 0) {
            this.isRunning = false;
            this.startId = 0;

        }

        // 알람음 재생 O , 알람음 시작 버튼 클릭
        else if(this.isRunning && startId == 1){
            this.isRunning = true;
            this.startId = 1;
        }

        else {
        }
        */
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //mediaPlayer.stop();
        Log.d("onDestory() 실행", "서비스 파괴");

    }
}