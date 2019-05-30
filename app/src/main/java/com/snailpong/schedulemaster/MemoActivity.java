package com.snailpong.schedulemaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class MemoActivity extends AppCompatActivity {

    EditText memoe;
    SharedPreferences pref;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        setTitle("메모장");
        memoe = (EditText)findViewById(R.id.memo_memo);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        String memo = pref.getString("memo"+String.valueOf(id), "");
        memoe.setText(memo);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("memo"+String.valueOf(id), memoe.getText().toString());
        editor.commit();
        finish();
    }
}
