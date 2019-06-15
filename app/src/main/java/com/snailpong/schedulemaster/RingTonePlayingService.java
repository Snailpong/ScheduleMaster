package com.snailpong.schedulemaster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class RingTonePlayingService extends Service {

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
        String get_state = intent.getExtras().getString("state");
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final LocationListener gpsLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                String provider = location.getProvider();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                double altitude = location.getAltitude();
                // x,y ; longitude, latitude
                /*
                txtResult.setText("위치정보 : " + provider + "\n" +
                        "위도 : " + longitude + "\n" +
                        "경도 : " + latitude + "\n" +
                */

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // 여기에 GPS 일치 여부에 따른 진동모드 변호나 설정(조건문 처리)
        assert get_state != null;
        switch (get_state) {
            case "vib on":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);  // 진동모드
                //Log.d("aaa", "진동");
                break;
            case "vib off":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);  // 벨소리모드
                //Log.d("aaa", "꺼짐");
                break;
            default:
                break;
        }



        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Log.d("onDestory() 실행", "서비스 파괴");
    }
}