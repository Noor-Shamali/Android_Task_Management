package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AllTasksActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAllTasks;
    private TextView emptyView;
    private Spinner sortSpinner;

    private Map<String, List<Task>> groupedTasks = new TreeMap<>();
    private AllTasksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        recyclerViewAllTasks = findViewById(R.id.recyclerViewAllTasks);
        emptyView = findViewById(R.id.emptyView);
        sortSpinner = findViewById(R.id.sortSpinner);

        List<Task> taskList = fetchTasks();
        groupedTasks = groupTasksByDate(taskList);

        if (groupedTasks.isEmpty()) {
            showEmptyView();
        } else {
            setupRecyclerView();
            setupSortSpinner(sortSpinner);
        }
    }

    private List<Task> fetchTasks() {
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        return dbHelper.getAllTasks();
    }

    private Map<String, List<Task>> groupTasksByDate(List<Task> tasks) {
        Map<String, List<Task>> grouped = new TreeMap<>();
        for (Task task : tasks) {
            String date = task.getDueDate();
            grouped.computeIfAbsent(date, k -> new ArrayList<>()).add(task);
        }
        return grouped;
    }

    private void sortTasksByPriority(Map<String, List<Task>> tasks) {
        for (List<Task> taskGroup : tasks.values()) {
            Collections.sort(taskGroup, Comparator.comparingInt(this::getPriorityValue).reversed());
        }
    }

    private int getPriorityValue(Task task) {
        switch (task.getPriority().toLowerCase()) {
            case "high": return 3;
            case "medium": return 2;
            case "low": return 1;
            default: return 0;
        }
    }

    private void setupRecyclerView() {
        emptyView.setVisibility(View.GONE);
        recyclerViewAllTasks.setVisibility(View.VISIBLE);

        adapter = new AllTasksAdapter(groupedTasks);
        recyclerViewAllTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAllTasks.setAdapter(adapter);
    }

    private void setupSortSpinner(Spinner sortSpinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.sort_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    groupedTasks = groupTasksByDate(fetchTasks());
                } else if (position == 1) {
                    sortTasksByPriority(groupedTasks);
                }
                updateRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateRecyclerView() {
        adapter = new AllTasksAdapter(groupedTasks);
        recyclerViewAllTasks.setAdapter(adapter);
    }

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        recyclerViewAllTasks.setVisibility(View.GONE);
    }
}
