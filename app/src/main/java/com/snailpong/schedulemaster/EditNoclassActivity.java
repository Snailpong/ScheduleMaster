package com.snailpong.schedulemaster;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import static com.snailpong.schedulemaster.EditDeadlineActivity.getDateDay;

public class EditNoclassActivity extends AppCompatActivity {

    private int year, month, day;
    private String date;
    private StringBuilder stringBuilder;
    private LinearLayout timelayout;
    private LinearLayout addBtn;
    private LinearLayout cancelBtn;
    private LinearLayout deleteBtn;
    private DBHelper helper;
    private SQLiteDatabase db;
    private int id;
    private TextView time;
    private TextView subject;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_noclass);
        setTitle("휴일 변경");

        time = (TextView) findViewById(R.id.noclass_ed_time);
        subject = (TextView) findViewById(R.id.noclass_ed_subject);
        timelayout = (LinearLayout) findViewById(R.id.noclass_ed_timelayout);
        addBtn = (LinearLayout) findViewById(R.id.noclass_ed_edit);
        cancelBtn = (LinearLayout) findViewById(R.id.noclass_ed_cancel);
        deleteBtn = (LinearLayout) findViewById(R.id.noclass_ed_delete);

        helper = new DBHelper(this, "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);

        Cursor query = db.query("noclass", null, "_id="+String.valueOf(id), null, null, null, null);
        query.moveToNext();

        subject.setText(intent.getStringExtra("name"));
        year = query.getInt(2);
        month = query.getInt(3);
        day = query.getInt(4);

        stringBuilder = new StringBuilder();
        date = String.format("%d%02d%d", year, month+1, day);
        stringBuilder.append(String.format("%d월 %d일 (%s)", month+1, day, getDateDay(date, "yyyyMMdd")));
        time.setText(stringBuilder.toString());

        timelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditNoclassActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put("year", year);
                values.put("month", month);
                values.put("day", day);
                db.update("noclass", values, "_id="+String.valueOf(id),null);
                finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.delete("noclass","_id="+String.valueOf(id),null);
                finish();
            }
        });

    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {
            year = _year;
            month = _month;
            day = _day;
            date = String.format("%d%02d%d", year, month+1, day);
            stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("%d월 %d일 (%s) ", month+1, day, getDateDay(date, "yyyyMMdd")));
            time.setText(stringBuilder.toString());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
