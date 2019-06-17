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
        String get_your_vib_state = intent.getExtras().getString("vib_state");
        String get_your_state = intent.getExtras().getString("state");
        int get_your_id = intent.getExtras().getInt("whatid");
        // AlarmService intent 생성
        Intent service_intent = new Intent(context, RingTonePlayingService.class);

        // Service로 extra string값 보내기
        service_intent.putExtra("vib_state", get_your_vib_state);
        service_intent.putExtra("state", get_your_state);
        service_intent.putExtra("whatid", get_your_id);

        // start the alarm service
        this.context.startService(service_intent);
    }
}