package com.example.studenttracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.time.LocalDate;
import java.util.List;

public class CourseActivity extends AppCompatActivity implements CourseFragment.ReturnValues {
    int termId;
    TrackerDatabase trackerDatabase;
    RecyclerView recyclerView;
    CourseAdapter courseAdapter;
    CourseFragment courseFragment;
    CourseAlarmFragment courseAlarmFragment;
    MenuItem assessmentsBtn;
    MenuItem addBtn;
    Course selectedCourse;
    boolean fragmentUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        termId = getIntent().getExtras().getInt("termId");

        // Load layout and recyclerView
        setContentView(R.layout.activity_course);
        trackerDatabase = TrackerDatabase.getInstance(this);
        recyclerView = findViewById(R.id.courseRecyclerView);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        courseAdapter = new CourseActivity.CourseAdapter(trackerDatabase.getCourses(termId));
        recyclerView.setAdapter(courseAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        assessmentsBtn = menu.findItem(R.id.action_assessments);
        addBtn = menu.findItem(R.id.action_add);
        if (fragmentUp) {
            loadFragMenu();
        }
        return true;
    }

    private class CourseHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Course course;
        private TextView textView;

        public CourseHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            textView = itemView.findViewById(R.id.recyclerTextView);
        }

        public void bind(Course course, int position) {
            this.course = course;
            textView.setText(course.getTitle());
        }

        @Override
        public void onClick(View view) {
            CourseDialogFragment courseDialogFragment = new CourseDialogFragment();
            courseDialogFragment.setDialogListener(new CourseDialogFragment.DialogListener() {

                // Option to view course alarms
                @Override
                public void viewAlarms() {

                    // Mark selected course
                    selectedCourse = course;

                    // Start fragment transaction
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    // Add the CourseAlarmFragment to the fragment container
                    courseAlarmFragment = new CourseAlarmFragment();
                    Bundle args = new Bundle();
                    args.putInt("id", course.getId());
                    args.putString("title", course.getTitle());
                    args.putString("startDate", course.getStartDate().toString());
                    args.putString("endDate", course.getEndDate().toString());
                    courseAlarmFragment.setArguments(args);
                    fragmentTransaction.add(R.id.fragment_container, courseAlarmFragment);
                    fragmentTransaction.commit();
                    loadFragMenu();
                }

                // Option to manage course info
                @Override
                public void manageInfo() {

                    // Mark selected course
                    selectedCourse = course;

                    // Start fragment transaction
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    // Add the CourseFragment to the fragment container
                    courseFragment = new CourseFragment();
                    Bundle args = new Bundle();
                    args.putInt("id", course.getId());
                    args.putString("title", course.getTitle());
                    args.putString("startDate", course.getStartDate().toString());
                    args.putString("endDate", course.getEndDate().toString());
                    args.putString("status", course.getStatus().toString());
                    args.putString("instructName", course.getInstructName());
                    args.putString("instructPhone", course.getInstructPhone());
                    args.putString("instructEmail", course.getInstructEmail());

                    // Put notes into args
                    List<Note> notes = trackerDatabase.getNotes(course.getId());
                    if (notes != null) {
                        args.putInt("numNotes", notes.size());
                        for (int i = 0; i < notes.size(); i++) {
                            args.putString("noteTitle" + i, notes.get(i).getTitle());
                            args.putString("noteBody" + i, notes.get(i).getBody());
                        }
                    }
                    else {
                        args.putInt("numNotes", 0);
                    }
                    args.putInt("termId", termId);
                    courseFragment.setArguments(args);
                    fragmentTransaction.add(R.id.fragment_container, courseFragment);
                    fragmentTransaction.commit();
                    loadFragMenu();
                }
            });

            // Display Options
            courseDialogFragment.show(getSupportFragmentManager(), null);
        }
    }

    private class CourseAdapter extends RecyclerView.Adapter<CourseActivity.CourseHolder> {

        private List<Course> courseList;

        public CourseAdapter(List<Course> courses) {
            courseList = courses;
        }

        @Override
        public CourseActivity.CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new CourseActivity.CourseHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CourseActivity.CourseHolder holder, int position) {
            holder.bind(courseList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return courseList.size();
        }

        public void refreshCourses(List<Course> courses) {
            courseList = courses;
        }
    }

    @Override
    public void returnValues(int courseId, String courseTitle, LocalDate startDate, LocalDate endDate, Status status, String instructName, String instructPhone, String instructEmail, List<Note> notes, int termId, boolean toDelete) {

        // Delete, add, or update based on courseId and toDelete
        if (toDelete) {

            // Delete was selected
            if (courseId == -1) {

                // Course isn't in database
                Toast.makeText(this, "New course deleted", Toast.LENGTH_SHORT).show();
            } else {
                try {

                    // Delete the course
                    trackerDatabase.deleteCourse(courseId);
                    List<Course> courses = trackerDatabase.getCourses(termId);
                    courseAdapter.refreshCourses(courses);
                    courseAdapter.notifyDataSetChanged();

                    // Delete the notes associated with the course
                    trackerDatabase.deleteNotes(courseId);
                }
                catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (courseId == -1) {

            // Add was selected
            Course course = new Course(courseTitle, startDate, endDate, status, instructName, instructPhone, instructEmail, termId);

            // Add new course
            int newCourseId = (int) trackerDatabase.addCourse(course);
            List<Course> courses = trackerDatabase.getCourses(termId);
            courseAdapter.refreshCourses(courses);
            courseAdapter.notifyDataSetChanged();

            // Add notes
            for (Note note : notes) {
                note.setCourseId(newCourseId);
                trackerDatabase.addNote(note);
            }
        } else if (courseId > 0) {

            // Update was selected
            Course course = new Course(courseId, courseTitle, startDate, endDate, status, instructName, instructPhone, instructEmail, termId);

            // Update course
            trackerDatabase.updateCourse(course);
            List<Course> courses = trackerDatabase.getCourses(termId);
            courseAdapter.refreshCourses(courses);
            courseAdapter.notifyDataSetChanged();

            // Update notes
            trackerDatabase.updateNotes(notes);
        }

        // User goes from CourseFragment to CourseActivity
        courseFragment = null;
        selectedCourse = null;
        loadActivityMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {

            // Create CourseFragment and add it to the fragment container
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            courseFragment = new CourseFragment();
            Bundle args = new Bundle();
            args.putInt("termId", termId);
            courseFragment.setArguments(args);

            // User goes from CourseActivity to the CourseFragment
            fragmentTransaction.add(R.id.fragment_container, courseFragment);
            fragmentTransaction.commit();
            loadFragMenu();
        }
        else if (item.getItemId() == android.R.id.home) {
            selectedCourse = null;
            if (courseFragment != null) {

                // User goes from CourseFragment to CourseActivity
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(courseFragment).commit();
                courseFragment = null;
                loadActivityMenu();
            }
            else if (courseAlarmFragment != null) {

                // User goes from CourseAlarmFragment to CourseActivity
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(courseAlarmFragment).commit();
                courseAlarmFragment = null;
                loadActivityMenu();
            }
            else {

                // User goes from CourseActivity to TermFragment
                this.finish();
            }
        }
        else if (item.getItemId() == R.id.action_assessments) {

            // User goes from CourseFragment to AssessmentActivity
            Intent intent = new Intent(this, AssessmentActivity.class);
            intent.putExtra("courseId", selectedCourse.getId());
            startActivity(intent);
        }

        return true;
    }

    public void loadFragMenu() {
        fragmentUp = true;
        addBtn.setVisible(false);
        if (selectedCourse != null) {
            assessmentsBtn.setVisible(true);
        }
    }

    public void loadActivityMenu() {
        fragmentUp = false;
        addBtn.setVisible(true);
        assessmentsBtn.setVisible(false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (fragmentUp) {

            // Maintain course id if device orientation changes
            savedInstanceState.putBoolean("fragmentUp", true);
            if (selectedCourse != null) {
                savedInstanceState.putInt("selectedId", selectedCourse.getId());
            }
            if (courseFragment != null) {
                savedInstanceState.putBoolean("courseFragUp", true);
            }
        }
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean("fragmentUp")) {

            // Reload course id and fragment if fragment is up
            fragmentUp = true;
            FragmentManager fragmentManager = getSupportFragmentManager();
            if(savedInstanceState.containsKey("courseFragUp")) {
                courseFragment = (CourseFragment) fragmentManager.findFragmentById(R.id.fragment_container);
            }
            else {
                courseAlarmFragment = (CourseAlarmFragment) fragmentManager.findFragmentById(R.id.fragment_container);
            }
            if (savedInstanceState.containsKey("selectedId")) {
                try {
                    selectedCourse = trackerDatabase.getCourse(savedInstanceState.getInt("selectedId"));
                }
                catch (Exception e) {
                    Toast.makeText(this, "Error loading course", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}