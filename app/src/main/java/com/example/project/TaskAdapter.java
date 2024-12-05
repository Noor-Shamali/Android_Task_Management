package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();  // Get context from the parent
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view, context); // Pass context to the ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Bind task data to the views
        holder.taskTitle.setText(task.getTitle());
        holder.taskDueDate.setText(task.getDueDate());
        holder.taskPriority.setText(task.getPriority());

        // Handle task item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.context, TaskDetailsActivity.class);
            intent.putExtra("task", task); // Pass the task object to the new activity
            holder.context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDueDate, taskPriority;
        Context context;  // Store the context here

        public TaskViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDueDate = itemView.findViewById(R.id.taskDueDate);
            taskPriority = itemView.findViewById(R.id.taskPriority);
            this.context = context;  // Assign context to the field
        }
    }
}
