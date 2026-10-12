package com.example.studyplanner;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Creates the notification channel once at start-up and applies the saved
 * light/dark mode preference before any activity is shown.
 */
public class StudyPlannerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.ensureChannel(this);

        AppCompatDelegate.setDefaultNightMode(SettingsManager.darkModeEnabled(this)
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
