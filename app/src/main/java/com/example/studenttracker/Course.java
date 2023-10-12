package com.example.studenttracker;

import java.time.LocalDate;

public class Course {
    private int id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private String instructName;
    private String instructPhone;
    private String instructEmail;
    private int termId;

    public Course() {
    }

    public Course(String title, LocalDate startDate, LocalDate endDate, Status status, String instructName, String instructPhone, String instructEmail, int termId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.instructName = instructName;
        this.instructPhone = instructPhone;
        this.instructEmail = instructEmail;
        this.termId = termId;
    }

    public Course(int id, String title, LocalDate startDate, LocalDate endDate, Status status, String instructName, String instructPhone, String instructEmail, int termId) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.instructName = instructName;
        this.instructPhone = instructPhone;
        this.instructEmail = instructEmail;
        this.termId = termId;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getInstructName() {
        return instructName;
    }

    public void setInstructName(String instructName) {
        this.instructName = instructName;
    }

    public String getInstructPhone() {
        return instructPhone;
    }

    public void setInstructPhone(String instructPhone) {
        this.instructPhone = instructPhone;
    }

    public String getInstructEmail() {
        return instructEmail;
    }

    public void setInstructEmail(String instructEmail) {
        this.instructEmail = instructEmail;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }
}
