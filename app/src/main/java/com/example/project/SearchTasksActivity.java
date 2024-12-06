package com.example.project;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchTasksActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSearchTasks;
    private TextView startDateTextView, endDateTextView;
    private Button searchButton;
    private TaskAdapter taskAdapter;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tasks);

        recyclerViewSearchTasks = findViewById(R.id.recyclerViewSearchTasks);
        startDateTextView = findViewById(R.id.start_date);
        endDateTextView = findViewById(R.id.end_date);
        searchButton = findViewById(R.id.search_button);

        recyclerViewSearchTasks.setLayoutManager(new LinearLayoutManager(this));

        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startDateTextView);
            }
        });

        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(endDateTextView);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDateStr = startDateTextView.getText().toString();
                String endDateStr = endDateTextView.getText().toString();

                if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                    Toast.makeText(SearchTasksActivity.this, "Please enter both start and end dates", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Date startDate = sdf.parse(startDateStr);
                    Date endDate = sdf.parse(endDateStr);

                    if (startDate != null && endDate != null) {
                        searchTasks(startDate, endDate);
                    }
                } catch (ParseException e) {
                    Toast.makeText(SearchTasksActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePickerDialog(final TextView dateTextView) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        dateTextView.setText(sdf.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void searchTasks(Date startDate, Date endDate) {
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        List<Task> allTasks = dbHelper.getAllTasks();
        List<Task> filteredTasks = new ArrayList<>();

        for (Task task : allTasks) {
            try {
                Date taskDate = sdf.parse(task.getDueDate());

                if (taskDate != null && !taskDate.before(startDate) && !taskDate.after(endDate)) {
                    filteredTasks.add(task);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (filteredTasks.isEmpty()) {
            Toast.makeText(this, "No tasks found in the specified period", Toast.LENGTH_SHORT).show();
        } else {
            taskAdapter = new TaskAdapter(filteredTasks);
            recyclerViewSearchTasks.setAdapter(taskAdapter);
        }
    }
}