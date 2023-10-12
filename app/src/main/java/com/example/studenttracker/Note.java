package com.example.studenttracker;

public class Note {
    private int id;
    private String title;
    private String body;
    private int courseId;

    public Note() {
    }

    public Note(int id, String title, String body, int courseId) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.courseId = courseId;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
