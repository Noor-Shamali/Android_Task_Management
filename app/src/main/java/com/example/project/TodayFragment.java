package com.example.project;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TodayFragment extends Fragment {
    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    public TodayFragment() {
        // Required empty public constructor
    }

    public static TodayFragment newInstance() {
        return new TodayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        // Initialize RecyclerView
        recyclerViewTasks = view.findViewById(R.id.recyclerViewTasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the task list and adapter
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerViewTasks.setAdapter(taskAdapter);

        // Load today's tasks
        loadTodayTasks(view);

        return view;
    }

    private void loadTodayTasks(View view) {
        // Get today's date in "dd/MM/yyyy" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = dateFormat.format(new Date());

        // Access the database to get today's tasks
        DataBaseHelper dbHelper = new DataBaseHelper(getContext());
        List<Task> todaysTasks = dbHelper.getTodaysTasks(todayDate);

        // Get views
        TextView todayTitle = view.findViewById(R.id.todayTitle);
        TextView emptyView = view.findViewById(R.id.empty_view);

        if (todaysTasks == null || todaysTasks.isEmpty()) {
            // No tasks for today
            todayTitle.setVisibility(View.GONE); // Keep the title visible
            recyclerViewTasks.setVisibility(View.GONE); // Hide RecyclerView
            emptyView.setVisibility(View.VISIBLE); // Show empty view
        } else {
            // Tasks found
            todayTitle.setVisibility(View.VISIBLE); // Show the title
            recyclerViewTasks.setVisibility(View.VISIBLE); // Show RecyclerView
            emptyView.setVisibility(View.GONE); // Hide empty view

            // Update task list and refresh adapter
            taskList.clear();
            taskList.addAll(todaysTasks);
            taskAdapter.notifyDataSetChanged();
        }
    }
}
