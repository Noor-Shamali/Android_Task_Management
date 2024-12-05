package com.example.project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailsActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Get the task passed from the adapter
        Task task = (Task) getIntent().getSerializableExtra("task");

        // Initialize views
        TextView taskTitle = findViewById(R.id.taskTitle);
        TextView taskDescription = findViewById(R.id.taskDescription);
        TextView taskDueDate = findViewById(R.id.taskDueDate);
        TextView taskPriority = findViewById(R.id.taskPriority);
        TextView taskDueTime = findViewById(R.id.taskDueTime);  // Fixed: Corrected to dueTime instead of startTime
        TextView taskIsCompleted = findViewById(R.id.taskIsCompleted);

        // Check if task is null to prevent crashes
        if (task != null) {
            // Set task details to the views
            taskTitle.setText(task.getTitle() != null ? task.getTitle() : "No title available");
            taskDescription.setText(task.getDescription() != null ? task.getDescription() : "No description available");
            taskDueDate.setText(task.getDueDate() != null ? task.getDueDate() : "No due date available");
            taskPriority.setText(task.getPriority() != null ? task.getPriority() : "No priority available");
            taskDueTime.setText(task.getDueTime() != null ? task.getDueTime() : "No due time available");
            taskIsCompleted.setText(task.isCompleted() ? "Completed" : "Not Completed");
        } else {
            // Handle the case where the task is null (in case the intent doesn't pass a task object)
            taskTitle.setText("No task available");
            taskDescription.setText("No task details available");
            taskDueDate.setText("No task details available");
            taskPriority.setText("No task details available");
            taskDueTime.setText("No task details available");
            taskIsCompleted.setText("No task details available");
        }
    }
}
