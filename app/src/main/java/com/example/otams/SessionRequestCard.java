package com.example.otams;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SessionRequestCard extends AppCompatActivity {

    private LinearLayout sessionRequestList;
    private Database db;
    private int tutorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutrequests);
        tutorId = getIntent().getIntExtra("tutorId", -1);
        Log.d("SessionRequestCard", "onCreate called with tutorId=" + tutorId);

        if (tutorId == -1) {
            throw new RuntimeException("Invalid tutorId passed!");
        }
        sessionRequestList = findViewById(R.id.sessionRequestList);
        Button backButton = findViewById(R.id.back);


        // Initialize database
        db = new Database(this);

        // Get tutor ID from intent
        tutorId = getIntent().getIntExtra("tutorId", -1);

        // Load session requests
        loadPendingRequests(tutorId);

        // Back button listener
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SessionRequestCard.this, TutorConsoleActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadPendingRequests(int tutorId) {
        sessionRequestList.removeAllViews();

        Cursor cursor = db.getPendingRequestsForTutor(tutorId);

        if (cursor == null || cursor.getCount() == 0) {
            TextView emptyMessage = new TextView(this);
            emptyMessage.setText("No pending session requests.");
            emptyMessage.setPadding(0, 48, 0, 0);
            sessionRequestList.addView(emptyMessage);
            if (cursor != null) cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            View card = LayoutInflater.from(this)
                    .inflate(R.layout.single_session_request, sessionRequestList, false);

            TextView studentName = card.findViewById(R.id.studentName);
            TextView sessionDateTime = card.findViewById(R.id.sessionDateTime);
            TextView studentEmail = card.findViewById(R.id.studentEmail);
            Button approve = card.findViewById(R.id.approveButton);
            Button reject = card.findViewById(R.id.rejectButton);
            Button cancel = card.findViewById(R.id.cancelButton);

            int studentId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String start = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
            String end = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));
            int requestId = cursor.getInt(cursor.getColumnIndexOrThrow("requestId"));

            studentName.setText("Student ID: " + db.getStudentNameById(studentId));
            studentEmail.setText("Email: " + db.getUserEmailById(studentId));

            sessionDateTime.setText(date + " " + start + " - " + end);

            approve.setOnClickListener(v -> {
                db.approveRequest(requestId);
                sessionRequestList.removeView(card);
            });
            reject.setOnClickListener(v -> {
                db.rejectRequest(requestId);
                sessionRequestList.removeView(card);
            });
            cancel.setOnClickListener(v -> {
                db.cancelRequest(requestId);
                sessionRequestList.removeView(card);
            });

            sessionRequestList.addView(card);
        }

        cursor.close();
    }
}
