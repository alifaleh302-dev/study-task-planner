package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Home screen: shows a summary of the tasks, the next upcoming tasks and the
 * shortcuts to every other section. It reloads its data in {@code onResume} so
 * the counters always match the database after an add, edit or delete.
 */
public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskListener {

    private static final int UPCOMING_LIMIT = 5;

    private TaskDbHelper dbHelper;
    private TaskAdapter adapter;

    private TextView textTodayDate;
    private TextView textTotal;
    private TextView textCompleted;
    private TextView textPending;
    private TextView textUpcomingEmpty;
    private RecyclerView recyclerUpcoming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = TaskDbHelper.getInstance(this);

        textTodayDate = findViewById(R.id.textTodayDate);
        textTotal = findViewById(R.id.textTotal);
        textCompleted = findViewById(R.id.textCompleted);
        textPending = findViewById(R.id.textPending);
        textUpcomingEmpty = findViewById(R.id.textUpcomingEmpty);
        recyclerUpcoming = findViewById(R.id.recyclerUpcoming);

        adapter = new TaskAdapter(this, true);
        recyclerUpcoming.setLayoutManager(new LinearLayoutManager(this));
        recyclerUpcoming.setAdapter(adapter);

        findViewById(R.id.buttonAddTask).setOnClickListener(
                v -> startActivity(new Intent(this, AddTaskActivity.class)));
        findViewById(R.id.buttonAllTasks).setOnClickListener(
                v -> startActivity(new Intent(this, AllTasksActivity.class)));
        findViewById(R.id.buttonCalendar).setOnClickListener(
                v -> startActivity(new Intent(this, CalendarActivity.class)));
        findViewById(R.id.buttonCategories).setOnClickListener(
                v -> startActivity(new Intent(this, CategoriesActivity.class)));
        findViewById(R.id.buttonSearch).setOnClickListener(
                v -> startActivity(new Intent(this, SearchActivity.class)));
        findViewById(R.id.buttonStatistics).setOnClickListener(
                v -> startActivity(new Intent(this, StatisticsActivity.class)));
        findViewById(R.id.buttonSettings).setOnClickListener(
                v -> startActivity(new Intent(this, SettingsActivity.class)));
        findViewById(R.id.textViewAll).setOnClickListener(
                v -> startActivity(new Intent(this, AllTasksActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSummary();
    }

    private void loadSummary() {
        String today = new SimpleDateFormat("EEEE، yyyy/MM/dd", new Locale("ar"))
                .format(new Date());
        textTodayDate.setText(today);

        int total = dbHelper.getTaskCount();
        int completed = dbHelper.getCompletedCount(true);

        textTotal.setText(String.valueOf(total));
        textCompleted.setText(String.valueOf(completed));
        textPending.setText(String.valueOf(total - completed));

        adapter.setTasks(dbHelper.getUpcomingTasks(UPCOMING_LIMIT));
        textUpcomingEmpty.setVisibility(adapter.isEmpty() ? TextView.VISIBLE : TextView.GONE);
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        intent.putExtra(TaskDetailsActivity.EXTRA_TASK_ID, task.getId());
        startActivity(intent);
    }

    @Override
    public void onTaskCheckedChange(Task task, boolean isChecked) {
        Task updated = dbHelper.getTask(task.getId());
        if (updated == null) {
            return;
        }
        updated.setCompleted(isChecked);
        dbHelper.updateTask(updated);
        if (isChecked) {
            ReminderScheduler.cancel(this, updated.getId());
        }
        loadSummary();
    }
}
