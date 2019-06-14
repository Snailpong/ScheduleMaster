package com.snailpong.schedulemaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        // intent로부터 전달받은 string
        String get_your_title = intent.getExtras().getString("title");
        String get_your_text = intent.getExtras().getString("text");

        // 서비스 intent 생성
        Intent service_intent = new Intent(context, RingTonePlayingService.class);

        // Service로 extra string값 보내기
        service_intent.putExtra("title", get_your_title);
        service_intent.putExtra("text", get_your_text);

        // start the service
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            this.context.startForegroundService(service_intent);
        }else{
            this.context.startService(service_intent);
        }
    }
}
