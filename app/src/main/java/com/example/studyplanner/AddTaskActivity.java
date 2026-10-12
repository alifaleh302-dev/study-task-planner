package com.example.studyplanner;

import android.os.Bundle;
import android.widget.Toast;

/**
 * Creates a new task. Validates the title, inserts the row in SQLite and
 * schedules the reminder when the user enabled it, then closes.
 */
public class AddTaskActivity extends BaseTaskFormActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        bindFormViews();

        if (SettingsManager.defaultReminderEnabled(this)) {
            checkReminder.setChecked(true);
        }

        findViewById(R.id.buttonSave).setOnClickListener(v -> saveTask());
        findViewById(R.id.buttonCancel).setOnClickListener(v -> finish());
    }

    private void saveTask() {
        if (readTitle() == null) {
            showTitleError();
            return;
        }
        Task task = new Task();
        fillTaskFromForm(task);

        long id = dbHelper.insertTask(task);
        if (id <= 0) {
            Toast.makeText(this, R.string.invalid_title, Toast.LENGTH_SHORT).show();
            return;
        }

        task.setId(id);
        if (task.isReminderEnabled() && SettingsManager.notificationsEnabled(this)) {
            ReminderScheduler.schedule(this, task);
        }

        Toast.makeText(this, R.string.task_saved, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
