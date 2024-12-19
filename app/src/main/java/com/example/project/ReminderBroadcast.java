package com.example.project;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.project.R;

import java.util.Calendar;

public class ReminderBroadcast extends BroadcastReceiver {
    private static final String MY_CHANNEL_ID = "TaskReminderChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String taskTitle = intent.getStringExtra("taskTitle");
        int snoozeRemaining = intent.getIntExtra("snoozeRemaining", 0);
        int snoozeInterval = intent.getIntExtra("snoozeInterval", 0);

        if ("ACTION_SNOOZE".equals(action)) {
            if (snoozeRemaining > 0) {
                // Schedule the snoozed notification
                Calendar snoozeTime = Calendar.getInstance();
                snoozeTime.add(Calendar.MINUTE, snoozeInterval);

                Intent snoozeIntent = new Intent(context, ReminderBroadcast.class);
                snoozeIntent.putExtra("taskTitle", taskTitle);
                snoozeIntent.putExtra("snoozeRemaining", snoozeRemaining - 1);
                snoozeIntent.putExtra("snoozeInterval", snoozeInterval);
                PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                        context,
                        (int) System.currentTimeMillis(),
                        snoozeIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            snoozeTime.getTimeInMillis(),
                            snoozePendingIntent
                    );
                }

                // Show a toast or log for debugging
                Toast.makeText(context, "Snoozed for " + snoozeInterval + " minutes.", Toast.LENGTH_SHORT).show();
            } else {
                // No snooze remaining
                Toast.makeText(context, "No more snoozes left.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the primary notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MY_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_reminder)
                    .setContentTitle("Task Reminder")
                    .setContentText("Reminder for: " + taskTitle)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
