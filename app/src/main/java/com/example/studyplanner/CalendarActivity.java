package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Shows the tasks of a chosen day using the built-in {@link CalendarView}.
 * Selecting another date reloads the list below the calendar.
 */
public class CalendarActivity extends AppCompatActivity implements TaskAdapter.TaskListener {

    private TaskDbHelper dbHelper;
    private TaskAdapter adapter;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dbHelper = TaskDbHelper.getInstance(this);
        textEmpty = findViewById(R.id.textEmpty);

        adapter = new TaskAdapter(this, false);
        RecyclerView recycler = findViewById(R.id.recyclerTasks);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            loadTasksForDay(calendar.getTimeInMillis());
        });

        loadTasksForDay(System.currentTimeMillis());
    }

    @Override
    protected void onResume() {
        super.onResume();
        CalendarView calendarView = findViewById(R.id.calendarView);
        loadTasksForDay(calendarView.getDate());
    }

    private void loadTasksForDay(long dayMillis) {
        adapter.setTasks(dbHelper.getTasksForDay(dayMillis));
        textEmpty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        intent.putExtra(TaskDetailsActivity.EXTRA_TASK_ID, task.getId());
        startActivity(intent);
    }

    @Override
    public void onTaskCheckedChange(Task task, boolean isChecked) {
        // The calendar list is read-only for completion; ignore the callback.
    }
}
