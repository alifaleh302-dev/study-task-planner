package com.example.studyplanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * Shows the full data of one task and offers the complete / edit / delete
 * actions. The task is always re-read from the database, so no static text is
 * displayed.
 */
public class TaskDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "extra_task_id";

    private TaskDbHelper dbHelper;
    private Task task;

    private TextView textTitle;
    private TextView textStatus;
    private TextView textDescription;
    private TextView textDue;
    private TextView textCategory;
    private TextView textPriority;
    private android.widget.Button buttonToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        dbHelper = TaskDbHelper.getInstance(this);
        textTitle = findViewById(R.id.textTitle);
        textStatus = findViewById(R.id.textStatus);
        textDescription = findViewById(R.id.textDescription);
        textDue = findViewById(R.id.textDue);
        textCategory = findViewById(R.id.textCategory);
        textPriority = findViewById(R.id.textPriority);
        buttonToggle = findViewById(R.id.buttonToggleComplete);

        long taskId = getIntent().getLongExtra(EXTRA_TASK_ID, -1);
        task = dbHelper.getTask(taskId);
        if (task == null) {
            Toast.makeText(this, R.string.no_tasks, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        buttonToggle.setOnClickListener(v -> toggleCompleted());
        findViewById(R.id.buttonEdit).setOnClickListener(v -> openEdit());
        findViewById(R.id.buttonDelete).setOnClickListener(v -> confirmDelete());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (task != null) {
            task = dbHelper.getTask(task.getId());
            if (task != null) {
                bind();
            }
        }
    }

    private void bind() {
        textTitle.setText(task.getTitle());
        textDescription.setText(task.getDescription());

        boolean completed = task.isCompleted();
        textStatus.setText(completed ? R.string.status_completed : R.string.status_pending);
        textStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(
                this, completed ? R.color.priorityLow : R.color.priorityMedium)));

        textDue.setText(getString(R.string.field_due_date) + ": "
                + DateUtils.formatDateTime(task.getDueDate()));
        textCategory.setText(getString(R.string.field_category) + ": "
                + dbHelper.getCategoryName(task.getCategoryId()));
        textPriority.setText(getString(R.string.field_priority) + ": "
                + DateUtils.priorityName(this, task.getPriority()));
        buttonToggle.setText(completed ? R.string.mark_uncompleted : R.string.mark_completed);
    }

    private void toggleCompleted() {
        boolean nowCompleted = dbHelper.toggleCompleted(task.getId());
        if (nowCompleted) {
            ReminderScheduler.cancel(this, task.getId());
        }
        task = dbHelper.getTask(task.getId());
        bind();
        Toast.makeText(this,
                nowCompleted ? R.string.task_completed : R.string.task_uncompleted,
                Toast.LENGTH_SHORT).show();
    }

    private void openEdit() {
        Intent intent = new Intent(this, EditTaskActivity.class);
        intent.putExtra(EditTaskActivity.EXTRA_TASK_ID, task.getId());
        startActivity(intent);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete, (DialogInterface dialog, int which) -> deleteTask())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteTask() {
        ReminderScheduler.cancel(this, task.getId());
        dbHelper.deleteTask(task.getId());
        Toast.makeText(this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
        finish();
    }
}
