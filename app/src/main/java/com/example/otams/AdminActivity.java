package com.example.otams;

import android.content.Intent;
import android.os.Bundle;
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


        setupButton(R.id.approvedBtn, "Approved", "Approved");
        setupButton(R.id.rejectedBtn, "Rejected", "Rejected");
        setupButton(R.id.pendingBtn, "Pending", "pending approval");

        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish AdminActivity when returning
        });

        // CHANGE: Load the initial view just once on create. "pending approval" is the status stored in the DB.
        showRequests("Pending", "pending approval");
    }

    // CHANGE: Simplified this method to just set an OnClickListener
    private void setupButton(int buttonId, String title, String statusToFetch) {
        Button button = findViewById(buttonId);
        // The text on the button can be set directly in your XML layout for simplicity
        button.setOnClickListener(v -> showRequests(title, statusToFetch));
    }

    // CHANGE: Overhauled this entire method to be logical and prevent crashes.
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


            contentLayout.addView(emptyText);
            return;
        }


        for (RegistrationRequest request : requests) {
            LinearLayout itemLayout = createRequestItemLayout(request);
            contentLayout.addView(itemLayout);

            //separator for each user in list
            View separator = new View(this);
            separator.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
            ));
            separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            contentLayout.addView(separator);
        }
    }


    private LinearLayout createRequestItemLayout(RegistrationRequest request) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setClickable(true);

        itemLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, RegistrationStatusActivity.class);
            intent.putExtra("request", request);
            startActivity(intent);
        });


        TextView nameText = new TextView(this);
        nameText.setText(request.getFirstName() + " " + request.getLastName());

        itemLayout.addView(nameText);

        TextView emailText = new TextView(this);
        emailText.setText(request.getEmail());
        itemLayout.addView(emailText);


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
            db.rejectedRegistrationRequest(request.getUserId());
            showRequests(titleText.getText().toString(), currentFilter);
        });


        buttonLayout.addView(approveButton);
        buttonLayout.addView(rejectButton);

        return buttonLayout;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (currentFilter != null) {
            showRequests(titleText.getText().toString(), currentFilter);
        }
    }
}
