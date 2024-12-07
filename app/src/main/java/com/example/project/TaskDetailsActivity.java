package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailsActivity extends AppCompatActivity {

    private Task task; // Assuming Task is a serializable model class
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Initialize database helper
        dbHelper = new DataBaseHelper(this);

        // Get task object passed from intent
        task = (Task) getIntent().getSerializableExtra("task");

        // Bind views
        TextView taskTitle = findViewById(R.id.taskTitle);
        TextView taskDescription = findViewById(R.id.taskDescription);
        TextView taskDueDate = findViewById(R.id.taskDueDate);
        TextView taskPriority = findViewById(R.id.taskPriority);
        TextView taskDueTime = findViewById(R.id.taskDueTime);
        TextView taskIsCompleted = findViewById(R.id.taskIsCompleted);

        Button editButton = findViewById(R.id.btnEditTask);
        Button deleteButton = findViewById(R.id.btnDeleteTask);
        Button markAsCompleteButton = findViewById(R.id.btnMarkAsComplete);
        Button shareButton = findViewById(R.id.btnShareTask);

        // Check if task is null
        if (task != null) {
            displayTaskDetails(task, taskTitle, taskDescription, taskDueDate, taskPriority, taskDueTime, taskIsCompleted);
        } else {
            Toast.makeText(this, "Task details are not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Edit task
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(TaskDetailsActivity.this, EditTaskActivity.class);
            intent.putExtra("task", task); // Pass the task to the edit activity
            startActivityForResult(intent, 100); // Use startActivityForResult to handle updates
        });

        // Delete task
        deleteButton.setOnClickListener(v -> {
            if (task != null) {
                boolean isDeleted = dbHelper.deleteTask(dbHelper.getTaskId(task.getTitle(), task.getDueDate(), task.getDueTime()));
                if (isDeleted) {
                    Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Mark task as complete
        markAsCompleteButton.setOnClickListener(v -> {
            if (task != null && !task.isCompleted()) {
                task.setCompleted(true);
                boolean isUpdated = dbHelper.markTaskAsComplete(dbHelper.getTaskId(task.getTitle(), task.getDueDate(), task.getDueTime()));
                if (isUpdated) {
                    taskIsCompleted.setText("Completed");
                    Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to mark task as completed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Task is already completed", Toast.LENGTH_SHORT).show();
            }
        });

        // Share task
        shareButton.setOnClickListener(v -> {
            if (task != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Task: " + task.getTitle() + "\nDescription: " + task.getDescription());
                startActivity(Intent.createChooser(shareIntent, "Share Task via"));
            }
        });
    }

    // Display task details in the UI
    private void displayTaskDetails(Task task, TextView taskTitle, TextView taskDescription, TextView taskDueDate, TextView taskPriority, TextView taskDueTime, TextView taskIsCompleted) {
        taskTitle.setText(task.getTitle());
        taskDescription.setText(task.getDescription());
        taskDueDate.setText(task.getDueDate());
        taskPriority.setText(task.getPriority());
        taskDueTime.setText(task.getDueTime());
        taskIsCompleted.setText(task.isCompleted() ? "Completed" : "Not Completed");
    }

    // Handle result from EditTaskActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            task = (Task) data.getSerializableExtra("updatedTask");
            if (task != null) {
                // Refresh task details after editing
                displayTaskDetails(task,
                        findViewById(R.id.taskTitle),
                        findViewById(R.id.taskDescription),
                        findViewById(R.id.taskDueDate),
                        findViewById(R.id.taskPriority),
                        findViewById(R.id.taskDueTime),
                        findViewById(R.id.taskIsCompleted));
                Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
