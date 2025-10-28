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

public class AdminActivity extends AppCompatActivity {
    private Database db;
    private TextView titleText;
    private LinearLayout contentLayout;
    private String currentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        //initialize the database
        db = new Database(this);
        contentLayout = findViewById(R.id.contentLayout);
        titleText = findViewById(R.id.titleText);

        setupButton(R.id.pendingBtn, "Pending", "pending approval");
        setupButton(R.id.approvedBtn, "Approved", "Approved");
        setupButton(R.id.rejectedBtn, "Rejected", "Rejected");


        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish AdminActivity when returning
        });

        showRequests("Pending", "pending approval");
    }

    private void setupButton(int buttonId, String title, String statusToFetch) {
        Button button = findViewById(buttonId);

        button.setOnClickListener(v -> showRequests(title, statusToFetch));
    }


    private void showRequests(String title, String statusToFetch) {
        titleText.setText(title);
        currentFilter = statusToFetch;

        contentLayout.removeAllViews();
        List<RegistrationRequest> requests;



        //fetch list based on status of users
        if ("pending approval".equals(statusToFetch)) {
            requests = db.getPendingRegistrationRequests();
        } else if ("Approved".equals(statusToFetch)) {
            requests = db.getApprovedRegistrationRequests();
        } else {
            requests = db.getRejectedRegistrationRequests();
        }


        if (requests.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No requests found.");
            emptyText.setPadding(10,20,10,20); //unable to gradle task


            contentLayout.addView(emptyText);
            return;
        }


        for (int i = 0; i < requests.size(); i++) {
            RegistrationRequest request = requests.get(i);
            LinearLayout itemLayout = createRequestItemLayout(request);
            contentLayout.addView(itemLayout);

            // Add a separator after each item, except for the last one.
            if (i < requests.size() - 1) {
                View separator = new View(this);
                separator.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2 // height of 2 pixels
                ));
                // CHANGE: Using a theme attribute for the color is better for light/dark mode support.
                TypedValue separatorColor = new TypedValue();
                getTheme().resolveAttribute(android.R.attr.listDivider, separatorColor, true);
                separator.setBackgroundColor(separatorColor.data);
                contentLayout.addView(separator);
            }
        }
    }

    private LinearLayout createRequestItemLayout(RegistrationRequest request) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(16,16,16,16);
        itemLayout.setClickable(true);

        itemLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, RegistrationStatusActivity.class);
            intent.putExtra("request", request);
            startActivity(intent);

        });

        //user information
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
        if ("Student".equals(request.getRole()) && request.getProgram() != null && !request.getProgram().isEmpty()) {
            TextView programText = new TextView(this);
            programText.setText("Program: " + request.getProgram());
            itemLayout.addView(programText);
        }



        if ("pending approval".equals(request.getStatus())) {
            itemLayout.addView(createActionButtons(request));
        }

        return itemLayout;
    }




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


    @Override
    protected void onResume() {
        super.onResume();
        //refresh when user comes back
        if (currentFilter != null) {
            showRequests(titleText.getText().toString(), currentFilter);
        }
    }
}
