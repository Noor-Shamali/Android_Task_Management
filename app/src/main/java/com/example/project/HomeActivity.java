package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private final Map<Integer, Runnable> navigationActions = new HashMap<>();
    String email;
    TodayFragment todayFragment;
    String UserName;
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize shared preference object
        sharedPreference = new SharedPreference(this);

        email = getIntent().getStringExtra("userEmail");
        UserName = getIntent().getStringExtra("userName");

        // Set up Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView moonIcon = toolbar.findViewById(R.id.moon_icon);
        moonIcon.setOnClickListener(v -> toggleNightMode());  // Toggle night mode on click

        // Set up DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set up NavigationView
        navigationView = findViewById(R.id.nav_view);

        // Set user details in the header
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_name);
        TextView userEmailTextView = headerView.findViewById(R.id.user_email);
        userNameTextView.setText(UserName);
        userEmailTextView.setText(email);

        // Load default fragment
        todayFragment = new TodayFragment();
        loadFragment(todayFragment);

        // Initialize the navigation actions
        initializeNavigationActions();

        // Set NavigationItemSelectedListener
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private void initializeNavigationActions() {
        navigationActions.put(R.id.nav_today, () -> loadFragment(todayFragment));
        navigationActions.put(R.id.nav_new_task, this::getToNewActivity);
        navigationActions.put(R.id.nav_all, this::getToAllTasksActivity);
        navigationActions.put(R.id.nav_search, this::goToSearchTasksActivity);
        navigationActions.put(R.id.nav_completed, this::getToCompletedTasksActivity);
        navigationActions.put(R.id.nav_get_tasks, this::goToGetTasks);
        navigationActions.put(R.id.nav_profile, this::getMyProfile);
        navigationActions.put(R.id.nav_logout, () -> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });
    }

    private void getMyProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Runnable action = navigationActions.get(item.getItemId());
        if (action != null) {
            action.run();
            drawerLayout.closeDrawers(); // Close the navigation drawer
            return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        // Check if fragment is already in the fragment manager before replacing
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }

    private void getToNewActivity() {
        Intent intent = new Intent(this, NewTaskActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private void goToGetTasks() {
        Intent intent = new Intent(this, GetTasks.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private void getToAllTasksActivity() {
        Intent intent = new Intent(this, AllTasksActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private void getToCompletedTasksActivity() {
        Intent intent = new Intent(this, CompletedTasksActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private void goToSearchTasksActivity() {
        Intent intent = new Intent(this, SearchTasksActivity.class);
        startActivity(intent);
    }

    // Toggle night mode
    private void toggleNightMode() {
        boolean currentNightMode = sharedPreference.readDarkMode("Mode",false);
        if (currentNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Switch to light mode
            sharedPreference.writeDarkMode("Mode", false);  // Save mode preference
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Switch to dark mode
            sharedPreference.writeDarkMode("Mode", true);  // Save mode preference
        }
    }
}
