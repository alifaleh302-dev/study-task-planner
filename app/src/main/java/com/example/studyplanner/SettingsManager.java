package com.example.studyplanner;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stores the simple user settings with {@link SharedPreferences}:
 * whether notifications are enabled, whether new tasks should have the
 * reminder enabled by default, and whether dark mode is selected.
 */
public final class SettingsManager {

    private static final String PREFS_NAME = "study_planner_settings";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_DEFAULT_REMINDER = "default_reminder";
    private static final String KEY_DARK_MODE = "dark_mode";

    private SettingsManager() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean notificationsEnabled(Context context) {
        return prefs(context).getBoolean(KEY_NOTIFICATIONS, true);
    }

    public static void setNotificationsEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }

    public static boolean defaultReminderEnabled(Context context) {
        return prefs(context).getBoolean(KEY_DEFAULT_REMINDER, false);
    }

    public static void setDefaultReminderEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_DEFAULT_REMINDER, enabled).apply();
    }

    public static boolean darkModeEnabled(Context context) {
        return prefs(context).getBoolean(KEY_DARK_MODE, false);
    }

    public static void setDarkModeEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }
}
