package com.snailpong.schedulemaster.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.snailpong.schedulemaster.AddDeadlineActivity;
import com.snailpong.schedulemaster.DBHelper;
import com.snailpong.schedulemaster.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CancelAddDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DBHelper helper;
    private SQLiteDatabase db;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar calendar = Calendar.getInstance();
        helper = new DBHelper(getActivity(), "db.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(getActivity(),this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day){
        ContentValues values = new ContentValues();
        values.put("whatid", getArguments().getInt("id"));
        values.put("year",year);
        values.put("month",month);
        values.put("day",day);
        db.insert("noclass", null, values);

    }
}