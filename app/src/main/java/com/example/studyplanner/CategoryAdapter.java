package com.example.studyplanner;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the list of categories. Each row displays the category name, its colour
 * and the number of tasks linked to it.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    /** Callback fired when a category row is tapped. */
    public interface CategoryListener {
        void onCategoryClick(Category category);
    }

    private final List<Category> categories = new ArrayList<>();
    private final CategoryListener listener;

    public CategoryAdapter(CategoryListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> newCategories) {
        categories.clear();
        if (newCategories != null) {
            categories.addAll(newCategories);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final View viewColor;
        private final TextView textName;
        private final TextView textCount;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            viewColor = itemView.findViewById(R.id.viewColor);
            textName = itemView.findViewById(R.id.textCategoryName);
            textCount = itemView.findViewById(R.id.textCategoryCount);
        }

        void bind(final Category category) {
            textName.setText(category.getName());
            textCount.setText(itemView.getContext()
                    .getString(R.string.tasks_count, category.getTaskCount()));

            GradientDrawable dot = new GradientDrawable();
            dot.setShape(GradientDrawable.OVAL);
            try {
                dot.setColor(Color.parseColor(category.getColor()));
            } catch (IllegalArgumentException e) {
                dot.setColor(Color.GRAY);
            }
            viewColor.setBackground(dot);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }
    }
}
