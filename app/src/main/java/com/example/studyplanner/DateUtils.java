package com.example.studyplanner;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Small formatting helpers shared by the activities and adapters so that
 * dates, times and priority labels are written the same way everywhere.
 */
public final class DateUtils {

    private static final Locale ARABIC = new Locale("ar");
    private static final String DATE_PATTERN = "yyyy/MM/dd";
    private static final String TIME_PATTERN = "hh:mm a";
    private static final String DATE_TIME_PATTERN = "yyyy/MM/dd hh:mm a";

    private DateUtils() {
    }

    public static String formatDate(long millis) {
        if (millis <= 0) {
            return "";
        }
        return new SimpleDateFormat(DATE_PATTERN, ARABIC).format(new Date(millis));
    }

    public static String formatTime(long millis) {
        if (millis <= 0) {
            return "";
        }
        return new SimpleDateFormat(TIME_PATTERN, ARABIC).format(new Date(millis));
    }

    public static String formatDateTime(long millis) {
        if (millis <= 0) {
            return "";
        }
        return new SimpleDateFormat(DATE_TIME_PATTERN, ARABIC).format(new Date(millis));
    }

    /** Returns the Arabic label of a priority value. */
    public static String priorityName(Context context, int priority) {
        switch (priority) {
            case Task.PRIORITY_HIGH:
                return context.getString(R.string.priority_high);
            case Task.PRIORITY_MEDIUM:
                return context.getString(R.string.priority_medium);
            default:
                return context.getString(R.string.priority_low);
        }
    }

    /** Returns the colour resource that matches a priority value. */
    public static int priorityColor(int priority) {
        switch (priority) {
            case Task.PRIORITY_HIGH:
                return R.color.priorityHigh;
            case Task.PRIORITY_MEDIUM:
                return R.color.priorityMedium;
            default:
                return R.color.priorityLow;
        }
    }
}
