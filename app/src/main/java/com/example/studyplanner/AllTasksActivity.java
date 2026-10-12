package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Lists every task with a filter for all / completed / pending tasks.
 * Tapping a row opens the details screen; the checkbox updates the state
 * directly in the database.
 */
public class AllTasksActivity extends AppCompatActivity implements TaskAdapter.TaskListener {

    private TaskDbHelper dbHelper;
    private TaskAdapter adapter;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        dbHelper = TaskDbHelper.getInstance(this);
        textEmpty = findViewById(R.id.textEmpty);

        adapter = new TaskAdapter(this, true);
        RecyclerView recycler = findViewById(R.id.recyclerTasks);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        RadioGroup group = findViewById(R.id.groupFilter);
        group.setOnCheckedChangeListener((g, checkedId) -> loadTasks(checkedId));

        findViewById(R.id.buttonAddTask).setOnClickListener(
                v -> startActivity(new Intent(this, AddTaskActivity.class)));

        loadTasks(R.id.radioAll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RadioGroup group = findViewById(R.id.groupFilter);
        loadTasks(group.getCheckedRadioButtonId());
    }

    private void loadTasks(int checkedId) {
        int filter = 0;
        if (checkedId == R.id.radioCompleted) {
            filter = 1;
        } else if (checkedId == R.id.radioPending) {
            filter = 2;
        }
        adapter.setTasks(dbHelper.getTasks(filter));
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
        Task updated = dbHelper.getTask(task.getId());
        if (updated == null) {
            return;
        }
        updated.setCompleted(isChecked);
        dbHelper.updateTask(updated);
        if (isChecked) {
            ReminderScheduler.cancel(this, updated.getId());
        }
        RadioGroup group = findViewById(R.id.groupFilter);
        loadTasks(group.getCheckedRadioButtonId());
    }
}
