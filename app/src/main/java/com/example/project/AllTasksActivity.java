package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AllTasksActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAllTasks;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        recyclerViewAllTasks = findViewById(R.id.recyclerViewAllTasks);
        emptyView = findViewById(R.id.emptyView);

        // Sample tasks (replace with your database or API call)
        List<Task> taskList = fetchTasks();

        // Group tasks by date
        Map<String, List<Task>> groupedTasks = groupTasksByDate(taskList);

        if (groupedTasks.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);

            // Set up the adapter
            AllTasksAdapter adapter = new AllTasksAdapter(groupedTasks);
            recyclerViewAllTasks.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewAllTasks.setAdapter(adapter);
        }
    }

    private List<Task> fetchTasks() {
        DataBaseHelper dbHelper = new DataBaseHelper(this); // Pass the context to the helper
        return dbHelper.getAllTasks(); // Fetch tasks from the database
    }

    private Map<String, List<Task>> groupTasksByDate(List<Task> tasks) {
        Map<String, List<Task>> groupedTasks = new TreeMap<>(); // TreeMap to keep keys sorted by date
        for (Task task : tasks) {
            String date = task.getDueDate();
            if (!groupedTasks.containsKey(date)) {
                groupedTasks.put(date, new ArrayList<>());
            }
            groupedTasks.get(date).add(task);
        }
        return groupedTasks;
    }
}
