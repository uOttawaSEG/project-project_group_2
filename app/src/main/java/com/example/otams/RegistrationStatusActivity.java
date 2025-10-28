package com.example.otams;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;



//doest work, the app keeps on crashing when tryin to get this page
public class RegistrationStatusActivity extends AppCompatActivity {
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_status);
        db = new Database(this);
        RegistrationRequest request = (RegistrationRequest) getIntent().getSerializableExtra("request");
        if (request != null) {
            displayRequestDetails(request);
        } else{
            Log.e("RegistrationStatusActivity", "Request data is null");
            finish();

        }
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> finish());
    }

    private void displayRequestDetails(RegistrationRequest request) {

        TextView nameText = findViewById(R.id.nameText);
        TextView emailText =  findViewById(R.id.emailText);
        TextView phoneText =  findViewById(R.id.phoneText) ;
        TextView roleText   =  findViewById(R.id.roleText);
        TextView statusText = findViewById(R.id.statusText);


        View programLayout = findViewById(R.id.programLayout);
        TextView programTxt = findViewById(R.id.programTxt);
        View degreeLayout = findViewById(R.id.degreeLayout);
        TextView degreeTxt = findViewById(R.id.degreeTxt);
        View coursesLayout = findViewById(R.id.coursesLayout);
        TextView courseTxt = findViewById(R.id.courseTxt);

        nameText.setText(request.getFirstName() + " " + request.getLastName());

        emailText.setText(request.getEmail());

        phoneText.setText(request.getPhoneNum());

        roleText.setText(request.getRole());

        statusText.setText(request.getStatus());



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
        } else {programLayout.setVisibility(View.GONE);
            degreeLayout.setVisibility(View.GONE);
            coursesLayout.setVisibility(View.GONE);
        }


        setupActionButtons(request);
    }





    private void setupActionButtons(RegistrationRequest request) {
         LinearLayout buttonLayout = findViewById(R.id.buttonLayout);
        buttonLayout.removeAllViews();

        switch (request.getStatus()) {

            case "Under Review":
                //approve/reject a user
                addActionButton("Approve", android.R.color.holo_green_light, v -> {
                    db.approveRegistrationRequest(request.getUserId());
                    finish(); // Go back to admin screen
                });
                addActionButton("Reject", android.R.color.holo_red_light, v -> {

                    db.rejecteRegistrationRequest(request.getUserId());
                    finish();
                });
                break;
            //if user approuved
            case "Approved" :

                addActionButton("Set to Pending", android.R.color.holo_orange_light, v -> {

                    db.setRegistrationToPending(request.getUserId());
                    finish();
                });
                break;
            //if user rejected
            case "Rejected":
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



    private void addActionButton(String text, int colorRes, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(text);
        button.setBackgroundColor(getResources().getColor(colorRes,null));
        button.setOnClickListener(listener);


        ((LinearLayout) findViewById(R.id.buttonLayout)).addView(button);
    }
}