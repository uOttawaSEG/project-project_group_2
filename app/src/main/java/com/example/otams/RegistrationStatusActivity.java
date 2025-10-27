package com.example.otams;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationStatusActivity extends AppCompatActivity {
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_status);

        db = new Database(this);

        // Get the request from intent
        RegistrationRequest request = (RegistrationRequest) getIntent().getSerializableExtra("request");

        if (request != null) {
            displayRequestDetails(request);
        }

        // Back button
        Button backButton = findViewById(R.id.returnButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void displayRequestDetails(RegistrationRequest request) {

        ((TextView) findViewById(R.id.nameText)).setText(request.getFirstName() + " " + request.getLastName());
        ((TextView) findViewById(R.id.emailText)).setText(request.getEmail());
        ((TextView) findViewById(R.id.phoneText)).setText(request.getPhoneNum());
        ((TextView) findViewById(R.id.roleText)).setText(request.getRole());

        ((TextView) findViewById(R.id.statusText)).setText(request.getStatus());


        // Show specific role wanted
        if ("Student".equals(request.getRole())) {
            findViewById(R.id.programLayout).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.programText)).setText(request.getProgram());
        } else {
            findViewById(R.id.programLayout).setVisibility(View.GONE);
        }



        if ("Tutor".equals(request.getRole())){
            findViewById(R.id.degreeLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.coursesLayout).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.degreeText)).setText(request.getDegree());
            ((TextView) findViewById(R.id.coursesText)).setText(request.getCourse());
        }
        else {
            findViewById(R.id.degreeLayout).setVisibility(View.GONE);
            findViewById(R.id.coursesLayout).setVisibility(View.GONE);

        }

        // Setup action buttons based on current status
        setupActionButtons(request);
    }





    private void setupActionButtons(RegistrationRequest request) {

        LinearLayout buttonLayout = findViewById(R.id.buttonLayout);
        buttonLayout.removeAllViews();

        switch (request.getStatus()) {
            case "Under Review":
                // Can approve or reject pending requests
                addActionButton("Approve", android.R.color.holo_green_light, v -> {
                    db.approveRegistrationRequest(request.getUserId());
                    finish(); // Go back to admin screen
                });
                addActionButton("Reject", android.R.color.holo_red_light, v -> {
                    db.rejectedRegistrationRequest(request.getUserId());
                    finish();
                });
                break;
            case "Approved":
                // Can set back to pending
                addActionButton("Set to Pending", android.R.color.holo_orange_light, v -> {
                    db.updateRegistrationRequest(request.getUserId());
                    finish();
                });
                break;
            case "Rejected":
                // Can approve or set to pending
                addActionButton("Approve", android.R.color.holo_green_light, v -> {
                    db.approveRegistrationRequest(request.getUserId());
                    finish();
                });
                addActionButton("Set to Pending", android.R.color.holo_orange_light, v -> {
                    db.updateRegistrationRequest(request.getUserId());
                    finish();
                });
                break;
        }
    }


    private void addActionButton(String text, int colorRes, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(text);
        button.setBackgroundColor(getResources().getColor(colorRes));
        button.setOnClickListener(listener);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        params.setMargins(8, 0, 8, 0);
        button.setLayoutParams(params);

        ((LinearLayout) findViewById(R.id.buttonLayout)).addView(button);
    }
}