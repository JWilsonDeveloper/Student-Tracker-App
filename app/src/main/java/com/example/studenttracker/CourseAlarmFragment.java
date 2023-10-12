package com.example.studenttracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.fragment.app.Fragment;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class CourseAlarmFragment extends Fragment {

    TextView alertText;
    TextView titleText;
    TextView startTxt;
    TextView endTxt;
    GridLayout alert_layout;
    ToggleButton startAlertToggle;
    Spinner startAlertSpinner;
    ToggleButton endAlertToggle;
    Spinner endAlertSpinner;
    int courseId;
    LocalDate startDate;
    LocalDate endDate;

    Button startBtn;
    Button endBtn;
    TrackerDatabase trackerDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_alarm, container, false);

        // Find views
        titleText = view.findViewById(R.id.title_text);
        startTxt = view.findViewById(R.id.start_text);
        endTxt = view.findViewById(R.id.end_text);
        alert_layout = view.findViewById(R.id.alert_layout);
        startAlertToggle = view.findViewById(R.id.start_alert_toggle);
        startAlertSpinner = view.findViewById(R.id.start_alert_spinner);
        endAlertToggle = view.findViewById(R.id.end_alert_toggle);
        endAlertSpinner = view.findViewById(R.id.end_alert_spinner);
        alertText = view.findViewById(R.id.alert_text);
        startBtn = view.findViewById(R.id.start_button);
        endBtn = view.findViewById(R.id.end_button);

        trackerDatabase = TrackerDatabase.getInstance(getContext());

        Bundle args = getArguments();

        // Set courseId
        courseId = args.getInt("id");

        // Set course title
        titleText.setText(args.getString("title"));

        // Set start TextView and DatePicker
        startTxt.setText(args.getString("startDate"));
        startDate = LocalDate.parse(startTxt.getText());

        // Set end date TextView and DatePicker
        endTxt.setText(args.getString("endDate"));
        endDate = LocalDate.parse(endTxt.getText());

        List<Alarm> alarms = trackerDatabase.getCourseAlarms(courseId);
        Alarm startAlarm = null;
        Alarm endAlarm = null;
        for(Alarm alarm: alarms){
            if(alarm.getType().equals("start")) {
                startAlarm = alarm;
                startAlertSpinner.setSelection(alarm.getDays() - 1);
            }
            if(alarm.getType().equals("end")) {
                endAlarm = alarm;
                endAlertSpinner.setSelection(alarm.getDays() - 1);
            }
        }
        if(startAlarm == null) {
            startAlertToggle.setChecked(false);
        }
        else {
            startAlertToggle.setChecked(true);
        }
        if(endAlarm == null) {
            endAlertToggle.setChecked(false);
        }
        else {
            endAlertToggle.setChecked(true);
        }

        // Start alarm submitted
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean deleteOld = true;
                if (startAlertToggle.isChecked()) {
                    int days = Integer.parseInt(startAlertSpinner.getSelectedItem().toString());
                    LocalDate alarmDate = startDate.minusDays(days);
                    long alarmMillis = alarmDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    long todayMillis = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    if (alarmMillis > todayMillis) {
                        Alarm alarm = new Alarm(titleText.getText().toString(), alarmMillis, days, "start", courseId);
                        trackerDatabase.setCourseAlarm(alarm.getAssocId(), alarm, "start");
                        Toast.makeText(getContext(), "Alarm scheduled for " + days + " day(s) before " + startDate.toString(), Toast.LENGTH_SHORT).show();
                        deleteOld = false;
                    }
                    else {
                        Toast.makeText(getContext(), "Alarms cannot be set for a day earlier than tomorrow", Toast.LENGTH_SHORT).show();
                        startAlertToggle.setChecked(false);
                        deleteOld = true;
                    }
                }
                else {
                    Toast.makeText(getContext(), "Alarm saved", Toast.LENGTH_SHORT).show();
                }
                if(deleteOld) {
                    // Delete old start alarm
                    List<Alarm> alarms = trackerDatabase.getCourseAlarms(courseId);
                    for(Alarm alarm: alarms) {
                        if(alarm.getType().equals("start")) {
                            trackerDatabase.deleteCourseAlarm(alarm.getId());
                        }
                    }
                }
            }
        });

        // End alarm submitted
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean deleteOld = true;
                if (endAlertToggle.isChecked()) {
                    int days = Integer.parseInt(endAlertSpinner.getSelectedItem().toString());
                    LocalDate alarmDate = endDate.minusDays(days);
                    long alarmMillis = alarmDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    long todayMillis = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    if (alarmMillis > todayMillis) {
                        Alarm alarm = new Alarm(titleText.getText().toString(), alarmMillis, days, "end", courseId);
                        trackerDatabase.setCourseAlarm(alarm.getAssocId(), alarm, "end");
                        Toast.makeText(getContext(), "Alarm scheduled for " + days + " day(s) before " + endDate.toString(), Toast.LENGTH_SHORT).show();
                        deleteOld = false;
                    }
                    else {
                        Toast.makeText(getContext(), "Alarms cannot be set for a day earlier than tomorrow", Toast.LENGTH_SHORT).show();
                        endAlertToggle.setChecked(false);
                        deleteOld = true;
                    }
                }
                else {
                    Toast.makeText(getContext(), "Alarm saved", Toast.LENGTH_SHORT).show();
                }
                if(deleteOld) {
                    // Delete old end alarm
                    List<Alarm> alarms = trackerDatabase.getCourseAlarms(courseId);
                    for(Alarm alarm: alarms) {
                        if(alarm.getType().equals("end")) {
                            trackerDatabase.deleteCourseAlarm(alarm.getId());
                        }
                    }
                }
            }
        });

        return view;
    }
}
