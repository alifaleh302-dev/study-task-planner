package com.example.studyplanner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import java.util.Calendar;

/**
 * Small helpers that wrap the platform date and time pickers so the add and
 * edit screens do not repeat the same dialog code.
 */
public final class DateTimePicker {

    private DateTimePicker() {
    }

    /** Shows a date picker initialised with {@code current} and reports the result. */
    public static void showDatePicker(Activity activity, long current, OnDateSelected callback) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(current);
        DatePickerDialog dialog = new DatePickerDialog(activity,
                (view, year, month, dayOfMonth) -> {
                    Calendar result = Calendar.getInstance();
                    result.setTimeInMillis(current);
                    result.set(year, month, dayOfMonth);
                    callback.onDateSelected(result.getTimeInMillis());
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    /** Shows a time picker initialised with {@code current} and reports the result. */
    public static void showTimePicker(Activity activity, long current, OnTimeSelected callback) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(current);
        TimePickerDialog dialog = new TimePickerDialog(activity,
                (view, hourOfDay, minute) -> {
                    Calendar result = Calendar.getInstance();
                    result.setTimeInMillis(current);
                    result.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    result.set(Calendar.MINUTE, minute);
                    result.set(Calendar.SECOND, 0);
                    callback.onTimeSelected(result.getTimeInMillis());
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false);
        dialog.show();
    }

    /** Callback for a chosen date. */
    public interface OnDateSelected {
        void onDateSelected(long millis);
    }

    /** Callback for a chosen time. */
    public interface OnTimeSelected {
        void onTimeSelected(long millis);
    }
}
