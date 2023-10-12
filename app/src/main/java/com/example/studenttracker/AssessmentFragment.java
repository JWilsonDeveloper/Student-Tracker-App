package com.example.studenttracker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;

public class AssessmentFragment extends Fragment {
    AssessmentFragment.ReturnValues valueReturner;
    EditText titleEdit;
    TextView startTxt;
    DatePicker startDP;
    TextView endTxt;
    DatePicker endDP;
    int assessmentId;
    String title;
    LocalDate startDate;
    LocalDate endDate;
    int courseId;
    Button submitBtn;
    Button deleteBtn;
    RadioGroup radioGroup;
    RadioButton perfBtn;
    RadioButton objBtn;
    String type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_assessment, container, false);

        assessmentId = -1;

        // Find views
        titleEdit = view.findViewById(R.id.title_edit);
        startTxt = view.findViewById(R.id.start_text);
        startDP = view.findViewById(R.id.start_DP);
        endTxt = view.findViewById(R.id.end_text);
        endDP = view.findViewById(R.id.end_DP);
        submitBtn = view.findViewById(R.id.submit_button);
        deleteBtn = view.findViewById(R.id.delete_button);
        radioGroup = view.findViewById(R.id.radio_section);
        perfBtn = view.findViewById(R.id.radio_perf);
        objBtn = view.findViewById(R.id.radio_obj);


        // Get info about the assessment
        Bundle args = getArguments();
        courseId = args.getInt("courseId");

        // Altered TextViews must be manually restored when the fragment reloads
        if (savedInstanceState != null) {

            // Set start TextView and startDate
            if(savedInstanceState.containsKey("startDate")) {
                startTxt.setText(savedInstanceState.getString("startDate"));
                startDate = LocalDate.parse(startTxt.getText().toString());
            }

            // Set end date TextView
            if(savedInstanceState.containsKey("endDate")) {
                endTxt.setText(savedInstanceState.getString("endDate"));
                endDate = LocalDate.parse(endTxt.getText().toString());
            }
        }

        // args doesn't contain key "id" for new assessments
        if (args.containsKey("id")) {

            // Set assessmentId
            assessmentId = args.getInt("id");
            Log.d("Assess frag", "args.getInt(id) = " + args.getInt("id"));

            // EditTexts and DatePickers save and restore automatically when the fragment reloads.
            // So they only must be pre-loaded when the fragment opens the first time.
            if(savedInstanceState == null) {

                // Set assessment title
                titleEdit.setText(args.getString("title"));

                // Set start TextView and DatePicker
                startTxt.setText(args.getString("startDate"));
                Utility.initializeDatePicker(startTxt.getText().toString(), startDP);
                startDate = LocalDate.parse(startTxt.getText());

                // Set end date TextView and DatePicker
                endTxt.setText(args.getString("endDate"));
                Utility.initializeDatePicker(endTxt.getText().toString(), endDP);
                endDate = LocalDate.parse(endTxt.getText());

                if(args.getString("type").equals("performance")){
                    perfBtn.setChecked(true);
                }
                else {
                    objBtn.setChecked(true);
                }
            }

        }

        // Toggle visibility of start DatePicker
        startTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (startDP.getVisibility() == View.VISIBLE) {
                    startDate = Utility.getDatePickerDate(startDP);
                    startTxt.setText(startDate.toString());
                    startDP.setVisibility(View.GONE);
                    startTxt.setBackgroundResource(R.color.white);
                }
                else {
                    startDP.setVisibility(View.VISIBLE);
                    startTxt.setBackgroundResource(R.color.lightGreen);
                    startTxt.setText(R.string.dateClick);
                }
            }
        });

        // Toggle visibility of end DatePicker
        endTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (endDP.getVisibility() == View.VISIBLE) {
                    endDate = Utility.getDatePickerDate(endDP);
                    endTxt.setText(endDate.toString());
                    endDP.setVisibility(View.GONE);
                    endTxt.setBackgroundResource(R.color.white);
                }
                else {
                    endDP.setVisibility(View.VISIBLE);
                    endTxt.setBackgroundResource(R.color.lightGreen);
                    endTxt.setText(R.string.dateClick);
                }
            }
        });

        // Submit button
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                title = titleEdit.getText().toString();
                finalizeFields();
                if(title.isEmpty() || startDate == null || endDate == null || (perfBtn.isChecked() == false && objBtn.isChecked() == false)) {
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
                else if (valueReturner != null) {
                    valueReturner.returnValues(assessmentId, title, startDate, endDate, courseId,false, type);
                    if (getActivity() != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().remove(AssessmentFragment.this).commit();
                    }
                }
            }
        });

        // Delete button
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                finalizeFields();
                if (valueReturner != null) {
                    valueReturner.returnValues(assessmentId, title, startDate, endDate, courseId,true, type);
                    if (getActivity() != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().remove(AssessmentFragment.this).commit();
                    }
                }
            }
        });
        return view;
    }

    public interface ReturnValues {
        void returnValues(int assessmentId, String assessmentTitle, LocalDate startDate, LocalDate endDate, int courseId, boolean toDelete, String type);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            valueReturner = (AssessmentFragment.ReturnValues) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    private void finalizeFields(){
        title = titleEdit.getText().toString();
        if(perfBtn.isChecked()) {
            type = "performance";
        }
        else {
            type = "objective";
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save startDate
        if(startDate != null) {
            startDate = Utility.getDatePickerDate(startDP);
            savedInstanceState.putString("startDate", startDate.toString());
        }

        // Save endDate
        if(endDate != null) {
            endDate = Utility.getDatePickerDate(endDP);
            savedInstanceState.putString("endDate", endDate.toString());
        }
    }
}