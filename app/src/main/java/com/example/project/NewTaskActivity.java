package com.example.project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class NewTaskActivity extends AppCompatActivity {

    private EditText taskTitle, taskDescription;
    private Button dueDateButton, dueTimeButton, saveTaskButton;
    private Spinner prioritySpinner;
    private Switch completionStatusSwitch;

    private int selectedYear, selectedMonth, selectedDay;
    private int selectedHour, selectedMinute;

    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        // Initialize UI components
        taskTitle = findViewById(R.id.taskTitle);
        taskDescription = findViewById(R.id.taskDescription);
        dueDateButton = findViewById(R.id.dueDateButton);
        dueTimeButton = findViewById(R.id.dueTimeButton);
        saveTaskButton = findViewById(R.id.saveButton);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        completionStatusSwitch = findViewById(R.id.completionStatusSwitch);

        // Initialize Database Helper
        dbHelper = new DataBaseHelper(this);

        // Initialize Spinner for priority levels
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.priority_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        // Initialize Calendar values
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        selectedMinute = calendar.get(Calendar.MINUTE);

        // Set button listeners
        dueDateButton.setOnClickListener(v -> showDatePickerDialog());
        dueTimeButton.setOnClickListener(v -> showTimePickerDialog());
        saveTaskButton.setOnClickListener(v -> saveTask());
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

        // Handle empty fields validation
        if (title.isEmpty() || description.isEmpty() || dueDate.equals("Select Date") || dueTime.equals("Select Time")) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            String userEmail = getIntent().getStringExtra("email");

            Task task = new Task(title, description, isCompleted, priority, dueDate,dueTime, 0, userEmail);
            boolean isInserted = dbHelper.addTask(task);

            if (isInserted) {
                Toast.makeText(this, "Task saved successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close activity after saving
            } else {
                Toast.makeText(this, "Error saving task", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
