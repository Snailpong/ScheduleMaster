package com.snailpong.schedulemaster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    DBHelper helper;
    SQLiteDatabase db;
    Location locationA;
    Location locationB;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // main func
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Intent로부터 전달받은 string
        String get_vib_state = intent.getExtras().getString("vib_state");
        String get_state = intent.getExtras().getString("state");
        int get_id = intent.getExtras().getInt("whatid");

        VibModeChangeWithGps(get_state, get_vib_state, get_id);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Log.d("onDestory() 실행", "서비스 파괴");
    }

    private void VibModeChangeWithGps (String get_state, String get_vib_state, int get_id) {
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        double pos_x, pos_y;
        // x,y ; longitude(위도), latitude(경도)
        // DB에서 저장한 좌표값 불러오기
        Cursor c = db.query(get_state, null
                , "_id="+String.valueOf(get_id), null,
                null, null, null, null);

        if (c.getInt(c.getColumnIndex("gps")) == 1) {
            pos_x = c.getDouble(c.getColumnIndex("x"));
            pos_y = c.getDouble(c.getColumnIndex("y"));

            locationA = new Location("point A");
            locationA.setLongitude(pos_x);
            locationA.setLatitude(pos_y);

            // GPS에서 내가 있는 좌표 설정
            final LocationListener gpsLocationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    locationB = new Location("point B");
                    locationB.setLongitude(longitude);
                    locationB.setLatitude(latitude);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) { }
                public void onProviderEnabled(String provider) { }
                public void onProviderDisabled(String provider) { }
            };

            double distance = locationA.distanceTo(locationB);
            // 300m 미만일 경우 진동모드 변경함
            if (distance < 0.3) {
                vibModeChange (get_vib_state);
            }
        }

        else {
            vibModeChange (get_vib_state);
        }
    }

    // 두 location 사이의 거리
    /*
    private double distanceTo(Location dest) {
        synchronized (mResults) {
            if (mLatitude != mLat1 || mLongitude != mLon1
                    || dest.mLatitude != mLat2 || dest.mLongitude != mLon2) {
                computeDistanceAndBearing(mLatitude, mlongitude, dest.mLatitude, dest.mLongitude, mResults);
                mLat1 = mLatitude;
                mLon1 = mLongitude;
                mLat2 = dest.mLatitude;
                mLon2 = dest.mLongitude;
                mDistance = mResults[0];
                mInitialBearing = mResults[1];
            }
            return mDistance;
        }
    }
    */
    // 진동모드 변경
    private void vibModeChange (String get_vib_state) {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        assert get_vib_state != null;
        switch (get_vib_state) {
            case "vib on":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);  // 진동모드
                Log.d("aaa", "진동");
                break;
            case "vib off":
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);  // 벨소리모드
                Log.d("aaa", "꺼짐");
                break;
            default:
                break;
        }
    }
}