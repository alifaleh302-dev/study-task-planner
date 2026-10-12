package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Searches tasks by title. The query runs against SQLite through a bound
 * {@code LIKE} parameter, never against a static list in the code.
 */
public class SearchActivity extends AppCompatActivity implements TaskAdapter.TaskListener {

    private TaskDbHelper dbHelper;
    private TaskAdapter adapter;
    private EditText editSearch;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = TaskDbHelper.getInstance(this);
        editSearch = findViewById(R.id.editSearch);
        textEmpty = findViewById(R.id.textEmpty);

        adapter = new TaskAdapter(this, false);
        RecyclerView recycler = findViewById(R.id.recyclerResults);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        findViewById(R.id.buttonSearch).setOnClickListener(v -> performSearch());
        findViewById(R.id.buttonClear).setOnClickListener(v -> {
            editSearch.setText("");
            adapter.setTasks(java.util.Collections.emptyList());
            textEmpty.setVisibility(View.GONE);
        });

        editSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String keyword = editSearch.getText().toString().trim();
        if (keyword.isEmpty()) {
            adapter.setTasks(java.util.Collections.emptyList());
            textEmpty.setVisibility(View.GONE);
            return;
        }
        adapter.setTasks(dbHelper.searchTasks(keyword));
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
        // Read-only results; completion is changed from the details screen.
    }
}
