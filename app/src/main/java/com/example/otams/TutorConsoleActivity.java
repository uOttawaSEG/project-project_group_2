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
import androidx.appcompat.app.AppCompatActivity;

public class TutorConsoleActivity  extends AppCompatActivity {

    //Check for error log
    private static final String tag = "TutorConsoleActivity";

    private Database db;
    private LinearLayout slotList;
    private Button createButton;
    private Button logOffBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "OnCreate, starting");

        try {
            setContentView(R.layout.activity_tutor_console);
            db = new Database(this);
            Log.d(tag, "Database starting");
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
                startActivity(intent);
            });
            // onClick for log off btn
            logOffBtn.setOnClickListener(v -> {
                Intent intent = new Intent(TutorConsoleActivity.this, MainActivity.class);

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
            Cursor cursor = db.getAllSlots();

            if (cursor == null || cursor.getCount() == 0) {
                TextView emptyMessage = new TextView(this);
                emptyMessage.setText("No slots available.");
                emptyMessage.setPadding(0, 48, 0, 0);
                slotList.addView(emptyMessage);
                if (cursor != null) {
                    cursor.close();
                }
                return;
            }

            while (cursor.moveToNext()) {
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));


                LayoutInflater inflater = LayoutInflater.from(this);
                View slotView = inflater.inflate(R.layout.single_time_slot, slotList, false);

                TextView dateText = slotView.findViewById(R.id.dateValue);
                TextView timeText = slotView.findViewById(R.id.timeValue);

                dateText.setText(date);
                timeText.setText(startTime + " - " + endTime);

                slotList.addView(slotView);
            }

            cursor.close();
        } catch (Exception e){
            Log.e(tag, "Crash in loadAndDisplaySlots", e);
        }
    }
}
