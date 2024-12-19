package com.example.project;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private TextView usernameText;
    private Button saveButton;
    private DataBaseHelper dbHelper;
    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        usernameText = findViewById(R.id.user_name_text_view);
        saveButton = findViewById(R.id.save_button);
        dbHelper = new DataBaseHelper(this);
        String email = getIntent().getStringExtra("email");
        // Load current email, first name, and last name
        emailEditText.setText(email);
        usernameText.setText(dbHelper.getUserName(email));
        sharedPreference = new SharedPreference(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!isValidEmail(email)) {
                    Toast.makeText(ProfileActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!getIntent().getStringExtra("email").equals(email)
                        && dbHelper.isEmailUsed(email)){
                    Toast.makeText(ProfileActivity.this, "Email address is used", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!TextUtils.isEmpty(password) && !isValidPassword(password)) {
                    Toast.makeText(ProfileActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                    return;
                }

                showConfirmationDialog(email, password);
            }
        });
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.length() <= 12 &&
                password.matches(".*[A-Z].*")&&
                password.matches(".*\\d.*") &&
                password.matches(".*[a-z].*");
    }

    private void showConfirmationDialog(String email, String password) {
        boolean isEmailChanged = false, isPasswordChanged = false;
        String oldEmail = getIntent().getStringExtra("email");
        if(!oldEmail.equals(email)){
            isEmailChanged = true;
        }
        if(password != null && !password.isEmpty()){
            isPasswordChanged = true;
        }

        boolean finalIsEmailChanged = isEmailChanged;
        boolean finalIsPasswordChanged = isPasswordChanged;

        new AlertDialog.Builder(this)
                .setTitle("Confirm Changes")
                .setMessage("Are you sure you want to save these changes?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(finalIsEmailChanged){
                            saveEmail(email);
                        }
                        if (finalIsPasswordChanged){
                            savePassword(password);
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void saveEmail(String email) {
        sharedPreference.saveEmail(email);
        String oldEmail = getIntent().getStringExtra("email");
        dbHelper.updateEmail(oldEmail, email);
        getIntent().putExtra("email", email);
        // TODO propagate new email to home activity.
        Toast.makeText(ProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void savePassword(String password) {
        dbHelper.updatePassword(getIntent().getStringExtra("email"), password);
        Toast.makeText(ProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
    }
}