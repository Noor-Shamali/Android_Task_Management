package com.example.project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditTaskActivity extends AppCompatActivity {

    private EditText taskTitle, taskDescription;
    private Button dueDateButton, dueTimeButton, saveButton, cancelButton;
    private Spinner prioritySpinner;
    private Switch completionStatusSwitch;

    private int selectedYear, selectedMonth, selectedDay;
    private int selectedHour, selectedMinute;

    private DataBaseHelper dbHelper;
    private Task task;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // Bind UI components
        taskTitle = findViewById(R.id.editTaskTitle);
        taskDescription = findViewById(R.id.editTaskDescription);
        dueDateButton = findViewById(R.id.editDueDateButton);
        dueTimeButton = findViewById(R.id.editDueTimeButton);
        saveButton = findViewById(R.id.saveEditButton);
        cancelButton = findViewById(R.id.cancelEditButton);
        prioritySpinner = findViewById(R.id.editPrioritySpinner);
        completionStatusSwitch = findViewById(R.id.editCompletionStatusSwitch);

        // Initialize Database Helper
        dbHelper = new DataBaseHelper(this);

        // Populate priority spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.priority_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        // Initialize calendar values
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        selectedMinute = calendar.get(Calendar.MINUTE);

        // Get the task object passed from the previous activity
        task = (Task) getIntent().getSerializableExtra("task");
        if (task != null) {
            populateFields(task, adapter);
        } else {
            Toast.makeText(this, "Error: Task data is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set listeners for buttons
        dueDateButton.setOnClickListener(v -> showDatePickerDialog());
        dueTimeButton.setOnClickListener(v -> showTimePickerDialog());
        saveButton.setOnClickListener(v -> saveTask());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void populateFields(Task task, ArrayAdapter<CharSequence> adapter) {
        taskTitle.setText(task.getTitle());
        taskDescription.setText(task.getDescription());
        completionStatusSwitch.setChecked(task.isCompleted());

        // Populate spinner selection
        int priorityPosition = adapter.getPosition(task.getPriority());
        if (priorityPosition >= 0) {
            prioritySpinner.setSelection(priorityPosition);
        }

        // Set due date and time
        dueDateButton.setText(task.getDueDate());
        dueTimeButton.setText(task.getDueTime());
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedYear = year;
                    selectedMonth = month;
                    selectedDay = dayOfMonth;
                    updateDateButton();
                }, selectedYear, selectedMonth, selectedDay);
        datePickerDialog.show();
    }

    private void updateDateButton() {
        dueDateButton.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minute;
                    updateTimeButton();
                }, selectedHour, selectedMinute, true);
        timePickerDialog.show();
    }

    private void updateTimeButton() {
        dueTimeButton.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
    }

    private void saveTask() {
        String title = taskTitle.getText().toString().trim();
        String description = taskDescription.getText().toString().trim();
        String priority = prioritySpinner.getSelectedItem().toString();
        boolean isCompleted = completionStatusSwitch.isChecked();
        String dueDate = dueDateButton.getText().toString();
        String dueTime = dueTimeButton.getText().toString();

        if (title.isEmpty() || description.isEmpty() || dueDate.equals("Select Date") || dueTime.equals("Select Time")) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Task updatedTask = new Task(
                title, description, isCompleted, priority, dueDate, dueTime, task.getRemind(), task.getUserEmail()
        );

        boolean isUpdated = dbHelper.editTask(task, updatedTask);

        if (isUpdated) {
            Toast.makeText(this, "Task updated successfully!", Toast.LENGTH_SHORT).show();
            getCurrentTaskState(updatedTask);
            intent.putExtra("updatedTask", updatedTask);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Error updating task", Toast.LENGTH_SHORT).show();
        }
    }

    public void getCurrentTaskState(Task task) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = dateFormat.format(new Date());

        if (task.getDueDate().equals(todayDate)) {
            intent = new Intent(this,HomeActivity.class);
        }
        else if (task.isCompleted()) {
            intent = new Intent(this,CompletedTasksActivity.class);
        }
        else{
            intent = new Intent(this,AllTasksActivity.class);
        }
    }
}
