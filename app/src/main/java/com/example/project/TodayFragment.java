package com.example.project;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
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

        recyclerViewTasks = view.findViewById(R.id.recyclerViewTasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        recyclerViewTasks.setAdapter(taskAdapter);

        // Load today's tasks
        loadTodayTasks(view);  // Pass the 'view' object to loadTodayTasks()

        return view;
    }

    private void loadTodayTasks(View view) {
        // Get today's date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = dateFormat.format(new Date());

        // Access the database to get today's tasks
        DataBaseHelper dbHelper = new DataBaseHelper(getContext());
        List<Task> todaysTasks = dbHelper.getTodaysTasks(todayDate);

        // Get the empty view to show when no tasks are available
        TextView emptyView = view.findViewById(R.id.empty_view);  // Use 'view' here

        if (todaysTasks.isEmpty()) {
            // No tasks for today
            recyclerViewTasks.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            // Tasks found, update the RecyclerView
            recyclerViewTasks.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            // Update task list and notify adapter
            taskList.clear();
            taskList.addAll(todaysTasks);
            taskAdapter.notifyDataSetChanged();
        }
    }
}
