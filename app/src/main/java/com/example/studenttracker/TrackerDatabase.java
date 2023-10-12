package com.example.studenttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrackerDatabase extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "tracker.db";
    private static TrackerDatabase mTrackerDb;
    private static SQLiteDatabase db;
    private boolean firstLaunch;

    public static TrackerDatabase getInstance(Context context) {
        if (mTrackerDb == null) {
            mTrackerDb = new TrackerDatabase(context);
            mTrackerDb.db = mTrackerDb.getWritableDatabase();
        }
        if (mTrackerDb.firstLaunch == true) {
            mTrackerDb.loadSampleData(db);
            mTrackerDb.firstLaunch = false;
        }
        return mTrackerDb;
    }

    private TrackerDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class TermTable {
        private static final String TABLE = "terms";
        private static final String COL_ID = "id";
        private static final String COL_TITLE = "title";
        private static final String COL_START = "startDate";
        private static final String COL_END = "endDate";
    }

    private static final class CourseTable {
        private static final String TABLE = "courses";
        private static final String COL_ID = "id";
        private static final String COL_TITLE = "title";
        private static final String COL_START = "startDate";
        private static final String COL_END = "endDate";
        private static final String COL_STATUS = "status";
        private static final String COL_NAME = "instructName";
        private static final String COL_PHONE = "instructPhone";
        private static final String COL_EMAIL = "instructEmail";
        private static final String COL_TERM_ID = "termId";
    }

    private static final class AssessmentTable {
        private static final String TABLE = "assessments";
        private static final String COL_ID = "id";
        private static final String COL_TITLE = "title";
        private static final String COL_START = "startDate";
        private static final String COL_END = "endDate";
        private static final String COL_TYPE = "type";
        private static final String COL_COURSE_ID = "courseId";
    }


    private static final class NoteTable {
        private static final String TABLE = "notes";
        private static final String COL_ID = "id";
        private static final String COL_TITLE = "title";
        private static final String COL_BODY = "body";
        private static final String COL_COURSE_ID = "courseId";
    }

    private static final class CourseAlarmTable {
        private static final String TABLE = "courseAlarms";
        private static final String COL_ID = "id";
        private static final String COL_TITLE = "title";
        private static final String COL_TIME = "time";
        private static final String COL_DAYS = "days";
        private static final String COL_TYPE = "type";
        private static final String COL_COURSE_ID = "courseId";
    }

    private static final class AssessmentAlarmTable {
        private static final String TABLE = "assessmentAlarms";
        private static final String COL_ID = "id";
        private static final String COL_TITLE = "title";
        private static final String COL_TIME = "time";
        private static final String COL_DAYS = "days";
        private static final String COL_TYPE = "type";
        private static final String COL_ASSESSMENT_ID = "courseId";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create terms table
        db.execSQL("create table " + TermTable.TABLE + " (" +
                TermTable.COL_ID + " integer primary key autoincrement, " +
                TermTable.COL_TITLE + " text, " +
                TermTable.COL_START + " text, " +
                TermTable.COL_END + " text)");

        // Create course table with foreign key that cascade deletes
        db.execSQL("create table " + CourseTable.TABLE + " (" +
                CourseTable.COL_ID + " integer primary key autoincrement, " +
                CourseTable.COL_TITLE + " text, " +
                CourseTable.COL_START + " text, " +
                CourseTable.COL_END + " text, " +
                CourseTable.COL_STATUS + " text, " +
                CourseTable.COL_NAME + " text, " +
                CourseTable.COL_PHONE + " text, " +
                CourseTable.COL_EMAIL + " text, " +
                CourseTable.COL_TERM_ID + " integer, " +
                "foreign key(" + CourseTable.COL_TERM_ID + ") references " +
                TermTable.TABLE + "(" + TermTable.COL_ID + "))");

        db.execSQL("create table " + AssessmentTable.TABLE + " (" +
                AssessmentTable.COL_ID + " integer primary key autoincrement, " +
                AssessmentTable.COL_TITLE + " text, " +
                AssessmentTable.COL_START + " text, " +
                AssessmentTable.COL_END + " text, " +
                AssessmentTable.COL_TYPE + " text, " +
                AssessmentTable.COL_COURSE_ID + " integer, " +
                "foreign key(" + AssessmentTable.COL_COURSE_ID + ") references " +
                CourseTable.TABLE + "(" + CourseTable.COL_ID + "))");

        db.execSQL("create table " + NoteTable.TABLE + " (" +
                NoteTable.COL_ID + " integer primary key autoincrement, " +
                NoteTable.COL_TITLE + " text, " +
                NoteTable.COL_BODY + " text, " +
                NoteTable.COL_COURSE_ID + " integer, " +
                "foreign key(" + NoteTable.COL_COURSE_ID + ") references " +
                CourseTable.TABLE + "(" + CourseTable.COL_ID + ") on delete cascade)");

        db.execSQL("create table " + CourseAlarmTable.TABLE + " (" +
                CourseAlarmTable.COL_ID + " integer primary key autoincrement, " +
                CourseAlarmTable.COL_TITLE + " text, " +
                CourseAlarmTable.COL_TIME + " text, " +
                CourseAlarmTable.COL_DAYS + " integer, " +
                CourseAlarmTable.COL_TYPE + " text, " +
                CourseAlarmTable.COL_COURSE_ID + " integer, " +
                "foreign key(" + CourseAlarmTable.COL_COURSE_ID + ") references " +
                CourseTable.TABLE + "(" + CourseTable.COL_ID + ") on delete cascade)");

        db.execSQL("create table " + AssessmentAlarmTable.TABLE + " (" +
                AssessmentAlarmTable.COL_ID + " integer primary key autoincrement, " +
                AssessmentAlarmTable.COL_TITLE + " text, " +
                AssessmentAlarmTable.COL_TIME + " text, " +
                AssessmentAlarmTable.COL_DAYS + " integer, " +
                AssessmentAlarmTable.COL_TYPE + " text, " +
                AssessmentAlarmTable.COL_ASSESSMENT_ID + " integer, " +
                "foreign key(" + AssessmentAlarmTable.COL_ASSESSMENT_ID + ") references " +
                AssessmentTable.TABLE + "(" + AssessmentTable.COL_ID + ") on delete cascade)");


        db.execSQL("create trigger delete_term before delete on " + TermTable.TABLE + " for each row begin " +
                "select case when (( select count(*) from " + CourseTable.TABLE + " where " + CourseTable.COL_TERM_ID + "= old." + TermTable.COL_ID + ") > 0" +
                ") then raise (abort, 'Terms with associated courses cannot be deleted') end; end;");

        db.execSQL("create trigger delete_course before delete on " + CourseTable.TABLE + " for each row begin " +
                "select case when (( select count(*) from " + AssessmentTable.TABLE + " where " + AssessmentTable.COL_COURSE_ID + "= old." + CourseTable.COL_ID + ") > 0" +
                ") then raise (abort, 'Courses with associated assessments cannot be deleted') end; end;");

        firstLaunch = true;
    }

    public void logTerms(){
        List<Term> terms = getTerms();
        for (Term term: terms) {
            int termId = term.getId();
            Log.d("logTerms", "--------Term id: " + termId + "--------");
            Log.d("logTerms", "Term title: " + term.getTitle());
            Log.d("logTerms", "Term start: " + term.getStartDate());
            Log.d("logTerms", "Term end: " + term.getEndDate());
        }
    }

    public void logAllCourses() {
        for (Course course: getAllCourses()) {
            Log.d("logCourses", "--------Term id: " + course.getTermId() + "--------");
            Log.d("logCourses", "Course id: " + course.getId());
            Log.d("logCourses", "Course title: " + course.getTitle());
            Log.d("logCourses", "Course start: " + course.getStartDate());
            Log.d("logCourses", "Course end: " + course.getEndDate());
            Log.d("logCourses", "Course status: " + course.getStatus());
            Log.d("logCourses", "Course instructor: " + course.getInstructName());
            Log.d("logCourses", "Phone: " + course.getInstructPhone());
            Log.d("logCourses", "Email: " + course.getInstructEmail());
        }
    }

    public void logAllAssessments() {
        for (Course course: getAllCourses()) {
            List<Assessment> assessments = getAssessments(course.getId());
            for (Assessment assessment : assessments) {
                Log.d("logAssessments", "------------Term id: " + course.getTermId());
                Log.d("logAssessments", "----------Course id: " + course.getId());
                Log.d("logAssessments", "------Assessment id: " + assessment.getId());
                Log.d("logAssessments", "Assessment title: " + (assessment.getTitle()));
                Log.d("logAssessments", "Assessment start: " + assessment.getStartDate());
                Log.d("logAssessments", "Assessment end: " + assessment.getEndDate());
                Log.d("logAssessments", "Assessment type: " + assessment.getType());
            }
        }
    }

    public List<Note> logAllNotes(){
        List<Note> notes = new ArrayList<>();
        String orderBy = TermTable.COL_ID + " asc";
        String sql = "select * from " + TermTable.TABLE + " order by " + orderBy;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setBody(cursor.getString(2));
                note.setCourseId(cursor.getInt(3));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TermTable.TABLE);
        db.execSQL("drop table if exists " + CourseTable.TABLE);
        db.execSQL("drop table if exists " + AssessmentTable.TABLE);
        db.execSQL("drop table if exists " + CourseAlarmTable.TABLE);
        db.execSQL("drop table if exists " + AssessmentAlarmTable.TABLE);
        db.execSQL("drop table if exists " + NoteTable.TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {

            // Enable foreign key constraints
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                db.execSQL("pragma foreign_keys = on;");
            } else {
                db.setForeignKeyConstraintsEnabled(true);
            }
        }
    }

    public List<Term> getTerms() {
        List<Term> terms = new ArrayList<>();
        String orderBy = TermTable.COL_ID + " asc";
        String sql = "select * from " + TermTable.TABLE + " order by " + orderBy;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Term term = new Term();
                term.setId(cursor.getInt(0));
                term.setTitle(cursor.getString(1));
                term.setStartDate(LocalDate.parse(cursor.getString(2)));
                term.setEndDate(LocalDate.parse(cursor.getString(3)));
                terms.add(term);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return terms;
    }

    public Term getTerm(int termId) throws Exception {
        Term match;
        List<Term> terms = this.getTerms();
        for(Term term: terms) {
            if(term.getId() == termId) {
                match = term;
                return match;
            }
        }
        throw new Exception("No term found with termId: " + termId);
    }

    public boolean addTerm(Term term) {
        ContentValues values = new ContentValues();
        values.put(TermTable.COL_TITLE, term.getTitle());
        values.put(TermTable.COL_START, String.valueOf(term.getStartDate()));
        values.put(TermTable.COL_END, String.valueOf(term.getEndDate()));
        long id = db.insert(TermTable.TABLE, null, values);
        return id != -1;
    }

    public void updateTerm(Term term) {
        ContentValues values = new ContentValues();
        values.put(TermTable.COL_TITLE, term.getTitle());
        values.put(TermTable.COL_START, String.valueOf(term.getStartDate()));
        values.put(TermTable.COL_END, String.valueOf(term.getEndDate()));
        db.update(TermTable.TABLE, values,
                TermTable.COL_ID + " = ?", new String[] {String.valueOf(term.getId())});
    }

    public void deleteTerm(Term term) throws SQLException {
        db.delete(TermTable.TABLE, TermTable.COL_ID + " = ?", new String[]{String.valueOf(term.getId())});
    }


    public List<Course> getCourses(int termId) {
        List<Course> courses = new ArrayList<>();
        String sql = "select * from " + CourseTable.TABLE +
                " where " + CourseTable.COL_TERM_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {String.valueOf(termId)});
        if (cursor.moveToFirst()) {
            do {
                Course course = new Course();
                course.setId(cursor.getInt(0));
                course.setTitle(cursor.getString(1));
                course.setStartDate(LocalDate.parse(cursor.getString(2)));
                course.setEndDate(LocalDate.parse(cursor.getString(3)));
                course.setStatus(Status.valueOf(cursor.getString(4)));
                course.setInstructName(cursor.getString(5));
                course.setInstructPhone(cursor.getString(6));
                course.setInstructEmail(cursor.getString(7));
                course.setTermId(cursor.getInt(8));
                courses.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return courses;
    }

    public Course getCourse(long courseId) throws Exception {
        Course match;
        List<Course> courses = getAllCourses();
        for(Course course: courses) {
            if(course.getId() == courseId) {
                match = course;
                return match;
            }
        }
        throw new Exception("No course found with courseId: " + courseId);
    }

    public List<Course> getAllCourses() {
        List<Term> terms = getTerms();
        List<Course> courses = new ArrayList<>();
        for (Term term: terms) {
            for (Course course: getCourses(term.getId())) {
                courses.add(course);
            }
        }
        return courses;
    }


    public long addCourse(Course course) {
        ContentValues values = new ContentValues();
        values.put(CourseTable.COL_TITLE, course.getTitle());
        values.put(CourseTable.COL_START, String.valueOf(course.getStartDate()));
        values.put(CourseTable.COL_END, String.valueOf(course.getEndDate()));
        values.put(CourseTable.COL_STATUS, String.valueOf(course.getStatus()));
        values.put(CourseTable.COL_NAME, course.getInstructName());
        values.put(CourseTable.COL_PHONE, course.getInstructPhone());
        values.put(CourseTable.COL_EMAIL, course.getInstructEmail());
        values.put(CourseTable.COL_TERM_ID, course.getTermId());
        long id = db.insert(CourseTable.TABLE, null, values);
        return id;
    }

    public void updateCourse(Course course) {
        ContentValues values = new ContentValues();
        values.put(CourseTable.COL_TITLE, course.getTitle());
        values.put(CourseTable.COL_START, String.valueOf(course.getStartDate()));
        values.put(CourseTable.COL_END, String.valueOf(course.getEndDate()));
        values.put(CourseTable.COL_STATUS, String.valueOf(course.getStatus()));
        values.put(CourseTable.COL_NAME, course.getInstructName());
        values.put(CourseTable.COL_PHONE, course.getInstructPhone());
        values.put(CourseTable.COL_EMAIL, course.getInstructEmail());
        values.put(CourseTable.COL_TERM_ID, course.getTermId());
        db.update(CourseTable.TABLE, values,
                CourseTable.COL_ID + " = " + course.getId(), null);
    }

    public void deleteCourse(long courseId) throws SQLException {
        db.delete(CourseTable.TABLE, CourseTable.COL_ID + " = " + courseId, null);
    }

    public List<Assessment> getAssessments(int courseId) {
        List<Assessment> assessments = new ArrayList<>();
        String sql = "select * from " + AssessmentTable.TABLE +
                " where " + AssessmentTable.COL_COURSE_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {String.valueOf(courseId)});
        if (cursor.moveToFirst()) {
            do {
                Assessment assessment = new Assessment();
                assessment.setId(cursor.getInt(0));
                assessment.setTitle(cursor.getString(1));
                assessment.setStartDate(LocalDate.parse(cursor.getString(2)));
                assessment.setEndDate(LocalDate.parse(cursor.getString(3)));
                assessment.setType(cursor.getString(4));
                assessment.setCourseId(cursor.getInt(5));
                assessments.add(assessment);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return assessments;
    }

    public List<Assessment> getAllAssessments() {
        List<Course> courses = getAllCourses();
        List<Assessment> allAssessments = new ArrayList<>();
        List<Assessment> assessments = new ArrayList<>();
        for(Course course: courses) {
            assessments = getAssessments(course.getId());
            for(Assessment assessment: assessments) {
                allAssessments.add(assessment);
            }
        }
        return allAssessments;
    }

    public Assessment getAssessment(int assessmentId) throws Exception {
        Assessment match;
        List<Assessment> assessments = getAllAssessments();
        for(Assessment assessment: assessments) {
            if(assessment.getId() == assessmentId) {
                match = assessment;
                return match;
            }
        }
        throw new Exception("No assessment found with assessmentId: " + assessmentId);
    }

    public boolean addAssessment(Assessment assessment) {
        ContentValues values = new ContentValues();
        values.put(AssessmentTable.COL_TITLE, assessment.getTitle());
        values.put(AssessmentTable.COL_START, String.valueOf(assessment.getStartDate()));
        values.put(AssessmentTable.COL_END, String.valueOf(assessment.getEndDate()));
        values.put(AssessmentTable.COL_TYPE, assessment.getType());
        values.put(AssessmentTable.COL_COURSE_ID, assessment.getCourseId());
        long id = db.insert(AssessmentTable.TABLE, null, values);
        return id != -1;
    }

    public void updateAssessment(Assessment assessment) {
        ContentValues values = new ContentValues();
        values.put(AssessmentTable.COL_TITLE, assessment.getTitle());
        values.put(AssessmentTable.COL_START, String.valueOf(assessment.getStartDate()));
        values.put(AssessmentTable.COL_END, String.valueOf(assessment.getEndDate()));
        values.put(AssessmentTable.COL_TYPE, assessment.getType());
        values.put(String.valueOf(AssessmentTable.COL_COURSE_ID), assessment.getCourseId());
        db.update(AssessmentTable.TABLE, values, AssessmentTable.COL_ID + " = " + assessment.getId(), null);
    }

    public void deleteAssessment(long assessmentId) {
        db.delete(AssessmentTable.TABLE, AssessmentTable.COL_ID + " = " + assessmentId, null);
    }

    public boolean addNote(Note note){
        ContentValues values = new ContentValues();
        values.put(NoteTable.COL_TITLE, note.getTitle());
        values.put(NoteTable.COL_BODY, note.getBody());
        values.put(NoteTable.COL_COURSE_ID, String.valueOf(note.getCourseId()));
        long id = db.insert(NoteTable.TABLE, null, values);
        return id != -1;
    }

    public List<Note> getNotes(int courseId) {
        List<Note> notes = new ArrayList<>();
        String sql = "select * from " + NoteTable.TABLE +
                " where " + NoteTable.COL_COURSE_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {String.valueOf(courseId)});
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setBody(cursor.getString(2));
                note.setCourseId(cursor.getInt(3));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return notes;
    }

    public void updateNotes(List<Note> notes) {
        if (notes != null) {
            // Delete all notes for course
            int courseId = notes.get(0).getCourseId();
            deleteNotes(courseId);
            // Add each note back to the database
            for (Note note : notes) {
                addNote(note);
            }
        }
    }

    public void deleteNotes(long courseId) {
        db.delete(NoteTable.TABLE, NoteTable.COL_COURSE_ID + " = " + courseId, null);
    }

    public List<Alarm> getCourseAlarms(int courseId) {
        List<Alarm> alarms = new ArrayList<>();
        String sql = "select * from " + CourseAlarmTable.TABLE +
                " where " + CourseAlarmTable.COL_COURSE_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {String.valueOf(courseId)});
        if (cursor.moveToFirst()) {
            do {
                Alarm alarm = new Alarm();
                alarm.setId(cursor.getInt(0));
                alarm.setTitle(cursor.getString(1));
                alarm.setTime(Long.parseLong(cursor.getString(2)));
                alarm.setDays(cursor.getInt(3));
                alarm.setType(cursor.getString(4));
                alarm.setAssocId(cursor.getInt(5));
                alarms.add(alarm);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return alarms;
    }

    public boolean addCourseAlarm(Alarm alarm){
        ContentValues values = new ContentValues();
        values.put(CourseAlarmTable.COL_TITLE, alarm.getTitle());
        values.put(CourseAlarmTable.COL_TIME, String.valueOf(alarm.getTime()));
        values.put(CourseAlarmTable.COL_DAYS, alarm.getDays());
        values.put(CourseAlarmTable.COL_TYPE, alarm.getType());
        values.put(CourseAlarmTable.COL_COURSE_ID, String.valueOf(alarm.getAssocId()));
        long id = db.insert(CourseAlarmTable.TABLE, null, values);
        return id != -1;
    }

    public void deleteCourseAlarm(int alarmId){
        db.delete(CourseAlarmTable.TABLE, CourseAlarmTable.COL_ID + " = " + alarmId, null);
    }

    public void setCourseAlarm(int courseId, Alarm newAlarm, String type) {
        // Clear old alarm of that type if the course has any
        List<Alarm> alarms = getAllCourseAlarms();
        for(Alarm alarm: alarms) {
            if(alarm.getAssocId() == courseId) {
                if(alarm.getType().equals(type)) {
                    deleteCourseAlarm(alarm.getId());
                }
            }
        }
        addCourseAlarm(newAlarm);
    }

    public List<Alarm> getAllCourseAlarms() {
        List<Alarm> alarms= new ArrayList<>();
        String orderBy = CourseAlarmTable.COL_ID + " asc";
        String sql = "select * from " + CourseAlarmTable.TABLE + " order by " + orderBy;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Alarm alarm = new Alarm();
                alarm.setId(cursor.getInt(0));
                alarm.setTitle(cursor.getString(1));
                alarm.setTime(Long.parseLong(cursor.getString(2)));
                alarm.setDays(cursor.getInt(3));
                alarm.setType(cursor.getString(4));
                alarm.setAssocId(cursor.getInt(5));
                alarms.add(alarm);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return alarms;
    }


    public List<Alarm> getAssessmentAlarms(int assessmentId) {
        List<Alarm> alarms = new ArrayList<>();
        String sql = "select * from " + AssessmentAlarmTable.TABLE +
                " where " + AssessmentAlarmTable.COL_ASSESSMENT_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {String.valueOf(assessmentId)});
        if (cursor.moveToFirst()) {
            do {
                Alarm alarm = new Alarm();
                alarm.setId(cursor.getInt(0));
                alarm.setTitle(cursor.getString(1));
                alarm.setTime(Long.parseLong(cursor.getString(2)));
                alarm.setDays(cursor.getInt(3));
                alarm.setType(cursor.getString(4));
                alarm.setAssocId(cursor.getInt(5));
                alarms.add(alarm);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return alarms;
    }

    public boolean addAssessmentAlarm(Alarm alarm){
        ContentValues values = new ContentValues();
        values.put(AssessmentAlarmTable.COL_TITLE, alarm.getTitle());
        values.put(AssessmentAlarmTable.COL_TIME, String.valueOf(alarm.getTime()));
        values.put(AssessmentAlarmTable.COL_DAYS, alarm.getDays());
        values.put(AssessmentAlarmTable.COL_TYPE, alarm.getType());
        values.put(AssessmentAlarmTable.COL_ASSESSMENT_ID, String.valueOf(alarm.getAssocId()));
        long id = db.insert(AssessmentAlarmTable.TABLE, null, values);
        return id != -1;
    }

    public void deleteAssessmentAlarm(int alarmId){
        db.delete(AssessmentAlarmTable.TABLE, AssessmentAlarmTable.COL_ID + " = " + alarmId, null);
    }

    public List<Alarm> getAllAssessmentAlarms() {
        List<Alarm> alarms= new ArrayList<>();
        String orderBy = AssessmentAlarmTable.COL_ID + " asc";
        String sql = "select * from " + AssessmentAlarmTable.TABLE + " order by " + orderBy;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Alarm alarm = new Alarm();
                alarm.setId(cursor.getInt(0));
                alarm.setTitle(cursor.getString(1));
                alarm.setTime(Long.parseLong(cursor.getString(2)));
                alarm.setDays(cursor.getInt(3));
                alarm.setType(cursor.getString(4));
                alarm.setAssocId(cursor.getInt(5));
                alarms.add(alarm);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return alarms;
    }

    public void setAssessmentAlarm(int assessmentId, Alarm newAlarm, String type) {
        // Clear old alarm of that type if the assessment has any
        List<Alarm> alarms = getAllAssessmentAlarms();
        for(Alarm alarm: alarms) {
            if(alarm.getAssocId() == assessmentId) {
                if(alarm.getType().equals(type)) {
                    deleteCourseAlarm(alarm.getId());
                }
            }
        }
        addAssessmentAlarm(newAlarm);
    }

    public void loadSampleData(SQLiteDatabase db) {

        // Create sample terms info
        String[] termTitles = { "Term 1", "Term 2", "Term 3" };
        LocalDate[] termStarts = { LocalDate.of(2023, 7, 1), LocalDate.of(2024, 1, 1), LocalDate.of(2024, 7, 1)};
        LocalDate[] termEnds = { LocalDate.of(2023, 12, 31), LocalDate.of(2024, 6, 30), LocalDate.of(2024, 12, 31)};

        // Add each term to the database
        for (int i = 0; i < 3; ++i) {
            Term term = new Term();
            term.setTitle(termTitles[i]);
            term.setStartDate(termStarts[i]);
            term.setEndDate(termEnds[i]);
            addTerm(term);
        }

        // Create sample courses info
        String[] courseTitles = { "Course 1", "Course 2", "Course 3"};
        LocalDate[] courseStarts = { LocalDate.of(2023, 7, 1), LocalDate.of(2023, 9, 1), LocalDate.of(2023, 11, 1)};
        LocalDate[] courseEnds = { LocalDate.of(2023, 8, 31), LocalDate.of(2023, 10, 31), LocalDate.of(2024, 12, 31)};
        Status[] courseStatuses = {Status.COMPLETED, Status.IN_PROGRESS, Status.PLAN_TO_TAKE};
        String[] courseNames = { "Adam Adams", "Betty Betts", "Carl Carlos"};
        String[] coursePhones = { "1231231234", "2342342345", "3453453456"};
        String[] courseEmails = { "aadams@wgu.edu", "bbetts@wgu.edu", "ccarlos@wgu.edu"};

        // Get existing term ids
        List<Term> terms = getTerms();
        int[] termIds = new int[terms.size()];
        for (int i = 0; i < termIds.length; ++i) {
            termIds[i] = terms.get(i).getId();
        }

        // Add each course to the database
        for (int i = 0; i < terms.size(); ++i) {
            Course course = new Course();
            course.setTitle(courseTitles[i]);
            course.setStartDate(courseStarts[i]);
            course.setEndDate(courseEnds[i]);
            course.setStatus(courseStatuses[i]);
            course.setInstructName(courseNames[i]);
            course.setInstructPhone(coursePhones[i]);
            course.setInstructEmail(courseEmails[i]);
            course.setTermId(termIds[i]);
            addCourse(course);
        }

        // Create sample assessments info
        String[] assessmentTitles = { "Assessment 1", "Assessment 2", "Assessment 3"};
        LocalDate[] assessmentStarts = { LocalDate.of(2023, 7, 1), LocalDate.of(2023, 9, 1), LocalDate.of(2023, 11, 1)};
        LocalDate[] assessmentEnds = { LocalDate.of(2023, 8, 31), LocalDate.of(2023, 10, 31), LocalDate.of(2024, 12, 31)};
        String[] assessmentTypes = { "performance", "objective", "objective"};

        // Get existing course ids
        List<Course> courses = getAllCourses();
        int[] courseIds = new int[courses.size()];
        for (int i = 0; i < courses.size(); ++i) {
            courseIds[i] = courses.get(i).getId();
        }

        // Add each assessment to the database
        for (int i = 0; i < courses.size(); ++i) {
            Assessment assessment = new Assessment();
            assessment.setTitle(assessmentTitles[i]);
            assessment.setStartDate(assessmentStarts[i]);
            assessment.setEndDate(assessmentEnds[i]);
            assessment.setType(assessmentTypes[i]);
            assessment.setCourseId(courseIds[i]);
            addAssessment(assessment);
        }

        // Create sample notes info
        Note note = new Note();
        note.setTitle("Sample title");
        note.setBody("Sample body");
        for(int courseId: courseIds) {
            note.setCourseId(courseId);

            // Add a note for each courseId to the database
            addNote(note);
        }
    }
}