package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllTasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_TASK = 1;

    private final List<Object> items; // Combines dates and tasks

    public AllTasksAdapter(Map<String, List<Task>> groupedTasks) {
        items = new ArrayList<>();
        for (Map.Entry<String, List<Task>> entry : groupedTasks.entrySet()) {
            items.add(entry.getKey()); // Add the date as a header
            items.addAll(entry.getValue()); // Add the tasks for that date
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof String) ? VIEW_TYPE_DATE : VIEW_TYPE_TASK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATE) {
            // Inflate date header view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
            return new DateViewHolder(view);
        } else {
            // Inflate task view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view, parent.getContext()); // Return TaskViewHolder with context
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DateViewHolder) {
            // Handle date headers
            String date = (String) items.get(position);
            ((DateViewHolder) holder).dateTextView.setText(date);
        } else if (holder instanceof TaskViewHolder) {
            // Handle tasks
            Task task = (Task) items.get(position);
            TaskViewHolder taskHolder = (TaskViewHolder) holder;

            // Bind task data to the views
            taskHolder.taskTitle.setText(task.getTitle());
            taskHolder.taskDueDate.setText(task.getDueDate());
            taskHolder.taskPriority.setText(task.getPriority());

            // Handle task item click
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), TaskDetailsActivity.class);
                intent.putExtra("task", task); // Pass the task object to the new activity
                holder.itemView.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }

    // TaskViewHolder class that handles task views
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView taskDueDate;
        TextView taskPriority;
        Context context;

        public TaskViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDueDate = itemView.findViewById(R.id.taskDueDate);
            taskPriority = itemView.findViewById(R.id.taskPriority);
        }
    }
}
