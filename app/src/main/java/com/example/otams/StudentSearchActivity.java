package com.example.otams;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StudentSearchActivity extends AppCompatActivity{
    private static final String TAG = "StudentSearchActivity";
    private Database db;
    private ListView listView;
    private Button backbtn;
    private SearchView searchView;
    private Cursor results;
    private String queryS;
    private int studentId;
    private LinearLayout linearLayout;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_student);
        
        studentId = getIntent().getIntExtra("studentId", -1);
        db = new Database(this);
        linearLayout=findViewById(R.id.linearlayout);
        backbtn = findViewById(R.id.backbtns);
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                chargeSession();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
                                          });
        backbtn.setOnClickListener(v -> {
            //startActivity(new Intent(StudentSearchActivity.this, StudentActivity.class));
            finish();
        });
        chargeSession();
    }
    protected void onResume() {
        super.onResume();
        chargeSession();
    }

    private void search(String query) {
        Cursor cursor = db.SearchperiodBycoursename(query, studentId);
        if (cursor == null) {
            return;
        }
        results = cursor;

    }
    private void chargeSession() {
        LinearLayout container = findViewById(R.id.linearlayout);
        LayoutInflater inflater = LayoutInflater.from(this);

        Cursor cursor = results;
        if (cursor == null) {
            return;
        }
        container.removeAllViews();

        while (cursor.moveToNext()) {


            View itemView = inflater.inflate(R.layout.search_item_period, container, false);

            TextView startText = itemView.findViewById(R.id.startTimeText);
            TextView endText = itemView.findViewById(R.id.endTimeText);
            TextView dateText = itemView.findViewById(R.id.date);
            TextView nameText = itemView.findViewById(R.id.tutorNameText);
            TextView ratingText = itemView.findViewById(R.id.tutorRatingText);
            RatingBar ratingBar = itemView.findViewById(R.id.tutorRatingBar);
            Button bookBtn = itemView.findViewById(R.id.bookButton);

            // Fill the data
            startText.setText("Time:" + cursor.getString(cursor.getColumnIndexOrThrow("startTime")));
            endText.setText(cursor.getString(cursor.getColumnIndexOrThrow("endTime")));
            dateText.setText("date: " + cursor.getString(cursor.getColumnIndexOrThrow("date")));
            nameText.setText("TutorName: " + cursor.getString(cursor.getColumnIndexOrThrow("tutorName")));

            float rating = cursor.getFloat(cursor.getColumnIndexOrThrow("tutorRating"));
            ratingBar.setRating(rating);
            ratingText.setText("Rating: " + rating);


            int periodId = cursor.getInt(cursor.getColumnIndexOrThrow("periodID"));
            int slotId = cursor.getInt(cursor.getColumnIndexOrThrow("slotID"));
            int autoApprove = cursor.getInt(cursor.getColumnIndexOrThrow("autoApprove"));
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm", Locale.getDefault());

            String currentDateTime = sdf.format(new Date());


            bookBtn.setOnClickListener(v -> {
                Log.d(TAG, "Booking request - autoApprove: " + autoApprove + ", studentId: " + studentId);

                // Validation: Check if student already has a request for this period
                if (db.studentHasExistingRequestForPeriod(studentId, periodId)) {
                    Toast.makeText(this, "You already have a pending or approved request for this session!", Toast.LENGTH_LONG).show();
                    return;
                }

                // Validation: Check for overlapping sessions
                if (db.studentHasOverlappingSession(studentId, periodId)) {
                    Toast.makeText(this, "You have another session at the same time!", Toast.LENGTH_LONG).show();
                    return;
                }

                long result = db.addSessionRequest(periodId, slotId, studentId, currentDateTime, autoApprove == 1);
                if (result != -1) {
                    Toast.makeText(this, autoApprove == 1 ? "Session booked!" : "Session request submitted!", Toast.LENGTH_SHORT).show();
                    // Refresh search results to immediately hide this period for this student
                    String currentQuery = searchView.getQuery() != null ? searchView.getQuery().toString() : "";
                    if (!currentQuery.isEmpty()) {
                        Cursor refreshed = db.SearchperiodBycoursename(currentQuery, studentId);
                        if (results != null) results.close();
                        results = refreshed;
                        chargeSession();
                    }
                } else {
                    Toast.makeText(this, "Failed to request session. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });

            // Add the item to the container
            container.addView(itemView);
        }
        cursor.close();


    }


}
