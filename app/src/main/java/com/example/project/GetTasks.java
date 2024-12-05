package com.example.project;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_tasks);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ConnectionAsyncTask connectionAsyncTask = new ConnectionAsyncTask(this);
        connectionAsyncTask.execute("https://mocki.io/v1/fff163db-e71b-4d95-9274-ed668e13afb0");

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
}
