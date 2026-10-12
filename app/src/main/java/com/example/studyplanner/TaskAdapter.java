package com.example.studyplanner;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a list of {@link Task} objects inside a RecyclerView.
 *
 * <p>The adapter does not talk to the database directly. Instead the hosting
 * activity implements {@link TaskListener} and reacts to clicks and checkbox
 * changes.</p>
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    /** Callbacks used by the activities that host the list. */
    public interface TaskListener {
        void onTaskClick(Task task);

        void onTaskCheckedChange(Task task, boolean isChecked);
    }

    private final List<Task> tasks = new ArrayList<>();
    private final TaskListener listener;
    private final boolean showCheckBox;

    public TaskAdapter(TaskListener listener, boolean showCheckBox) {
        this.listener = listener;
        this.showCheckBox = showCheckBox;
    }

    /** Replaces the whole list and refreshes the view. */
    public void setTasks(List<Task> newTasks) {
        tasks.clear();
        if (newTasks != null) {
            tasks.addAll(newTasks);
        }
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        private final CheckBox checkCompleted;
        private final TextView textTitle;
        private final TextView textDue;
        private final TextView textCategory;
        private final TextView textPriority;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkCompleted = itemView.findViewById(R.id.checkCompleted);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDue = itemView.findViewById(R.id.textDue);
            textCategory = itemView.findViewById(R.id.textCategory);
            textPriority = itemView.findViewById(R.id.textPriority);
        }

        void bind(final Task task) {
            textTitle.setText(task.getTitle());
            textDue.setText(DateUtils.formatDateTime(task.getDueDate()));

            String categoryName = TaskDbHelper.getInstance(itemView.getContext())
                    .getCategoryName(task.getCategoryId());
            textCategory.setText(categoryName);

            textPriority.setText(DateUtils.priorityName(itemView.getContext(), task.getPriority()));
            int color = ContextCompat.getColor(
                    itemView.getContext(), DateUtils.priorityColor(task.getPriority()));
            textPriority.setBackgroundTintList(ColorStateList.valueOf(color));

            // Strike through the title when the task is completed.
            int paintFlags = task.isCompleted()
                    ? textTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                    : textTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG;
            textTitle.setPaintFlags(paintFlags);

            // Detach the listener before setting the state to avoid a false callback.
            checkCompleted.setOnCheckedChangeListener(null);
            checkCompleted.setChecked(task.isCompleted());
            checkCompleted.setVisibility(showCheckBox ? View.VISIBLE : View.GONE);
            checkCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTaskCheckedChange(task, isChecked);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(task);
                }
            });
        }
    }
}
