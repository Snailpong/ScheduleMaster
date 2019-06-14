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
        db.execSQL("create table if not exists weekly(_id integer primary key autoincrement, name text, day integer, starttime text, endtime text, vib integer, gps integer, y real, x real);");
        db.execSQL("create table if not exists daily(_id integer primary key autoincrement, name text, day text, starttime text, endtime text, vib integer, gps integer, y real, x real)");
        db.execSQL("create table if not exists noclass(_id integer primary key autoincrement, whatid integer, year integer, month integer, day integer)");
        db.execSQL("create table if not exists deadline(_id integer primary key autoincrement, name text, whatid integer, year integer, month integer, day integer, hour integer, min integer, prev integer)");
        db.execSQL("create table if not exists alarmset(_id integer primary key autoincrement, state text, name text, whatid integer, hour integer, min integer, timebefore integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists weekly");
        db.execSQL("drop table if exists daily");
        db.execSQL("drop table if exists noclass");
        db.execSQL("drop table if exists deadline");
        db.execSQL("drop table if exists alarmset");
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
