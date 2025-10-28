package com.example.otams;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * RegistrationStatusActivity displays the details of a single registration request
 * and allows the admin to take action on it (approve, reject, etc.).
 */
public class RegistrationStatusActivity extends AppCompatActivity {
    private Database db;

    /**
     * Initializes the activity, sets up the user interface, and displays the request details.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_status);
        db = new Database(this);

        // Get the registration request from the intent
        RegistrationRequest request = (RegistrationRequest) getIntent().getSerializableExtra("request");
        if (request != null) {
            displayRequestDetails(request);
        } else {
            Log.e("RegistrationStatusActivity", "Request data is null");
            finish();
        }

        // Set up the return button
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> finish());
    }

    /**
     * Displays the details of the registration request in the UI.
     *
     * @param request The RegistrationRequest object to display.
     */
    private void displayRequestDetails(RegistrationRequest request) {
        // Initialize UI elements
        TextView nameText = findViewById(R.id.nameText);
        TextView emailText = findViewById(R.id.emailText);
        TextView phoneText = findViewById(R.id.phoneText);
        TextView roleText = findViewById(R.id.roleText);
        TextView statusText = findViewById(R.id.statusText);

        View programLayout = findViewById(R.id.programLayout);
        TextView programTxt = findViewById(R.id.programTxt);
        View degreeLayout = findViewById(R.id.degreeLayout);
        TextView degreeTxt = findViewById(R.id.degreeTxt);
        View coursesLayout = findViewById(R.id.coursesLayout);
        TextView courseTxt = findViewById(R.id.courseTxt);

        // Set the text for the UI elements
        nameText.setText(request.getFirstName() + " " + request.getLastName());
        emailText.setText(request.getEmail());
        phoneText.setText(request.getPhoneNum());
        roleText.setText(request.getRole());
        statusText.setText(request.getStatus());

        // Show/hide fields based on the user's role
        if ("Student".equals(request.getRole())) {
            programLayout.setVisibility(View.VISIBLE);
            programTxt.setText(request.getProgram());
            degreeLayout.setVisibility(View.GONE);
            coursesLayout.setVisibility(View.GONE);
        } else if ("Tutor".equals(request.getRole())) {
            degreeLayout.setVisibility(View.VISIBLE);
            coursesLayout.setVisibility(View.VISIBLE);
            degreeTxt.setText(request.getDegree());
            courseTxt.setText(request.getCourse());
            programLayout.setVisibility(View.GONE);
        } else {
            programLayout.setVisibility(View.GONE);
            degreeLayout.setVisibility(View.GONE);
            coursesLayout.setVisibility(View.GONE);
        }

        // Set up the action buttons
        setupActionButtons(request);
    }

    /**
     * Sets up the action buttons (approve, reject, etc.) based on the status of the request.
     *
     * @param request The RegistrationRequest object.
     */
    private void setupActionButtons(RegistrationRequest request) {
        LinearLayout buttonLayout = findViewById(R.id.buttonLayout);
        buttonLayout.removeAllViews();

        switch (request.getStatus()) {
            case "Under Review":
                // Add "Approve" and "Reject" buttons
                addActionButton("Approve", android.R.color.holo_green_light, v -> {
                    db.approveRegistrationRequest(request.getUserId());
                    finish(); // Go back to the admin screen
                });
                addActionButton("Reject", android.R.color.holo_red_light, v -> {
                    db.rejecteRegistrationRequest(request.getUserId());
                    finish();
                });
                break;
            case "Approved":
                // Add a "Set to Pending" button
                addActionButton("Set to Pending", android.R.color.holo_orange_light, v -> {
                    db.setRegistrationToPending(request.getUserId());
                    finish();
                });
                break;
            case "Rejected":
                // Add "Approve" and "Set to Pending" buttons
                addActionButton("Approve", android.R.color.holo_green_light, v -> {
                    db.approveRegistrationRequest(request.getUserId());
                    finish();
                });
                addActionButton("Set to Pending", android.R.color.holo_orange_light, v -> {
                    db.setRegistrationToPending(request.getUserId());
                    finish();
                });
                break;
        }
    }

    /**
     * Adds a new action button to the button layout.
     *
     * @param text     The text to display on the button.
     * @param colorRes The color resource for the button's background.
     * @param listener The OnClickListener for the button.
     */
    private void addActionButton(String text, int colorRes, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(text);
        button.setBackgroundColor(getResources().getColor(colorRes, null));
        button.setOnClickListener(listener);

        ((LinearLayout) findViewById(R.id.buttonLayout)).addView(button);
    }
}
