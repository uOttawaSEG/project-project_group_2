package com.example.otams;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TutorConsoleActivity  extends AppCompatActivity {
    private static final String tag = "TutorConsoleActivity";

    private Database db;
    private LinearLayout slotList;
    private Button createButton;
    private Button logOffBtn;
    private int tutorId;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "OnCreate, starting");

        try {
            setContentView(R.layout.activity_tutor_console);
            db = new Database(this);
            Log.d(tag, "Database starting");
            
            // Retrieve tutorId and email from intent
            tutorId = getIntent().getIntExtra("tutorId", -1);
            email = getIntent().getStringExtra("email");
            
            if (tutorId == -1) {
                Log.e(tag, "No tutorId provided");
                finish();
                return;
            }
            
            slotList = findViewById(R.id.slotList);
            createButton = findViewById(R.id.createButton);

            if (slotList == null) {
                Log.e(tag, "slotList is null");
                finish();
                return;
            }
            if (createButton == null) {
                Log.e(tag, "createButton is null");
                finish();
                return;
            }
            logOffBtn = findViewById(R.id.logOffBtn);



            //onClick for create time slot
            createButton.setOnClickListener(v -> {
                Intent intent = new Intent(TutorConsoleActivity.this, TimeSlotActivity.class);
                intent.putExtra("tutorId", tutorId);
                intent.putExtra("email", email);
                startActivity(intent);
            });
            // onClick for log off btn
            logOffBtn.setOnClickListener(v -> {
                Intent intent = new Intent(TutorConsoleActivity.this, MainActivity.class);

                startActivity(intent);

            });
            Button requestsButton = findViewById(R.id.requestsButton);
            requestsButton.setOnClickListener(v -> {
                int tutorId = getIntent().getIntExtra("tutorId", -1);
                Log.d("TutorConsole", "Starting SessionRequestCard with tutorId=" + tutorId);
                Intent intent = new Intent(TutorConsoleActivity.this, SessionRequestCard.class);
                intent.putExtra("tutorId", tutorId);
                startActivity(intent);
            });






            Log.d(tag,"onCreate worked");
        } catch(Exception e){
            Log.e(tag,"Crash in OnCreate",e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplaySlots();
    }

    private void loadAndDisplaySlots() {
        Log.d(tag, "load and display starting");

        try {
            slotList.removeAllViews();
            Cursor cursor = db.getSlotsForTutor(tutorId);

            if (cursor == null || cursor.getCount() == 0) {
                TextView emptyMessage = new TextView(this);
                emptyMessage.setText("No slots available.");
                emptyMessage.setPadding(0, 48, 0, 0);
                slotList.addView(emptyMessage);
                if (cursor != null) cursor.close();
                return;
            }

            while (cursor.moveToNext()) {
                int slotId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));

                // Inflate the slot view
                LayoutInflater inflater = LayoutInflater.from(this);
                View slotView = inflater.inflate(R.layout.single_time_slot, slotList, false);

                // Set the text fields
                TextView dateText = slotView.findViewById(R.id.dateValue);
                TextView timeText = slotView.findViewById(R.id.timeValue);
                Button deleteButton = slotView.findViewById(R.id.deleteButton);

                dateText.setText(date);
                timeText.setText(startTime + " - " + endTime);

                if(db.slotHasBookings(slotId)){
                    deleteButton.setEnabled(false);
                    deleteButton.setText("Booked");
                    deleteButton.setOnClickListener(v -> Toast.makeText(this,"Cannot delete: has bookings",Toast.LENGTH_SHORT).show());
                } else {
                    deleteButton.setOnClickListener(v -> {
                        boolean deleted = db.removeSlot(slotId);
                        if (deleted) {
                            Toast.makeText(this, "Slot deleted", Toast.LENGTH_SHORT).show();
                            loadAndDisplaySlots(); // refresh the list after deleting
                        } else {
                            Toast.makeText(this, "Failed to delete slot", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                slotList.addView(slotView);
            }

            cursor.close();
        } catch (Exception e) {
            Log.e(tag, "Crash in loadAndDisplaySlots", e);
        }
    }

}
