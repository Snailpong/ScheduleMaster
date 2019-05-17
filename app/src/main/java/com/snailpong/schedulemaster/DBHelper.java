package com.snailpong.schedulemaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("drop table if exists weekly");
        db.execSQL("create table if not exists weekly(_id integer primary key autoincrement, name text, day integer, starttime text, endtime text, vib integer, gps integer, x real, y real);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists weekly");
        onCreate(db);
    }

    public void addRegular(SQLiteDatabase db, String name, int day, String startTime, String endTime, boolean vib, boolean gps, double y, double x) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("day", day);
        values.put("starttime", startTime);
        values.put("endtime", endTime);
        values.put("vib", vib?1:0);
        values.put("gps", gps?1:0);
        values.put("y",y);
        values.put("x",x);
        db.insert("weekly", null, values);
    }
}
