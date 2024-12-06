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

public class CompletedTasksActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCompletedTasks;
    private TextView emptyCompletedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);

        // Initialize views
        recyclerViewCompletedTasks = findViewById(R.id.recyclerViewCompletedTasks);
        emptyCompletedView = findViewById(R.id.emptyCompletedView);

        // Fetch completed tasks
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        List<Task> completedTasks = dbHelper.getCompletedTasks();

        // Check if the list is empty
        if (completedTasks.isEmpty()) {
            emptyCompletedView.setVisibility(View.VISIBLE);
        } else {
            emptyCompletedView.setVisibility(View.GONE);

            // Group tasks by date (optional)
            Map<String, List<Task>> groupedTasks = groupTasksByDate(completedTasks);

            // Set up adapter
            AllTasksAdapter adapter = new AllTasksAdapter(groupedTasks);
            recyclerViewCompletedTasks.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewCompletedTasks.setAdapter(adapter);
        }
    }

    // Helper function to group tasks by date
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
