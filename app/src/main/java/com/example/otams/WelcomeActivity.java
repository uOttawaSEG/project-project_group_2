package com.example.otams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * WelcomeActivity displays a welcome message to the logged-in user.
 */
public class WelcomeActivity extends AppCompatActivity {

    private TextView welcomeText;

    /**
     * Initializes the activity, sets up the user interface, and displays the welcome message.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initialize UI elements
        welcomeText = findViewById(R.id.welcomeText);
        Intent intent = getIntent();

        // Get the user's role from the intent and display a welcome message
        String role = intent.getStringExtra("role");
        if (role == null) {
            welcomeText.setText("Error, you need to login with a role");
        } else {
            String message = "Welcome! you are logged as '" + role + "'";
            welcomeText.setText(message);
        }

        // Set up the log off button
        Button logOffBtn = findViewById(R.id.logOffBtn);
        logOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log off the user and return to the main activity
                finishAffinity();
                Intent intent1 = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });
    }
}
