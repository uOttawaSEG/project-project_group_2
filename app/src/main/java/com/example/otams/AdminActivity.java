package com.example.otams;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

/**
 * AdminActivity displays registration requests and allows the admin to approve or reject them.
 */
public class AdminActivity extends AppCompatActivity {
    private Database db;
    private TextView titleText;
    private LinearLayout contentLayout;
    private String currentFilter;

    /**
     * Initializes the activity, sets up the user interface, and displays the initial list of pending requests.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize the database and UI elements
        db = new Database(this);
        contentLayout = findViewById(R.id.contentLayout);
        titleText = findViewById(R.id.titleText);

        // Set up buttons for filtering requests
        setupButton(R.id.pendingBtn, "Pending", "pending approval");
        setupButton(R.id.approvedBtn, "Approved", "Approved");
        setupButton(R.id.rejectedBtn, "Rejected", "Rejected");

        // Button to return to the main activity
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish AdminActivity when returning
        });

        // Show pending requests by default
        showRequests("Pending", "pending approval");
    }

    /**
     * Sets up a button to filter registration requests based on their status.
     *
     * @param buttonId      The ID of the button.
     * @param title         The title to display when the filter is active.
     * @param statusToFetch The status of requests to fetch from the database.
     */
    private void setupButton(int buttonId, String title, String statusToFetch) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> showRequests(title, statusToFetch));
    }

    /**
     * Fetches and displays registration requests based on the selected filter.
     *
     * @param title         The title to display for the list of requests.
     * @param statusToFetch The status of requests to fetch.
     */
    private void showRequests(String title, String statusToFetch) {
        titleText.setText(title);
        currentFilter = statusToFetch;

        contentLayout.removeAllViews();
        List<RegistrationRequest> requests;

        // Fetch the list of requests based on their status
        if ("pending approval".equals(statusToFetch)) {
            requests = db.getPendingRegistrationRequests();
        } else if ("Approved".equals(statusToFetch)) {
            requests = db.getApprovedRegistrationRequests();
        } else {
            requests = db.getRejectedRegistrationRequests();
        }

        // Display a message if no requests are found
        if (requests.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No requests found.");
            emptyText.setPadding(10, 20, 10, 20);
            contentLayout.addView(emptyText);
            return;
        }

        // Create and display a layout for each request
        for (int i = 0; i < requests.size(); i++) {
            RegistrationRequest request = requests.get(i);
            LinearLayout itemLayout = createRequestItemLayout(request);
            contentLayout.addView(itemLayout);

            // Add a separator between items
            if (i < requests.size() - 1) {
                View separator = new View(this);
                separator.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2 // height of 2 pixels
                ));
                TypedValue separatorColor = new TypedValue();
                getTheme().resolveAttribute(android.R.attr.listDivider, separatorColor, true);
                separator.setBackgroundColor(separatorColor.data);
                contentLayout.addView(separator);
            }
        }
    }

    /**
     * Creates a layout to display the details of a single registration request.
     *
     * @param request The RegistrationRequest object to display.
     * @return A LinearLayout containing the request details.
     */
    private LinearLayout createRequestItemLayout(RegistrationRequest request) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(16, 16, 16, 16);
        itemLayout.setClickable(true);

        // Set an OnClickListener to open the RegistrationStatusActivity
        itemLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, RegistrationStatusActivity.class);
            intent.putExtra("request", request);
            startActivity(intent);
        });

        // Display user information
        TextView nameText = new TextView(this);
        nameText.setText(request.getFirstName() + " " + request.getLastName());
        itemLayout.addView(nameText);
        TextView emailText = new TextView(this);
        emailText.setText(request.getEmail());
        itemLayout.addView(emailText);
        TextView phoneText = new TextView(this);
        phoneText.setText("Phone: " + request.getPhoneNum());
        itemLayout.addView(phoneText);
        TextView roleText = new TextView(this);
        roleText.setText("Role: " + request.getRole());
        itemLayout.addView(roleText);
        TextView statusText = new TextView(this);
        statusText.setText("Status: " + request.getStatus());
        itemLayout.addView(statusText);

        // Display additional information for tutors
        if ("Tutor".equals(request.getRole())) {
            if (request.getDegree() != null && !request.getDegree().isEmpty()) {
                TextView degreeText = new TextView(this);
                degreeText.setText("Degree: " + request.getDegree());
                itemLayout.addView(degreeText);
            }
            if (request.getCourse() != null && !request.getCourse().isEmpty()) {
                TextView courseText = new TextView(this);
                courseText.setText("Course: " + request.getCourse());
                itemLayout.addView(courseText);
            }
        }

        // Display additional information for students
        if ("Student".equals(request.getRole()) && request.getProgram() != null && !request.getProgram().isEmpty()) {
            TextView programText = new TextView(this);
            programText.setText("Program: " + request.getProgram());
            itemLayout.addView(programText);
        }

        // Add approve/reject buttons for pending requests, and an approve button for rejected requests
        if ("pending approval".equals(request.getStatus())) {
            itemLayout.addView(createActionButtons(request));
        } else if ("Rejected".equals(request.getStatus())) {
            itemLayout.addView(createApproveButtonForRejected(request));
        }

        return itemLayout;
    }

    /**
     * Creates a layout with "Approve" and "Reject" buttons for a registration request.
     *
     * @param request The RegistrationRequest to create buttons for.
     * @return A LinearLayout containing the action buttons.
     */
    private LinearLayout createActionButtons(RegistrationRequest request) {
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 16, 0, 0);

        Button approveButton = new Button(this);
        approveButton.setText("Approve");
        approveButton.setOnClickListener(v -> {
            db.approveRegistrationRequest(request.getUserId());
            showRequests(titleText.getText().toString(), currentFilter);
        });

        Button rejectButton = new Button(this);
        rejectButton.setText("Reject");
        rejectButton.setOnClickListener(v -> {
            db.rejecteRegistrationRequest(request.getUserId());
            showRequests(titleText.getText().toString(), currentFilter);
        });

        buttonLayout.addView(approveButton);
        buttonLayout.addView(rejectButton);

        return buttonLayout;
    }

    /**
     * Creates a layout with an "Approve" button for a rejected registration request.
     *
     * @param request The RegistrationRequest to create the button for.
     * @return A LinearLayout containing the action button.
     */
    private LinearLayout createApproveButtonForRejected(RegistrationRequest request) {
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 16, 0, 0);

        Button approveButton = new Button(this);
        approveButton.setText("Approve");
        approveButton.setOnClickListener(v -> {
            db.approveRegistrationRequest(request.getUserId());
            showRequests(titleText.getText().toString(), currentFilter);
        });

        buttonLayout.addView(approveButton);
        return buttonLayout;
    }

    /**
     * Refreshes the list of requests when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (currentFilter != null) {
            showRequests(titleText.getText().toString(), currentFilter);
        }
    }
}
