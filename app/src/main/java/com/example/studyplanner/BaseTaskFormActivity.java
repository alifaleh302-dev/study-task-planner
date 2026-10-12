package com.example.studyplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/**
 * Base class for the add and edit task screens. Both screens show the same
 * fields, so the shared behaviour (pickers, validation, reading the form into a
 * {@link Task}) lives here and each subclass only decides what to do on save.
 */
public abstract class BaseTaskFormActivity extends AppCompatActivity {

    protected EditText editTitle;
    protected EditText editDescription;
    protected Button buttonPickDate;
    protected Button buttonPickTime;
    protected Spinner spinnerCategory;
    protected RadioGroup groupPriority;
    protected CheckBox checkReminder;

    protected TaskDbHelper dbHelper;
    protected CategorySpinnerAdapter categoryAdapter;

    /** Selected due date (epoch millis) with the current time of day. */
    protected long dueDate;
    protected int selectedPriority = Task.PRIORITY_MEDIUM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = TaskDbHelper.getInstance(this);

        // Start with "today" so the pickers and save action always have a value.
        dueDate = System.currentTimeMillis();
    }

    /** Wires the common views after the layout has been inflated. */
    protected void bindFormViews() {
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        buttonPickDate = findViewById(R.id.buttonPickDate);
        buttonPickTime = findViewById(R.id.buttonPickTime);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        groupPriority = findViewById(R.id.groupPriority);
        checkReminder = findViewById(R.id.checkReminder);

        categoryAdapter = new CategorySpinnerAdapter(this, dbHelper.getCategories());
        spinnerCategory.setAdapter(categoryAdapter);

        buttonPickDate.setOnClickListener(v -> DateTimePicker.showDatePicker(
                this, dueDate, millis -> {
                    dueDate = millis;
                    updateDateTimeLabels();
                }));

        buttonPickTime.setOnClickListener(v -> DateTimePicker.showTimePicker(
                this, dueDate, millis -> {
                    dueDate = millis;
                    updateDateTimeLabels();
                }));

        groupPriority.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioPriorityLow) {
                selectedPriority = Task.PRIORITY_LOW;
            } else if (checkedId == R.id.radioPriorityHigh) {
                selectedPriority = Task.PRIORITY_HIGH;
            } else {
                selectedPriority = Task.PRIORITY_MEDIUM;
            }
        });

        updateDateTimeLabels();
    }

    /** Refreshes the two buttons that show the chosen date and time. */
    protected void updateDateTimeLabels() {
        buttonPickDate.setText(DateUtils.formatDate(dueDate).isEmpty()
                ? getString(R.string.pick_date)
                : getString(R.string.field_due_date) + ": " + DateUtils.formatDate(dueDate));
        buttonPickTime.setText(DateUtils.formatTime(dueDate).isEmpty()
                ? getString(R.string.pick_time)
                : getString(R.string.field_due_time) + ": " + DateUtils.formatTime(dueDate));
    }

    /** Returns the title, or {@code null} when the field is empty. */
    protected String readTitle() {
        String title = editTitle.getText().toString().trim();
        return title.isEmpty() ? null : title;
    }

    /** Prints the "title is required" message and focuses the field. */
    protected void showTitleError() {
        Toast.makeText(this, R.string.invalid_title, Toast.LENGTH_SHORT).show();
        editTitle.requestFocus();
    }

    /** Copies the current form values into the given task. */
    protected void fillTaskFromForm(Task task) {
        task.setTitle(readTitle());
        task.setDescription(editDescription.getText().toString().trim());
        task.setDueDate(dueDate);
        task.setPriority(selectedPriority);
        Category category = (Category) spinnerCategory.getSelectedItem();
        if (category != null) {
            task.setCategoryId(category.getId());
        }
        boolean reminder = checkReminder.isChecked();
        task.setReminderEnabled(reminder);
        task.setReminderTime(reminder ? dueDate : 0);
    }

    /** Restores a task into the form, used by the edit screen. */
    protected void fillFormFromTask(Task task) {
        editTitle.setText(task.getTitle());
        editDescription.setText(task.getDescription());
        dueDate = task.getDueDate();
        selectedPriority = task.getPriority();
        checkReminder.setChecked(task.isReminderEnabled());

        switch (selectedPriority) {
            case Task.PRIORITY_LOW:
                groupPriority.check(R.id.radioPriorityLow);
                break;
            case Task.PRIORITY_HIGH:
                groupPriority.check(R.id.radioPriorityHigh);
                break;
            default:
                groupPriority.check(R.id.radioPriorityMedium);
                break;
        }

        List<Category> categories = dbHelper.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == task.getCategoryId()) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
        updateDateTimeLabels();
    }
}
