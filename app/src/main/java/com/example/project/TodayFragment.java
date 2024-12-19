package com.example.project;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TodayFragment extends Fragment {
    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private Spinner sortSpinner;
    private LinearLayout sortContainer;

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

        // Initialize sorting dropdown
        sortContainer = view.findViewById(R.id.sortContainer);
        sortSpinner = view.findViewById(R.id.sortSpinner);
        setupSortSpinner(sortSpinner);



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

        // Separate completed and incomplete tasks
        List<Task> incompleteTasks = new ArrayList<>();
        boolean allCompleted = true;

        if (todaysTasks != null) {
            for (Task task : todaysTasks) {
                if (!task.isCompleted()) {
                    incompleteTasks.add(task);
                    allCompleted = false;
                }
            }
        }

        // Get views
        TextView todayTitle = view.findViewById(R.id.todayTitle);
        TextView emptyView = view.findViewById(R.id.empty_view);
        ImageView congratulationFrame = view.findViewById(R.id.congratulationFrame);

        if (todaysTasks == null || todaysTasks.isEmpty()) {
            // No tasks for today
            todayTitle.setVisibility(View.GONE); // Hide the title
            recyclerViewTasks.setVisibility(View.GONE); // Hide RecyclerView
            emptyView.setVisibility(View.VISIBLE); // Show empty view
            congratulationFrame.setVisibility(View.GONE); // Hide congratulation animation
            sortSpinner.setVisibility(View.GONE); // Hide Spinner
            sortContainer.setVisibility(View.GONE);
        } else if (allCompleted) {
            // All tasks are completed
            todayTitle.setVisibility(View.GONE); // Hide the title
            recyclerViewTasks.setVisibility(View.GONE); // Hide RecyclerView
            emptyView.setVisibility(View.GONE); // Hide empty view
            congratulationFrame.setVisibility(View.VISIBLE); // Show congratulation animation
            sortSpinner.setVisibility(View.GONE); // Hide Spinner
            sortContainer.setVisibility(View.GONE);

            // Start the animation
            congratulationFrame.post(() -> {
                AnimationDrawable animation = (AnimationDrawable) congratulationFrame.getBackground();
                animation.start();
            });

            // Display Toast message
            Toast.makeText(getContext(), "Congratulations! You've completed all tasks for today!", Toast.LENGTH_LONG).show();
        } else {
            // Display incomplete tasks
            todayTitle.setVisibility(View.VISIBLE); // Show the title
            recyclerViewTasks.setVisibility(View.VISIBLE); // Show RecyclerView
            emptyView.setVisibility(View.GONE); // Hide empty view
            congratulationFrame.setVisibility(View.GONE); // Hide congratulation animation
            sortSpinner.setVisibility(View.VISIBLE); // Show Spinner
            sortContainer.setVisibility(View.VISIBLE);

            // Update task list and refresh adapter
            taskList.clear();
            taskList.addAll(incompleteTasks);
            taskAdapter.notifyDataSetChanged();
        }

        Log.d("TodayFragment", "Spinner Visibility: " + sortSpinner.getVisibility());

    }


    private void setupSortSpinner(Spinner sortSpinner) {
        // Create sorting options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.sort_options, // Defined in res/values/strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        // Set sorting functionality
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Default order
                        loadTodayTasks(getView());
                        break;
                    case 1: // Sort by priority (High to Low)
                        sortTasksByPriority();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void sortTasksByPriority() {
        // Sort taskList based on priority (High > Medium > Low)
        Collections.sort(taskList, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return getPriorityValue(task2.getPriority()) - getPriorityValue(task1.getPriority());
            }
        });

        // Refresh adapter
        taskAdapter.notifyDataSetChanged();
    }

    private int getPriorityValue(String priority) {
        switch (priority.toLowerCase()) {
            case "high":
                return 3;
            case "medium":
                return 2;
            case "low":
                return 1;
            default:
                return 0; // Default for unknown priorities
        }
    }
}
