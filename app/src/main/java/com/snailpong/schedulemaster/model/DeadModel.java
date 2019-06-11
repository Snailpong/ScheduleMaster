package com.snailpong.schedulemaster.model;

public class DeadModel {
    int id;
    String name;
    int whatid;
    int year;
    int month;
    int day;
    int hour;
    int min;
    int prev;

    public DeadModel(int id, String name, int whatid, int year, int month, int day, int hour, int min, int prev) {
        this.id = id;
        this.name = name;
        this.whatid = whatid;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.prev = prev;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
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

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getPrev() {
        return prev;
    }

    public void setPrev(int prev) {
        this.prev = prev;
    }


}
