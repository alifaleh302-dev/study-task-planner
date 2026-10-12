package com.example.studyplanner;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.CompoundButton;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

/**
 * Stores the simple user settings with {@link SettingsManager} and applies the
 * dark mode immediately. Also asks for the notification permission on the
 * Android versions that require it.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATIONS = 101;

    private SwitchCompat switchNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchNotifications = findViewById(R.id.switchNotifications);
        SwitchCompat switchDefaultReminder = findViewById(R.id.switchDefaultReminder);
        SwitchCompat switchDarkMode = findViewById(R.id.switchDarkMode);
        TextView textVersion = findViewById(R.id.textVersion);

        textVersion.setText(getString(R.string.settings_version, BuildConfig.VERSION_NAME));

        switchNotifications.setChecked(SettingsManager.notificationsEnabled(this));
        switchDefaultReminder.setChecked(SettingsManager.defaultReminderEnabled(this));
        switchDarkMode.setChecked(SettingsManager.darkModeEnabled(this));

        switchNotifications.setOnCheckedChangeListener(this::onNotificationsChanged);
        switchDefaultReminder.setOnCheckedChangeListener((button, checked) ->
                SettingsManager.setDefaultReminderEnabled(this, checked));
        switchDarkMode.setOnCheckedChangeListener(this::onDarkModeChanged);

        findViewById(R.id.buttonAbout).setOnClickListener(
                v -> startActivity(new Intent(this, AboutActivity.class)));
    }

    private void onNotificationsChanged(CompoundButton button, boolean checked) {
        SettingsManager.setNotificationsEnabled(this, checked);
        if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ needs an explicit permission before showing notifications.
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.POST_NOTIFICATIONS"}, REQUEST_NOTIFICATIONS);
        }
    }

    private void onDarkModeChanged(CompoundButton button, boolean checked) {
        SettingsManager.setDarkModeEnabled(this, checked);
        AppCompatDelegate.setDefaultNightMode(checked
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
