package com.snailpong.schedulemaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;

public class AlarmSetReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        // Service 서비스 intent 생성
        Intent service_intent = new Intent(context, AlarmSetService.class);

        // start the service
        //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
        //    this.context.startForegroundService(service_intent);
        //}else{
        this.context.startService(service_intent);
        //}
    }
}