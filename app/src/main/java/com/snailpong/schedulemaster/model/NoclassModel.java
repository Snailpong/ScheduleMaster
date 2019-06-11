package com.snailpong.schedulemaster.model;

public class NoclassModel {
    int id;
    int whatid;
    int year;
    int month;
    int day;

    public NoclassModel(int id, int whatid, int year, int month, int day) {
        this.id = id;
        this.whatid = whatid;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWhatid() {
        return whatid;
    }

    public void setWhatid(int whatid) {
        this.whatid = whatid;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
