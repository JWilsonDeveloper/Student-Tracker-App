package com.example.studenttracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDate;
import java.util.List;

public class AssessmentActivity extends AppCompatActivity implements AssessmentFragment.ReturnValues{
    int courseId;
    TrackerDatabase trackerDatabase;
    RecyclerView recyclerView;
    AssessmentAdapter assessmentAdapter;
    AssessmentFragment assessmentFragment;
    AssessmentAlarmFragment assessmentAlarmFragment;
    MenuItem addBtn;
    Assessment selectedAssessment;
    boolean fragmentUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        courseId = getIntent().getExtras().getInt("courseId");

        // Load layout and recyclerView
        setContentView(R.layout.activity_assessment);
        trackerDatabase = TrackerDatabase.getInstance(this);
        recyclerView = findViewById(R.id.assessmentRecyclerView);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        assessmentAdapter = new AssessmentActivity.AssessmentAdapter(trackerDatabase.getAssessments(courseId));
        recyclerView.setAdapter(assessmentAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        addBtn = menu.findItem(R.id.action_add);
        if(fragmentUp) {
            loadFragMenu();
        }
        return true;
    }

    private class AssessmentHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private Assessment assessment;
        private TextView textView;

        public AssessmentHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            textView = itemView.findViewById(R.id.recyclerTextView);
        }

        public void bind(Assessment assessment, int position) {
            this.assessment = assessment;
            textView.setText(assessment.getTitle());
        }

        @Override
        public void onClick(View view) {
            AssessmentDialogFragment assessmentDialogFragment = new AssessmentDialogFragment();
            assessmentDialogFragment.setDialogListener(new AssessmentDialogFragment.DialogListener() {

                // Option to view assessment alarms
                @Override
                public void viewAlarms() {

                    // Mark selected assessment
                    selectedAssessment = assessment;

                    // Start fragment
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    // Add the AssessmentAlarmFragment to the fragment container
                    assessmentAlarmFragment = new AssessmentAlarmFragment();
                    Bundle args = new Bundle();
                    args.putInt("id", assessment.getId());
                    args.putString("title", assessment.getTitle());
                    args.putString("startDate", assessment.getStartDate().toString());
                    args.putString("endDate", assessment.getEndDate().toString());
                    assessmentAlarmFragment.setArguments(args);
                    fragmentTransaction.add(R.id.fragment_container, assessmentAlarmFragment);
                    fragmentTransaction.commit();
                    loadFragMenu();
                }

                // Option to manage assessment info
                @Override
                public void manageInfo() {

                    // Mark selected assessment
                    selectedAssessment = assessment;

                    // Start AssessmentFragment
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    // Add the AssessmentFragment to the fragment container
                    assessmentFragment = new AssessmentFragment();
                    Bundle args = new Bundle();
                    args.putInt("id", assessment.getId());
                    args.putString("title", assessment.getTitle());
                    args.putString("startDate", assessment.getStartDate().toString());
                    args.putString("endDate", assessment.getEndDate().toString());
                    args.putInt("courseId", courseId);
                    args.putString("type", assessment.getType());
                    assessmentFragment.setArguments(args);
                    fragmentTransaction.add(R.id.fragment_container, assessmentFragment);
                    fragmentTransaction.commit();
                    loadFragMenu();
                }
            });
            assessmentDialogFragment.show(getSupportFragmentManager(), null);
        }
    }

    private class AssessmentAdapter extends RecyclerView.Adapter<AssessmentActivity.AssessmentHolder> {

        private List<Assessment> assessmentList;

        public AssessmentAdapter(List<Assessment> assessments) {
            assessmentList = assessments;
        }

        @Override
        public AssessmentActivity.AssessmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new AssessmentActivity.AssessmentHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(AssessmentActivity.AssessmentHolder holder, int position){
            holder.bind(assessmentList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return assessmentList.size();
        }

        public void refreshAssessments(List<Assessment> assessments) {
            assessmentList = assessments;
        }
    }


    @Override
    public void returnValues(int assessmentId, String assessmentTitle, LocalDate startDate, LocalDate endDate, int courseId, boolean toDelete, String type) {

        // Delete, add, or update based on assessmentId and toDelete
        if (toDelete) {

            // Delete was selected
            if(assessmentId == -1) {

                // Assessment isn't in database
                Toast.makeText(this, "New assessment deleted", Toast.LENGTH_SHORT).show();
            }
            else {
                try {

                    // Delete the assessment
                    trackerDatabase.deleteAssessment(assessmentId);
                    List<Assessment> assessments = trackerDatabase.getAssessments(courseId);
                    assessmentAdapter.refreshAssessments(assessments);
                    assessmentAdapter.notifyDataSetChanged();
                }
                catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (assessmentId == -1) {

            // Add was selected
            Assessment assessment = new Assessment(assessmentTitle, startDate, endDate, courseId, type);

            // Add new assessment
            trackerDatabase.addAssessment(assessment);
            List<Assessment> assessments = trackerDatabase.getAssessments(courseId);
            assessmentAdapter.refreshAssessments(assessments);
            assessmentAdapter.notifyDataSetChanged();
        }
        else if (assessmentId > 0) {

            // Update was selected
            Assessment assessment = new Assessment(assessmentId, assessmentTitle, startDate, endDate, courseId, type);

            // Update assessment
            trackerDatabase.updateAssessment(assessment);
            List<Assessment> assessments = trackerDatabase.getAssessments(courseId);
            assessmentAdapter.refreshAssessments(assessments);
            assessmentAdapter.notifyDataSetChanged();
        }

        // User goes from AssessmentFragment to AssessmentActivity
        assessmentFragment = null;
        selectedAssessment = null;
        loadActivityMenu();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {

            // Create AssessmentFragment and add it to the fragment container
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            assessmentFragment = new AssessmentFragment();
            Bundle args = new Bundle();
            args.putInt("courseId", courseId);
            assessmentFragment.setArguments(args);

            // User goes from AssessmentActivity to the AssessmentFragment
            fragmentTransaction.add(R.id.fragment_container, assessmentFragment);
            fragmentTransaction.commit();
            loadFragMenu();
        }
        else if (item.getItemId() == android.R.id.home) {
            selectedAssessment = null;
            if (assessmentFragment != null) {

                // User goes from AssessmentFragment to AssessmentActivity
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(assessmentFragment).commit();
                assessmentFragment = null;
                loadActivityMenu();
            } else if (assessmentAlarmFragment != null) {

                // User goes from AssessmentAlarmFragment to AssessmentActivity
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(assessmentAlarmFragment).commit();
                assessmentAlarmFragment = null;
                loadActivityMenu();
            }
            else {

                // User goes from AssessmentActivity to CourseFragment
                this.finish();
            }
        }
        return true;
    }

    public void loadFragMenu(){
        fragmentUp = true;
        addBtn.setVisible(false);
    }
    public void loadActivityMenu(){
        fragmentUp = false;
        addBtn.setVisible(true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(fragmentUp) {

            // Maintain assessment id if device orientation changes
            savedInstanceState.putBoolean("fragmentUp", true);
            if (selectedAssessment != null) {
                savedInstanceState.putInt("selectedId", selectedAssessment.getId());
            }
            if (assessmentFragment != null) {
                savedInstanceState.putBoolean("assessmentFragUp", true);
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getBoolean("fragmentUp")){

            // Reload course id  and fragment if fragment is up
            fragmentUp = true;
            FragmentManager fragmentManager = getSupportFragmentManager();
            if(savedInstanceState.containsKey("assessmentFragUp")) {
                assessmentFragment = (AssessmentFragment) fragmentManager.findFragmentById(R.id.fragment_container);
            }
            else {
                assessmentAlarmFragment = (AssessmentAlarmFragment) fragmentManager.findFragmentById(R.id.fragment_container);
            }
            if (savedInstanceState.containsKey("selectedId")) {
                try{
                    selectedAssessment = trackerDatabase.getAssessment(savedInstanceState.getInt("selectedId"));
                }
                catch (Exception e) {
                    Toast.makeText(this, "Error loading assessment", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}