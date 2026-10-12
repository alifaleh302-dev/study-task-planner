package com.example.studyplanner;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/**
 * Shows how many tasks are done compared with the total, the completion
 * percentage and the number of tasks per category.
 */
public class StatisticsActivity extends AppCompatActivity {

    private TaskDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        dbHelper = TaskDbHelper.getInstance(this);
        loadStatistics();
    }

    private void loadStatistics() {
        int total = dbHelper.getTaskCount();
        int completed = dbHelper.getCompletedCount(true);
        int pending = total - completed;

        // Guard against division by zero when there are no tasks yet.
        int percent = total == 0 ? 0 : Math.round(completed * 100f / total);

        ((TextView) findViewById(R.id.textTotal)).setText(String.valueOf(total));
        ((TextView) findViewById(R.id.textCompleted)).setText(String.valueOf(completed));
        ((TextView) findViewById(R.id.textPending)).setText(String.valueOf(pending));
        ((ProgressBar) findViewById(R.id.progressCompletion)).setProgress(percent);
        ((TextView) findViewById(R.id.textProgressPercent))
                .setText(getString(R.string.progress_percent, percent));

        buildCategoryBreakdown();
    }

    /** Creates one bar row per category inside the container. */
    private void buildCategoryBreakdown() {
        LinearLayout container = findViewById(R.id.containerCategories);
        container.removeAllViews();

        List<Category> categories = dbHelper.getCategoriesWithCounts();
        int total = Math.max(dbHelper.getTaskCount(), 1);
        for (Category category : categories) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 12, 0, 12);

            TextView name = new TextView(this);
            name.setText(getString(R.string.tasks_count, category.getTaskCount())
                    + "  —  " + category.getName());
            name.setTextSize(15f);
            row.addView(name);

            ProgressBar bar = new ProgressBar(this, null,
                    android.R.attr.progressBarStyleHorizontal);
            bar.setMax(total);
            bar.setProgress(category.getTaskCount());
            row.addView(bar);

            container.addView(row);
        }
    }
}
