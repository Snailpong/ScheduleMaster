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

    private DBHelper helper;
    private SQLiteDatabase db;
    @Override
    public void onReceive(Context context, Intent intent) {

        helper = new DBHelper(context, "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);
        db.execSQL("delete from " + "alarmset");
        //int get_your_tag = intent.getExtras().getInt("tag");
        this.context = context;
        // Service 서비스 intent 생성
        Intent service_intent = new Intent(context, AlarmSetService.class);
        //service_intent.putExtra("tag", get_your_tag);
        db.close();
        this.context.startService(service_intent);
    }
}