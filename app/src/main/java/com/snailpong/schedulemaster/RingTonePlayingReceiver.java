package com.snailpong.schedulemaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class RingTonePlayingReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        // intent로부터 전달받은 string
        String get_your_state = intent.getExtras().getString("state");
        // AlarmService intent 생성
        Intent service_intent = new Intent(context, RingTonePlayingService.class);

        // Service로 extra string값 보내기
        service_intent.putExtra("state", get_your_state);

        // start the alarm service
        this.context.startService(service_intent);
    }
}