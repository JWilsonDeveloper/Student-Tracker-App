package com.example.studenttracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;

public class TermFragment extends Fragment {
    private EditText titleEdit;
    private TextView startTxt;
    private TextView endTxt;
    private DatePicker startDP;
    private DatePicker endDP;
    private int termId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Button submitBtn;
    private Button deleteBtn;
    private ReturnValues valueReturner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_term, container, false);

        termId = -1;

        // Find views
        titleEdit = view.findViewById(R.id.title_edit);
        startTxt = view.findViewById(R.id.start_text);
        startDP = view.findViewById(R.id.start_DP);
        endTxt = view.findViewById(R.id.end_text);
        endDP = view.findViewById(R.id.end_DP);
        submitBtn = view.findViewById(R.id.submit_button);
        deleteBtn = view.findViewById(R.id.delete_button);

        // Get info about the term
        Bundle args = getArguments();

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

        // args is null for new terms
        if (args != null) {
            termId = args.getInt("id");

            // EditTexts and DatePickers save and restore automatically when the fragment reloads.
            // So they only must be pre-loaded when the fragment opens the first time.
            if(savedInstanceState == null) {

                // Set term title
                titleEdit.setText(args.getString("title"));

                // Set start TextView and DatePicker
                startTxt.setText(args.getString("startDate"));
                Utility.initializeDatePicker(startTxt.getText().toString(), startDP);
                startDate = LocalDate.parse(startTxt.getText());

                // Set end date TextView and DatePicker
                endTxt.setText(args.getString("endDate"));
                Utility.initializeDatePicker(endTxt.getText().toString(), endDP);
                endDate = LocalDate.parse(endTxt.getText());
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
                finalizeFields();
                if(title.isEmpty() || startDate == null || endDate == null) {
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
                else if (valueReturner != null) {
                    valueReturner.returnValues(termId, title, startDate, endDate, false);
                    if (getActivity() != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().remove(TermFragment.this).commit();
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
                    valueReturner.returnValues(termId, title, startDate, endDate, true);
                    if (getActivity() != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().remove(TermFragment.this).commit();
                    }
                }
            }
        });

        return view;
    }

    public interface ReturnValues {
        void returnValues(int termId, String termTitle, LocalDate startDate, LocalDate endDate, boolean toDelete);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            valueReturner = (ReturnValues) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    private void finalizeFields(){
        title = titleEdit.getText().toString();
        startDate = Utility.getDatePickerDate(startDP);
        endDate = Utility.getDatePickerDate(endDP);
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