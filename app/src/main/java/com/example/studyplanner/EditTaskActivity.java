package com.example.studyplanner;

import android.os.Bundle;
import android.widget.Toast;

/**
 * Edits an existing task. The task id arrives through the Intent, the current
 * values are loaded into the form and the changes are saved back to SQLite.
 * The reminder is re-scheduled when its time or state changed.
 */
public class EditTaskActivity extends BaseTaskFormActivity {

    public static final String EXTRA_TASK_ID = "extra_task_id";

    private Task existingTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        bindFormViews();

        long taskId = getIntent().getLongExtra(EXTRA_TASK_ID, -1);
        existingTask = dbHelper.getTask(taskId);
        if (existingTask == null) {
            Toast.makeText(this, R.string.no_tasks, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fillFormFromTask(existingTask);

        findViewById(R.id.buttonSave).setOnClickListener(v -> updateTask());
        findViewById(R.id.buttonCancel).setOnClickListener(v -> finish());
    }

    private void updateTask() {
        if (readTitle() == null) {
            showTitleError();
            return;
        }
        fillTaskFromForm(existingTask);
        dbHelper.updateTask(existingTask);

        // Always clear the old alarm first, then schedule the new one if needed.
        ReminderScheduler.cancel(this, existingTask.getId());
        if (existingTask.isReminderEnabled() && SettingsManager.notificationsEnabled(this)) {
            ReminderScheduler.schedule(this, existingTask);
        }

        Toast.makeText(this, R.string.task_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
