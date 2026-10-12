package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Shows the categories with the number of tasks linked to each one and lets the
 * user add a simple custom category.
 */
public class CategoriesActivity extends AppCompatActivity implements CategoryAdapter.CategoryListener {

    private TaskDbHelper dbHelper;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        dbHelper = TaskDbHelper.getInstance(this);
        adapter = new CategoryAdapter(this);
        RecyclerView recycler = findViewById(R.id.recyclerCategories);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        findViewById(R.id.buttonAddCategory).setOnClickListener(v -> showAddCategoryDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    private void loadCategories() {
        adapter.setCategories(dbHelper.getCategoriesWithCounts());
    }

    private void showAddCategoryDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.category_name_hint);

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_category)
                .setView(input)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, R.string.required_field, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dbHelper.insertCategory(name, "#1565C0");
                    Toast.makeText(this, R.string.category_added, Toast.LENGTH_SHORT).show();
                    loadCategories();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onCategoryClick(Category category) {
        Intent intent = new Intent(this, CategoryTasksActivity.class);
        intent.putExtra(CategoryTasksActivity.EXTRA_CATEGORY_ID, category.getId());
        intent.putExtra(CategoryTasksActivity.EXTRA_CATEGORY_NAME, category.getName());
        startActivity(intent);
    }
}
