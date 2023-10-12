package com.example.studenttracker;

import android.widget.DatePicker;
import java.time.LocalDate;
import java.util.Calendar;

public class Utility {

    public static void initializeDatePicker(String dateString, DatePicker datePicker) {
        LocalDate date = LocalDate.parse(dateString);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, date.getYear());
        cal.set(Calendar.MONTH, date.getMonthValue() - 1);
        cal.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
    }

    public static LocalDate getDatePickerDate(DatePicker datePicker) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        LocalDate date = LocalDate.of(year, month + 1, day);

        return date;
    }

    public static int getStatusIndex(Status status) {
        switch (status){
            case IN_PROGRESS:
                return 0;
            case COMPLETED :
                return 1;
            case DROPPED:
                return 2;
            case PLAN_TO_TAKE:
                return 3;
        }
        return 0;
    }
}
