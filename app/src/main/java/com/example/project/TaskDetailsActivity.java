package com.example.project;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskDetailsActivity extends AppCompatActivity {

    private Task task; // Assuming Task is a serializable model class
    private DataBaseHelper dbHelper;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Initialize database helper
        dbHelper = new DataBaseHelper(this);

        // Get task object passed from intent
        task = (Task) getIntent().getSerializableExtra("task");

        getCurrentTaskState(task);

        // Bind views
        TextView taskTitle = findViewById(R.id.taskTitle);
        TextView taskDescription = findViewById(R.id.taskDescription);
        TextView taskDueDate = findViewById(R.id.taskDueDate);
        TextView taskPriority = findViewById(R.id.taskPriority);
        TextView taskDueTime = findViewById(R.id.taskDueTime);
        TextView taskIsCompleted = findViewById(R.id.taskIsCompleted);
        Button setNotificationButton = findViewById(R.id.set_notification_button);
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
                    setResult(RESULT_OK, intent);
                    startActivity(intent);
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
                    // send congratulation message when all today tasks are completed
                    if (areAllTodayTasksCompleted()) {
                        Toast.makeText(this, "Congratulations! All tasks for today are completed!", Toast.LENGTH_LONG).show();
                        // Optional: Add animation code here
                    }
                } else {
                    Toast.makeText(this, "Failed to mark task as completed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Task is already completed", Toast.LENGTH_SHORT).show();
            }

            setResult(RESULT_OK, intent);
            startActivity(intent);
            finish();
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
        setNotificationButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(TaskDetailsActivity.this,
                    (view, hourOfDay, minuteOfHour) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minuteOfHour);
                        scheduleNotification(task, calendar.getTimeInMillis());
                    }, hour, minute, true);
            timePickerDialog.show();
        });
        createNotificationChannel();
    }
    private boolean areAllTodayTasksCompleted() {
        List<Task> todayTasks = dbHelper.getTodaysTasks(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        for (Task t : todayTasks) {
            if (!t.isCompleted()) {
                return false;
            }
        }
        return true;
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
                setResult(RESULT_OK, intent);
                startActivity(intent);
                finish();
            }
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


    private void scheduleNotification(Task task, long timeInMillis) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("task_name", task.getTitle());

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        long triggerTime = timeInMillis;

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }

        Toast.makeText(this, "Notification set for " + task.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TaskChannel";
            String description = "Channel for task notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("task_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String taskName = intent.getStringExtra("task_name");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "task_channel")
                    .setContentTitle("Task Reminder")
                    .setContentText("Reminder for task: " + taskName)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (notificationManager.areNotificationsEnabled()) {
                try {
                    notificationManager.notify(1, builder.build());
                } catch (SecurityException e) {
                    Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show();
                }
            } else {
                // TODO fix this to show a dialog or something
                Toast.makeText(context, "Notifications are disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
