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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        db = new Database(this);
        contentLayout = findViewById(R.id.contentLayout);
        titleText = findViewById(R.id.titleText);

        setupButton(R.id.pendingBtn, "pending", "approval is pending");
        setupButton(R.id.approvedBtn, "approved", "approved");
        setupButton(R.id.rejectedBtn, "Rejected ", "rejected");
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        showRequests("pending", "approval is pending");


    }

    private void setupButton(int buttonId, String title, String status){
        Button button = findViewById(buttonId);
        button.setText(title);
        button.setOnClickListener(v -> showRequests(title,  status));

    }



    private void showRequests(String title, String status) {

        titleText.setText(title);

        contentLayout.removeAllViews();
        List<RegistrationRequest> requests;

        if (title.equals("Pending ")) {
            requests = db.getPendingRegistrationRequests();
        } else if (title.equals("Approved ")) {
            requests = db.getApprovedRegistrationRequests();
        }
        else {
            requests = db.getRejectedRegistrationRequests();

        }

        //display every  request
        for (RegistrationRequest request : requests) {
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setPadding(16, 16, 16, 16);
            //request informations
            //name
            TextView nameText = new TextView(this);
            nameText.setText(request.getFirstName() + " " + request.getLastName());
            itemLayout.addView(nameText);
            //email
            TextView emailText = new TextView(this);
            emailText.setText(request.getEmail());
            itemLayout.addView(emailText);
            //PhoneNum
            TextView phoneText = new TextView(this);
            phoneText.setText(request.getPhoneNum());
            itemLayout.addView(phoneText);
            //Degree
            TextView degreeText = new TextView(this);
            degreeText.setText(request.getDegree());
            itemLayout.addView(degreeText);
            //Program
            TextView programText = new TextView(this);
            programText.setText(request.getProgram());
            itemLayout.addView(programText);




             //approve/reject button for pending request
            if(title.equals("pending ")) {
                LinearLayout buttonLayout = new LinearLayout(this);
                buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                Button approvedBtn = new Button(this);
                approvedBtn.setText("Approved");
                approvedBtn.setOnClickListener(v -> {
                        db.approveRegistrationRequest(request.getUserId());
                        showRequests(title, "Approved");});
                //reject
                Button rejectedBtn = new Button(this);
                rejectedBtn.setText("Rejected");
                rejectedBtn.setOnClickListener(v-> {
                        db.rejectedRegistrationRequest(request.getUserId());
                        showRequests(title, "Rejected");});

                buttonLayout.addView(approvedBtn);
                buttonLayout.addView(rejectedBtn);
                itemLayout.addView(buttonLayout);


            }


            itemLayout.setOnClickListener(v->{
                Intent intent = new Intent(AdminActivity.this, RegistrationStatusActivity.class);
                intent.putExtra("userId", request.getUserId());
                startActivity(intent);
            });

            //add the layout on the page
            contentLayout.addView(itemLayout);
            View separator = new View(this);
            separator.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 2
            ));
            separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            contentLayout.addView(separator);





        }









    }
}