package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Project.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PASSWORD = "password";

    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IS_COMPLETED = "isCompleted";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_DUE_DATE = "dueDate";
    private static final String COLUMN_DUE_TIME = "dueTime";
    private static final String COLUMN_REMIND = "remind";
    private static final String COLUMN_USER_EMAIL = "userEmail";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + EMAIL + " TEXT PRIMARY KEY, "
                + FIRST_NAME + " TEXT, "
                + LAST_NAME + " TEXT, "
                + PASSWORD + " TEXT);";
        db.execSQL(createUsersTable);

        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_IS_COMPLETED + " INTEGER, "
                + COLUMN_PRIORITY + " TEXT, "
                + COLUMN_DUE_DATE + " TEXT, "
                + COLUMN_DUE_TIME + " TEXT, "
                + COLUMN_REMIND + " INTEGER, "
                + COLUMN_USER_EMAIL + " TEXT)";
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public boolean addUser(String email, String firstName, String lastName, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EMAIL, email);
        values.put(FIRST_NAME, firstName);
        values.put(LAST_NAME, lastName);
        values.put(PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues object to hold task data
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_DUE_TIME, task.getDueTime());
        values.put(COLUMN_REMIND, task.getRemind());
        values.put(COLUMN_USER_EMAIL, task.getUserEmail());

        // Insert the task into the database
        long result = db.insert(TABLE_TASKS, null, values);

        db.close();

        // If result is -1, insertion failed, otherwise it was successful
        return result != -1;
    }


    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + EMAIL + " = ? AND " + PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }

    public boolean isEmailUsed(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean emailExists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return emailExists;
    }

    public List<Task> getTodaysTasks(String todayDate) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to fetch tasks with today's date
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_DUE_DATE + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{todayDate});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Task task = new Task();

                // Safely check for column index and retrieve data
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                if (titleIndex >= 0) {
                    task.setTitle(cursor.getString(titleIndex));
                }

                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                if (descriptionIndex >= 0) {
                    task.setDescription(cursor.getString(descriptionIndex));
                }

                int isCompletedIndex = cursor.getColumnIndex(COLUMN_IS_COMPLETED);
                if (isCompletedIndex >= 0) {
                    task.setCompleted(cursor.getInt(isCompletedIndex) == 1);
                }

                int priorityIndex = cursor.getColumnIndex(COLUMN_PRIORITY);
                if (priorityIndex >= 0) {
                    task.setPriority(cursor.getString(priorityIndex));
                }

                int dueDateIndex = cursor.getColumnIndex(COLUMN_DUE_DATE);
                if (dueDateIndex >= 0) {
                    task.setDueDate(cursor.getString(dueDateIndex));
                }

                int dueTimeIndex = cursor.getColumnIndex(COLUMN_DUE_TIME);
                if (dueTimeIndex >= 0) {
                    task.setDueTime(cursor.getString(dueTimeIndex));
                }

                int remindIndex = cursor.getColumnIndex(COLUMN_REMIND);
                if (remindIndex >= 0) {
                    task.setRemind(cursor.getInt(remindIndex));
                }

                int userEmailIndex = cursor.getColumnIndex(COLUMN_USER_EMAIL);
                if (userEmailIndex >= 0) {
                    task.setUserEmail(cursor.getString(userEmailIndex));
                }

                taskList.add(task);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return taskList;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + FIRST_NAME + ", " + LAST_NAME + " FROM " + TABLE_USERS + " WHERE " + EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        String userName = null;

        if (cursor != null && cursor.moveToFirst()) {
            int firstNameIndex = cursor.getColumnIndex(FIRST_NAME);
            int lastNameIndex = cursor.getColumnIndex(LAST_NAME);

            // Check if the columns exist
            if (firstNameIndex >= 0 && lastNameIndex >= 0) {
                String firstName = cursor.getString(firstNameIndex);
                String lastName = cursor.getString(lastNameIndex);
                userName = firstName + " " + lastName; // Combine first and last name
            } else {
                // Handle the case when columns are not found
                Log.e("Database", "Column names not found in the query result.");
            }

            cursor.close();
        } else {
            // Handle case where no data is found for the given email
            Log.e("Database", "No user found with email: " + email);
        }

        db.close();
        return userName;
    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Extract task data from cursor
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1;
                String priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE));
                String dueTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_TIME));
                int remind = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REMIND));
                String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL));

                // Create Task object and add to list
                Task task = new Task(title, description, isCompleted, priority, dueDate, dueTime, remind, userEmail);
                taskList.add(task);
            }
            cursor.close();
        }

        db.close();
        return taskList;
    }

    public boolean isTaskStored(String title, String dueDate, String dueTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE "
                + COLUMN_TITLE + " = ? AND "
                + COLUMN_DUE_DATE + " = ? AND "
                + COLUMN_DUE_TIME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{title, dueDate, dueTime});

        boolean exists = false;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                exists = cursor.getInt(0) > 0; // If count > 0, the task exists
            }
            cursor.close();
        }
        db.close();
        return exists;
    }

    public List<Task> getCompletedTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_IS_COMPLETED + " = 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Extract task data from cursor
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1;
                String priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE));
                String dueTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_TIME));
                int remind = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REMIND));
                String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL));

                // Create Task object and add to list
                Task task = new Task(title, description, isCompleted, priority, dueDate, dueTime, remind, userEmail);
                taskList.add(task);
            }
            cursor.close();
        }

        db.close();
        return taskList;
    }

}
