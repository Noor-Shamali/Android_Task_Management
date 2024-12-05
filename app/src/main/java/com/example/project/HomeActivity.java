package com.example.project;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private final Map<Integer, Runnable> navigationActions = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set up Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set up NavigationView
        navigationView = findViewById(R.id.nav_view);

        // Initialize the navigation actions
        initializeNavigationActions();

        // Set NavigationItemSelectedListener
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private void initializeNavigationActions() {
     //   navigationActions.put(R.id.nav_today, () -> loadFragment(new TodayFragment()));
       navigationActions.put(R.id.nav_new_task, () -> startActivity(new Intent(this, NewTaskActivity.class)));
        //navigationActions.put(R.id.nav_all, () -> loadFragment(new AllTasksFragment()));
        //navigationActions.put(R.id.nav_completed, () -> loadFragment(new CompletedTasksFragment()));
        //navigationActions.put(R.id.nav_search, () -> loadFragment(new SearchFragment()));
        navigationActions.put(R.id.nav_get_tasks, () -> startActivity(new Intent(this, GetTasks.class)));
        navigationActions.put(R.id.nav_logout, () -> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });
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
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }
}
