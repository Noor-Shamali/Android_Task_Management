package com.example.project;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailsActivity extends AppCompatActivity {

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
        TextView taskStartTime = findViewById(R.id.taskStartTime);
        TextView taskEndTime = findViewById(R.id.taskEndTime);
        TextView taskIsCompleted = findViewById(R.id.taskIsCompleted);

        // Set task details to the views
        taskTitle.setText(task.getTitle());
        taskDescription.setText(task.getDescription());
        taskDueDate.setText(task.getDueDate());
        taskPriority.setText(task.getPriority());
        taskStartTime.setText(task.getStartTime());
        taskEndTime.setText(task.getEndTime());
        taskIsCompleted.setText(task.isCompleted() ? "Completed" : "Not Completed");
    }
}
