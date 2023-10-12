package com.example.studenttracker;

import java.time.LocalDate;

public class Assessment {
    private int id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private int courseId;
    private String type;

    public Assessment(){}
    public Assessment(int id, String title, LocalDate startDate, LocalDate endDate, int courseId, String type) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.courseId = courseId;
        this.type = type;
    }

    public Assessment(String title, LocalDate startDate, LocalDate endDate, int courseId, String type) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.courseId = courseId;
        this.type = type;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
