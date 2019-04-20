package com.snailpong.schedulemaster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AddDeadlineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deadline);
        setTitle("마감 추가");
    }
}
