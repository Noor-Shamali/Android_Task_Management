package com.example.project;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
    private EditText searchBar;
    private Button searchButton;
    private TaskAdapter taskAdapter;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tasks);

        recyclerViewSearchTasks = findViewById(R.id.recyclerViewSearchTasks);
        startDateTextView = findViewById(R.id.start_date);
        endDateTextView = findViewById(R.id.end_date);
        searchBar = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);

        recyclerViewSearchTasks.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new DataBaseHelper(this);

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
                String keyword = searchBar.getText().toString();

                List<Task> filteredTasks = dbHelper.searchTasks(keyword, startDateStr, endDateStr);
                if (filteredTasks.isEmpty()) {
                    Toast.makeText(SearchTasksActivity.this, "No tasks found", Toast.LENGTH_SHORT).show();
                }
                taskAdapter = new TaskAdapter(filteredTasks);
                recyclerViewSearchTasks.setAdapter(taskAdapter);
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

}