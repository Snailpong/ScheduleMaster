package com.snailpong.schedulemaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

public class AlarmReceiver extends BroadcastReceiver {
    Context context;
    private DBHelper helper;
    private SQLiteDatabase db;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        // intent로부터 전달받은 string
        String get_your_state = intent.getExtras().getString("state");
        String get_your_title = intent.getExtras().getString("title");
        String get_your_text = intent.getExtras().getString("text");
        String get_your_vib_state = intent.getExtras().getString("vib_state");

        helper = new DBHelper(this.context, "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);
        // db와 비교해 삭제된 알람일 경우 울리지 않음
        switch(get_your_state) {
            case "daily": { }
            // weekly : 알람이 매일 반복되므로, 해당 요일이 아닐 경우 울리지 않음
            case "weekly": { }
            case "deadline": { }
            // noclass : 해당 날짜일 경우 울리지 않음
            case "noclass": { }
        }
        // RingtonePlayingService 서비스 intent 생성
        Intent service_intent = new Intent(context, RingTonePlayingService.class);

        // RingtonePlayinService로 extra string값 보내기
        service_intent.putExtra("state", get_your_state);
        service_intent.putExtra("title", get_your_title);
        service_intent.putExtra("text", get_your_text);
        service_intent.putExtra("vib_state", get_your_vib_state);

        // start the ringtone service
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            this.context.startForegroundService(service_intent);
        }else{
            this.context.startService(service_intent);
        }
    }
}
