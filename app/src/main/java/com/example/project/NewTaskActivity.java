package com.example.project;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.TimeZone;

public class NewTaskActivity extends AppCompatActivity {

    private static final String MY_CHANNEL_ID = "TaskReminderChannel";
    private static final String MY_CHANNEL_NAME = "Task Reminders";

    private EditText taskTitle, taskDescription;
    private Button dueDateButton, dueTimeButton, saveTaskButton, reminderDateButton, reminderTimeButton;
    private Spinner prioritySpinner, snoozeIntervalSpinner, snoozeRepeatSpinner;
    private Switch completionStatusSwitch;

    private int selectedYear, selectedMonth, selectedDay;
    private int selectedHour, selectedMinute;
    private String userEmail;
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
        reminderDateButton = findViewById(R.id.ReminderDateButton);
        reminderTimeButton = findViewById(R.id.ReminderTimeButton);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        snoozeIntervalSpinner = findViewById(R.id.snoozeIntervalSpinner);
        snoozeRepeatSpinner = findViewById(R.id.snoozeRepeatSpinner);
        completionStatusSwitch = findViewById(R.id.completionStatusSwitch);
        userEmail = getIntent().getStringExtra("email");

        // Initialize Database Helper
        dbHelper = new DataBaseHelper(this);

        // Populate Spinners
        initializeSpinner(snoozeIntervalSpinner, R.array.snooze_intervals);
        initializeSpinner(snoozeRepeatSpinner, R.array.snooze_repeats);
        initializeSpinner(prioritySpinner, R.array.priority_options);

        // Initialize Calendar values
        initializeCalendar();

        // Set button listeners
        dueDateButton.setOnClickListener(v -> showDatePickerDialog(dueDateButton));
        dueTimeButton.setOnClickListener(v -> showTimePickerDialog(dueTimeButton));
        reminderDateButton.setOnClickListener(v -> showDatePickerDialog(reminderDateButton));
        reminderTimeButton.setOnClickListener(v -> showTimePickerDialog(reminderTimeButton));
        saveTaskButton.setOnClickListener(v -> saveTask());

        // Create notification channel
        createNotificationChannel();
    }

    private void initializeSpinner(Spinner spinner, int arrayResourceId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                arrayResourceId,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void initializeCalendar() {
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        selectedMinute = calendar.get(Calendar.MINUTE);
    }

    private void showDatePickerDialog(Button button) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedYear = year;
                    selectedMonth = month;
                    selectedDay = dayOfMonth;
                    button.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
                }, selectedYear, selectedMonth, selectedDay);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(Button button) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minute;
                    button.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                }, selectedHour, selectedMinute, true);
        timePickerDialog.show();
    }

    private int getSnoozeInterval() {
        String selectedInterval = snoozeIntervalSpinner.getSelectedItem().toString();
        switch (selectedInterval) {
            case "5 minutes":
                return 5;
            case "10 minutes":
                return 10;
            case "15 minutes":
                return 15;
            case "30 minutes":
                return 30;
            default:
                return 0; // Default value if none selected
        }
    }

    private int getSnoozeRepeat() {
        String selectedRepeat = snoozeRepeatSpinner.getSelectedItem().toString();
        switch (selectedRepeat) {
            case "1 time":
                return 1;
            case "3 times":
                return 3;
            case "5 times":
                return 5;
            default:
                return 0;
        }
    }

    private void saveTask() {
        String title = taskTitle.getText().toString().trim();
        String description = taskDescription.getText().toString().trim();
        String priority = prioritySpinner.getSelectedItem().toString();
        boolean isCompleted = completionStatusSwitch.isChecked();
        String dueDate = dueDateButton.getText().toString();
        String dueTime = dueTimeButton.getText().toString();
        String reminderDate = reminderDateButton.getText().toString();
        String reminderTime = reminderTimeButton.getText().toString();

        int snoozeInterval = getSnoozeInterval();
        int snoozeRepeat = getSnoozeRepeat();

        // Validate input fields
        if (title.isEmpty() || description.isEmpty() || dueDate.equals("Select Date") || dueTime.equals("Select Time") ||
                reminderDate.equals("Select Date") || reminderTime.equals("Select Time")) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task(title, description, isCompleted, priority, dueDate, dueTime, 0, userEmail, reminderDate, reminderTime, false, snoozeInterval, snoozeRepeat);
        boolean isInserted = dbHelper.addTask(task);

        if (isInserted) {
            scheduleNotification(task);
            Toast.makeText(this, "Task saved successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Error saving task", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "TaskReminderChannel";
            CharSequence channelName = "Task Reminder Notifications";
            String channelDescription = "Channel for Task Reminder Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void scheduleNotification(Task task) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);

        // Intent for the primary notification
        Intent intent = new Intent(this, ReminderBroadcast.class);
        intent.putExtra("taskTitle", task.getTitle());
        intent.putExtra("snoozeRemaining", task.getSnoozeRepeat());
        intent.putExtra("snoozeInterval", task.getSnoozedTime());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                dbHelper.getTaskId(task.getTitle(), task.getDueDate(), task.getDueTime()),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Set the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        // Build the notification
        Intent snoozeIntent = new Intent(this, ReminderBroadcast.class);
        snoozeIntent.setAction("ACTION_SNOOZE");
        snoozeIntent.putExtra("taskTitle", task.getTitle());
        snoozeIntent.putExtra("snoozeRemaining", task.getSnoozeRepeat());
        snoozeIntent.putExtra("snoozeInterval", task.getSnoozedTime());
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                this,
                dbHelper.getTaskId(task.getTitle(), task.getDueDate(), task.getDueTime()) + 1,
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle("Task Reminder")
                .setContentText("Reminder for: " + task.getTitle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_snooze, "Snooze", snoozePendingIntent); // Add snooze action

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(
                dbHelper.getTaskId(task.getTitle(), task.getDueDate(), task.getDueTime()),
                builder.build()
        );
    }

}
