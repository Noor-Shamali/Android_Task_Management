package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class GetTasks extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_tasks);

        userEmail = getIntent().getStringExtra("email");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ConnectionAsyncTask connectionAsyncTask = new ConnectionAsyncTask(this);
        connectionAsyncTask.execute("https://mocki.io/v1/b60a63e1-2462-4599-990b-9f6c8d6b832c");

    }

    public void setTasksToRecyclerView(List<Task> tasks) {
        if (tasks != null && !tasks.isEmpty()) {
            // Initialize and set the adapter
            taskAdapter = new TaskAdapter(tasks);
            recyclerView.setAdapter(taskAdapter);
        } else {
            // Show a message if no tasks are available
            Toast.makeText(this, "No tasks found!", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveToDataBase(List<Task> tasks) {
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        for (Task task : tasks) {
            if(!dbHelper.isTaskStored(task.getTitle(), task.getDueDate(), task.getDueTime())){
                task.setUserEmail(userEmail);
                dbHelper.addTask(task);
            }
        }
    }
}
