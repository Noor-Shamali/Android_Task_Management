package com.example.project;

import java.io.Serializable;

public class Task implements Serializable {
    private String title;
    private String description;
    private boolean isCompleted;
    private String priority;
    private String dueDate;
    private String dueTime;
    private int remind;
    private String userEmail;
    private String reminderDate;
    private String reminderTime;
    private boolean snoozed;
    private int snoozedTime;
    private int snoozeRepeat;

    public Task() {
    }

    public Task(String title, String description, boolean isCompleted, String priority, String dueDate, String dueTime, int remind, String userEmail, String reminderDate, String reminderTime, boolean snoozed, int snoozedTime, int snoozeRepeat) {
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.priority = priority;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.remind = remind;
        this.userEmail = userEmail;
        this.reminderDate = reminderDate;
        this.reminderTime = reminderTime;
        this.snoozed = snoozed;
        this.snoozedTime = snoozedTime;
        this.snoozeRepeat=snoozeRepeat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public int getRemind() {
        return remind;
    }

    public void setRemind(int remind) {
        this.remind = remind;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isSnoozed() {
        return snoozed;
    }

    public void setSnoozed(boolean snoozed) {
        this.snoozed = snoozed;
    }

    public int getSnoozedTime() {
        return snoozedTime;
    }

    public void setSnoozedTime(int snoozedTime) {
        this.snoozedTime = snoozedTime;
    }

    public int getSnoozeRepeat() {
        return snoozeRepeat;
    }

    public void setSnoozeRepeat(int snoozeRepeat) {
        this.snoozeRepeat = snoozeRepeat;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isCompleted=" + isCompleted +
                ", priority='" + priority + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", dueTime='" + dueTime + '\'' +
                ", remind=" + remind +
                ", userEmail='" + userEmail + '\'' +
                ", reminderDate='" + reminderDate + '\'' +
                ", reminderTime='" + reminderTime + '\'' +
                ", snoozedTime=" + snoozedTime +
                ", snoozeRepeat=" + snoozeRepeat +
                '}';
    }

    public boolean hasReminder() {
        return reminderDate != null && !reminderDate.isEmpty() &&
                reminderTime != null && !reminderTime.isEmpty();
    }
}
