package com.example.otams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * LoginActivity allows users to log in to the application.
 * It handles both regular user and admin login.
 */
public class LoginActivity extends AppCompatActivity {
    private Database db;
    private TextView loginTextView;
    private EditText EmailView;
    private EditText PasswordView;

    /**
     * Initializes the activity, sets up the user interface, and handles user interactions.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize the database and UI elements
        db = new Database(this);
        EmailView = findViewById(R.id.editTextTextEmailAddress);
        PasswordView = findViewById(R.id.editTextTextPassword);
        loginTextView = findViewById(R.id.textView);

        // Link to the main activity for users who need to register
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Set up the login button
        Button loginButton = findViewById(R.id.Loginbtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    /**
     * Handles the login process.
     * It validates user input, checks credentials, and navigates to the appropriate activity.
     */
    private void login() {
        String email = EmailView.getText().toString();
        String password = PasswordView.getText().toString();

        // Validate user input
        if (email.isEmpty()) {
            EmailView.setError("Email is required");
            EmailView.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            PasswordView.setError("Password is required");
            PasswordView.requestFocus();
            return;
        }

        // Check for admin credentials
        if (email.equals("admin@otams.ca") && password.equals("admin")) {
            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Check for regular user credentials
        if (db.checkUser(email, password)) {
            String userRole = db.getUserRole(email, password);

            if ("Tutor".equalsIgnoreCase(userRole)){
                Intent intent = new Intent(LoginActivity.this, TutorConsoleActivity.class);
                intent.putExtra("role", userRole);
                intent.putExtra("tutorId", db.getUserIdByEmail(email));
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                intent.putExtra("role", userRole);
                startActivity(intent);
                finish();
            }
            return;
        } else { // Handle failed login
            String status = db.getUserRegistrationStatus(email, password);
            if (status != null) {
                if (status.equals("pending approval")) {
                    PasswordView.setError("Your registration is pending approval");
                } else if (status.equals("Rejected")) {
                    PasswordView.setError("Your registration has been rejected, please contact 613 777 6789");
                } else {
                    PasswordView.setError("Invalid email or password");
                }
            } else {
                PasswordView.setError("Invalid email or password");
            }
            PasswordView.requestFocus();
        }
    }
}
