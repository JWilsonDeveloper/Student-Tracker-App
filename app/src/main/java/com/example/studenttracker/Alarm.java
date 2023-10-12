package com.example.studenttracker;

public class Alarm {
    private int id;
    private String title;
    private long time;
    private int days;
    private String type;
    private int assocId;

    public Alarm() {
    }

    public Alarm(String title, long time, int days, String type, int assocId) {
        this.title = title;
        this.time = time;
        this.days = days;
        this.type = type;
        this.assocId = assocId;
    }

    public Alarm(int id, String title, long time, int days, String type, int assocId) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.days = days;
        this.type = type;
        this.assocId = assocId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAssocId() {
        return assocId;
    }

    public void setAssocId(int assocId) {
        this.assocId = assocId;
    }
}