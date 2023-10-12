package com.example.studenttracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class CourseDialogFragment extends DialogFragment {

    private DialogListener dialogListener;

    public interface DialogListener {
        void viewAlarms();
        void manageInfo();
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
            .setMessage(R.string.course_choice)
            .setPositiveButton(R.string.view_alarms, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialogListener.viewAlarms();
                }
            })
            .setNegativeButton(R.string.edit_course, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialogListener.manageInfo();
                }
            })
            .create();
    }

    public void setDialogListener(DialogListener dialogListener){
        this.dialogListener = dialogListener;
    }
}
