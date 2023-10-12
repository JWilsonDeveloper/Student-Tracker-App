package com.example.studenttracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CourseFragment extends Fragment {
    CourseFragment.ReturnValues valueReturner;
    LinearLayout notesContainer;
    EditText titleEdit;
    TextView startTxt;
    DatePicker startDP;
    TextView endTxt;
    DatePicker endDP;
    Spinner statusSpinner;
    EditText nameEdit;
    EditText phoneEdit;
    EditText emailEdit;
    int courseId;
    String title;
    LocalDate startDate;
    LocalDate endDate;
    Status status;
    String instructName;
    String instructPhone;
    String instructEmail;
    int termId;
    ImageButton addNoteBtn;
    Button submitBtn;
    Button deleteBtn;
    List<Note> endNotes;
    List<int[]> idArrays;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        courseId = -1;

        // Find views
        titleEdit = view.findViewById(R.id.title_edit);
        startTxt = view.findViewById(R.id.start_text);
        startDP = view.findViewById(R.id.start_DP);
        endTxt = view.findViewById(R.id.end_text);
        endDP = view.findViewById(R.id.end_DP);
        statusSpinner = view.findViewById(R.id.status_spinner);
        nameEdit = view.findViewById(R.id.name_edit);
        phoneEdit = view.findViewById(R.id.phone_edit);
        emailEdit = view.findViewById(R.id.email_edit);
        notesContainer = view.findViewById(R.id.notes_container);
        addNoteBtn = view.findViewById(R.id.add_button);
        submitBtn = view.findViewById(R.id.submit_button);
        deleteBtn = view.findViewById(R.id.delete_button);

        // Initialize
        idArrays = new ArrayList<>();
        List<Note> startNotes = new ArrayList<>();

        // Get info about the course
        Bundle args = getArguments();
        termId = args.getInt("termId");

        // Altered TextViews and the programmatically generated Notes must be manually restored when the fragment reloads
        if (savedInstanceState != null) {

            // Set start TextView
            if(savedInstanceState.containsKey("startDate")) {
                startTxt.setText(savedInstanceState.getString("startDate"));
                startDate = LocalDate.parse(startTxt.getText().toString());
            }

            // Set end date TextView
            if(savedInstanceState.containsKey("endDate")) {
                endTxt.setText(savedInstanceState.getString("endDate"));
                endDate = LocalDate.parse(endTxt.getText().toString());
            }

            // Set args' notes to match notes from savedInstanceState
            int numNotes = savedInstanceState.getInt("numNotes");
            args.putInt("numNotes", savedInstanceState.getInt("numNotes"));
            for (int i = 0; i < numNotes; ++i) {
                String noteTitle = savedInstanceState.getString("noteTitle" + i);
                args.putString("noteTitle" + i, noteTitle);
                String noteBody = savedInstanceState.getString("noteBody" + i);
                args.putString("noteBody" + i, noteBody);
            }
        }

        // args doesn't contain key "id" for new courses
        if (args.containsKey("id")) {

            // Set courseId
            courseId = args.getInt("id");

            // EditTexts, DatePickers, and Spinners save and restore automatically when the fragment reloads.
            // So they only must be pre-loaded when the fragment opens the first time.
            if(savedInstanceState == null) {

                // Set course title
                titleEdit.setText(args.getString("title"));

                // Set start TextView and DatePicker
                startTxt.setText(args.getString("startDate"));
                Utility.initializeDatePicker(startTxt.getText().toString(), startDP);
                startDate = LocalDate.parse(startTxt.getText());

                // Set end date TextView and DatePicker
                endTxt.setText(args.getString("endDate"));
                Utility.initializeDatePicker(endTxt.getText().toString(), endDP);
                endDate = LocalDate.parse(endTxt.getText());

                // Set status spinner
                statusSpinner.setSelection(Utility.getStatusIndex(Status.valueOf(args.getString("status"))));

                // Set instructor EditTexts
                nameEdit.setText(args.getString("instructName"));
                phoneEdit.setText(args.getString("instructPhone"));
                emailEdit.setText(args.getString("instructEmail"));
            }

            // Load note views and listeners
            int numNotes = args.getInt("numNotes");
            if (numNotes > 0) {
                for (int i = 0; i < numNotes; ++i) {
                    Note note = new Note();
                    note.setTitle(args.getString("noteTitle" + i));
                    note.setBody(args.getString("noteBody" + i));
                    startNotes.add(note);
                }
                for (Note note : startNotes) {
                    View noteSection = inflater.inflate(R.layout.notes_layout, null);
                    int[] idArray = new int[4];
                    idArray[0] = View.generateViewId();
                    noteSection.findViewById(R.id.note_title).setId(idArray[0]);
                    idArray[1] = View.generateViewId();
                    noteSection.findViewById(R.id.delete_note_button).setId(idArray[1]);
                    idArray[2] = View.generateViewId();
                    noteSection.findViewById(R.id.share_note_button).setId(idArray[2]);
                    idArray[3] = View.generateViewId();
                    noteSection.findViewById(R.id.note_body).setId(idArray[3]);
                    idArrays.add(idArray);
                    notesContainer.addView(noteSection);
                    EditText noteTitleEdit = noteSection.findViewById(idArray[0]);
                    noteTitleEdit.setText(note.getTitle());
                    ImageButton deleteNoteBtn = noteSection.findViewById(idArray[1]);
                    deleteNoteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            notesContainer.removeView(noteSection);
                            idArrays.remove(idArray);
                        }
                    });
                    Button shareNoteBtn = noteSection.findViewById(idArray[2]);
                    shareNoteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditText titleText = noteSection.findViewById(idArray[0]);
                            EditText bodyText = noteSection.findViewById(idArray[3]);
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT, titleText.getText().toString());
                            intent.putExtra(Intent.EXTRA_TEXT, bodyText.getText().toString());

                            // If at least one app can handle intent, allow user to choose
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                Intent chooser = intent.createChooser(intent, "Share Note");
                                startActivity(chooser);
                            }
                        }
                    });
                    EditText noteBodyEdit = noteSection.findViewById(idArray[3]);
                    noteBodyEdit.setText(note.getBody());
                }
            }
        }

        // Toggle visibility of start DatePicker
        startTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            public void onClick(View view) {
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

        // Add Note button
        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View noteSection = inflater.inflate(R.layout.notes_layout, null);
                int[] idArray = new int[4];
                idArray[0] = View.generateViewId();
                noteSection.findViewById(R.id.note_title).setId(idArray[0]);
                idArray[1] = View.generateViewId();
                noteSection.findViewById(R.id.delete_note_button).setId(idArray[1]);
                idArray[2] = View.generateViewId();
                noteSection.findViewById(R.id.share_note_button).setId(idArray[2]);
                idArray[3] = View.generateViewId();
                noteSection.findViewById(R.id.note_body).setId(idArray[3]);
                idArrays.add(idArray);
                notesContainer.addView(noteSection);
                ImageButton deleteNoteBtn = noteSection.findViewById(idArray[1]);

                // Delete note button
                deleteNoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notesContainer.removeView(noteSection);
                        idArrays.remove(idArray);
                    }
                });

                // Share note button
                Button shareNoteBtn = noteSection.findViewById(idArray[2]);
                shareNoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText titleText = noteSection.findViewById(idArray[0]);
                        EditText bodyText = noteSection.findViewById(idArray[3]);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, titleText.getText().toString());
                        intent.putExtra(Intent.EXTRA_TEXT, bodyText.getText().toString());

                        // If at least one app can handle intent, allow user to choose
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            Intent chooser = intent.createChooser(intent, "Share Note");
                            startActivity(chooser);
                        }
                    }
                });
            }
        });


        // Submit button
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizeFields();
                if (title.isEmpty() || startDate == null || endDate == null || status == null || instructName.isEmpty() || instructPhone.isEmpty() || instructEmail.isEmpty()) {
                    Toast.makeText(getContext(), "All fields are required (excluding notes)", Toast.LENGTH_SHORT).show();
                }
                else if (valueReturner != null) {
                    valueReturner.returnValues(courseId, title, startDate, endDate, status, instructName, instructPhone, instructEmail, endNotes, termId, false);
                    if (getActivity() != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().remove(CourseFragment.this).commit();
                    }
                }
            }
        });

        // Delete button
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizeFields();
                if (valueReturner != null) {
                    valueReturner.returnValues(courseId, title, startDate, endDate, status, instructName, instructPhone, instructEmail, endNotes, termId, true);
                    if (getActivity() != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().remove(CourseFragment.this).commit();
                    }
                }
            }
        });

        return view;
    }

    public interface ReturnValues {
        void returnValues(int courseId, String courseTitle, LocalDate startDate, LocalDate endDate, Status status, String instructName, String instructPhone, String instructEmail, List<Note> notes, int termId, boolean toDelete);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            valueReturner = (CourseFragment.ReturnValues) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    private void finalizeFields() {
        title = titleEdit.getText().toString();
        status = Status.valueOf(statusSpinner.getSelectedItem().toString());
        startDate = Utility.getDatePickerDate(startDP);
        endDate = Utility.getDatePickerDate(endDP);
        instructName = nameEdit.getText().toString();
        instructPhone = phoneEdit.getText().toString();
        instructEmail = emailEdit.getText().toString();
        endNotes = new ArrayList<>();
        for (int i = 0; i < idArrays.size(); ++i) {
            int[] idArray = idArrays.get(i);
            Note note = new Note();
            EditText noteTitleEdit = getView().findViewById(idArray[0]);
            Log.d("finalizeFields " + i, noteTitleEdit.getText().toString());
            note.setTitle(noteTitleEdit.getText().toString());
            EditText noteBodyEdit = getView().findViewById(idArray[3]);
            Log.d("finalizeFields " + i, noteBodyEdit.getText().toString());
            note.setBody(noteBodyEdit.getText().toString());
            note.setCourseId(courseId);
            endNotes.add(note);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save notes
        endNotes = new ArrayList<>();
        for (int i = 0; i < idArrays.size(); ++i) {
            int[] idArray = idArrays.get(i);
            Note note = new Note();
            EditText noteTitleEdit = getView().findViewById(idArray[0]);
            note.setTitle(noteTitleEdit.getText().toString());
            EditText noteBodyEdit = getView().findViewById(idArray[3]);
            note.setBody(noteBodyEdit.getText().toString());
            note.setCourseId(courseId);
            endNotes.add(note);
        }
        savedInstanceState.putInt("numNotes", endNotes.size());
        for(int i = 0; i < endNotes.size(); ++i) {
            savedInstanceState.putString("noteTitle" + i, endNotes.get(i).getTitle().toString());
            savedInstanceState.putString("noteBody" + i, endNotes.get(i).getBody().toString());
        }

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