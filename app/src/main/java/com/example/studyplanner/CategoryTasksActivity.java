package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Lists the tasks of one category. The category id is received through the
 * Intent and used to read only the matching tasks from the database.
 */
public class CategoryTasksActivity extends AppCompatActivity implements TaskAdapter.TaskListener {

    public static final String EXTRA_CATEGORY_ID = "extra_category_id";
    public static final String EXTRA_CATEGORY_NAME = "extra_category_name";

    private long categoryId;
    private TaskDbHelper dbHelper;
    private TaskAdapter adapter;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_tasks);

        dbHelper = TaskDbHelper.getInstance(this);
        categoryId = getIntent().getLongExtra(EXTRA_CATEGORY_ID, -1);
        String categoryName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);

        TextView title = findViewById(R.id.textCategoryName);
        if (categoryName != null && !categoryName.isEmpty()) {
            title.setText(categoryName);
        } else {
            title.setText(dbHelper.getCategoryName(categoryId));
        }

        textEmpty = findViewById(R.id.textEmpty);
        adapter = new TaskAdapter(this, false);
        RecyclerView recycler = findViewById(R.id.recyclerTasks);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        loadTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        adapter.setTasks(dbHelper.getTasksByCategory(categoryId));
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
        // Read-only list; completion is changed from the details screen.
    }
}
