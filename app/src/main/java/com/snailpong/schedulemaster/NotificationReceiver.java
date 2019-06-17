package com.snailpong.schedulemaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Calendar;
// 0시에 해야할 처리
public class NotificationReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        // intent로부터 전달받은 string
        String get_your_title = intent.getExtras().getString("title");
        String get_your_text = intent.getExtras().getString("text");
        /*
        int get_your_year = intent.getExtras().getInt("year");
        int get_your_month = intent.getExtras().getInt("month");
        int get_your_day = intent.getExtras().getInt("day");
        int get_your_hour = intent.getExtras().getInt("hour");
        int get_your_min = intent.getExtras().getInt("min");
        int get_your_whatid = intent.getExtras().getInt("whatid");
        */


        Log.d("Notifi", get_your_text + get_your_title);
        // 서비스 intent 생성
        Intent service_intent = new Intent(context, NotificationService.class);

        // Service로 extra string값 보내기
        service_intent.putExtra("title", get_your_title);
        service_intent.putExtra("text", get_your_text);
        /*
        service_intent.putExtra("year", get_your_year);
        service_intent.putExtra("month", get_your_month);
        service_intent.putExtra("day", get_your_day);
        service_intent.putExtra("hour", get_your_hour);
        service_intent.putExtra("min", get_your_min);
        service_intent.putExtra("whatid", get_your_whatid);
        */

        // start the service

            this.context.startService(service_intent);
    }
}
