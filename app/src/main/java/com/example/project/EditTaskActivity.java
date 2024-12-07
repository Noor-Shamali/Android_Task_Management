package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditTaskActivity extends AppCompatActivity {

    private EditText editTaskTitle, editTaskDescription;
    private Spinner editPrioritySpinner;
    private Switch editCompletionStatusSwitch;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // Bind views
        editTaskTitle = findViewById(R.id.editTaskTitle);
        editTaskDescription = findViewById(R.id.editTaskDescription);
        editPrioritySpinner = findViewById(R.id.editPrioritySpinner);
        editCompletionStatusSwitch = findViewById(R.id.editCompletionStatusSwitch);
        Button saveEditButton = findViewById(R.id.saveEditButton);
        Button cancelEditButton = findViewById(R.id.cancelEditButton);

        // Populate the priority spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.priority_options, // Ensure this is defined in res/values/strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editPrioritySpinner.setAdapter(adapter);

        // Get the task object passed from the previous activity
        task = (Task) getIntent().getSerializableExtra("task");

        if (task != null) {
            // Prefill fields with existing task data
            editTaskTitle.setText(task.getTitle());
            editTaskDescription.setText(task.getDescription());
            editCompletionStatusSwitch.setChecked(task.isCompleted());

            // Set spinner selection based on the task's priority
            int priorityPosition = adapter.getPosition(task.getPriority());
            if (priorityPosition >= 0) {
                editPrioritySpinner.setSelection(priorityPosition);
            } else {
                // Handle case where priority is not in the spinner options
                Toast.makeText(this, "Unknown priority: " + task.getPriority(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where the task object is null
            Toast.makeText(this, "Error: Task data is missing", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Save button logic
        saveEditButton.setOnClickListener(v -> {
            if (task != null) {
                // Validate input fields
                if (editTaskTitle.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update task details
                Task updatedTask = new Task(
                        editTaskTitle.getText().toString(),
                        editTaskDescription.getText().toString(),
                        editCompletionStatusSwitch.isChecked(),
                        editPrioritySpinner.getSelectedItem().toString(),
                        task.getDueDate(), // Keeping the original due date unless edited separately
                        task.getDueTime(), // Keeping the original due time unless edited separately
                        task.getRemind(),  // Keeping the original remind value
                        task.getUserEmail() // Keeping the original user email
                );

                // Update the task in the database
                DataBaseHelper dbHelper = new DataBaseHelper(this);
                boolean isUpdated = dbHelper.editTask(task, updatedTask); // Pass old and new task

                if (isUpdated) {
                    // Pass the updated task back to the previous activity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedTask", updatedTask);
                    setResult(RESULT_OK, resultIntent);

                    // Show confirmation message and finish the activity
                    Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cancel button logic
        cancelEditButton.setOnClickListener(v -> finish());
    }
}
