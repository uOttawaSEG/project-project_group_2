package com.example.otams;
import android.database.Cursor;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.widget.Button;
import android.widget.EditText;
import java.util.regex.Pattern;
import android.content.Intent;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class TimeSlotActivity extends AppCompatActivity{
    private EditText dateText, startTimeText, endTimeText;
    private TextInputLayout dateInputLayout, startTimeInputLayout, endTimeInputLayout;
    private Button addButton, backButton;
    private Database db;
    private int tutorId;
    private String email;

    private static final Pattern TIME_24H = Pattern.compile("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeslot);
        db = new Database(this);
        
        // Retrieve tutorId and email from intent
        tutorId = getIntent().getIntExtra("tutorId", -1);
        email = getIntent().getStringExtra("email");

        dateText = findViewById(R.id.dateText);
        startTimeText = findViewById(R.id.startTime);
        endTimeText = findViewById(R.id.endTime);
        addButton = findViewById(R.id.addButton);
        backButton = findViewById(R.id.backButton);


        dateInputLayout = findViewById(R.id.dateInputLayout);
        startTimeInputLayout = findViewById(R.id.startTimeInputLayout);
        endTimeInputLayout = findViewById(R.id.endTimeInputLayout);

        dateText.setOnClickListener(v -> showDatePickerDialog());
        addButton.setOnClickListener(v -> checkAndSaveSlot());
        backButton.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog(){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view,selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
                    dateText.setText(sdf.format(calendar.getTime()));
                    dateInputLayout.setError(null);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void checkAndSaveSlot(){
        Switch autoAcceptSwitch = findViewById(R.id.autoAcceptSwitch);
        dateInputLayout.setError(null);
        startTimeInputLayout.setError(null);
        endTimeInputLayout.setError(null);

        boolean isValid = true;

        String date = dateText.getText().toString();
        String startTime = startTimeText.getText().toString();
        String endTime = endTimeText.getText().toString();
        int autoApproveValue = autoAcceptSwitch.isChecked() ? 1 : 0;

        //Empty check
        if (date.isEmpty()){
            dateInputLayout.setError("Please select a date.");
            isValid = false;
        }
        if(startTime.isEmpty()){
            startTimeInputLayout.setError("Please enter a start time.");
            isValid = false;
        }
        if(endTime.isEmpty()){
            endTimeInputLayout.setError("Please enter an end time.");
            isValid = false;
        }
        if (!isValid){
            return;
        }
        //Check if the time are in correct format first
        if (!TIME_24H.matcher(startTime).matches()){
            startTimeInputLayout.setError("Invalid time format. Use hh:mm");
            isValid = false;
        }
        if (!TIME_24H.matcher(endTime).matches()){
            endTimeInputLayout.setError("Invalid time format. Use hh:mm");
            isValid = false;
        }
        if (!isValid){
            return;
        }

        //Now check if the minute are in 30 or 00;
        String startMin = startTime.split(":")[1];
        if (!startMin.equals("00") && !startMin.equals("30")){
            startTimeInputLayout.setError("Minute must be 00 or 30");
            isValid = false;
        }
        String endMin = endTime.split(":")[1];
        if (!endMin.equals("00") && !endMin.equals("30")){
            endTimeInputLayout.setError("Minute must be 00 or 30");
            isValid = false;
        }

        //Check if start time is earlier than the end time
        if (startTime.compareTo(endTime) > 0){
            endTimeInputLayout.setError("End time must be after start time");
            isValid = false;
        }

        //checks if there is overlap in timeslots
        Cursor cursor = db.getSlotsForTutor(tutorId);
        while (cursor != null && cursor.getCount() != 0 && cursor.moveToNext() && isValid != false) {
            String comparedDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            if (date.equals(comparedDate)) {
                String comparedStart = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
                String comparedEnd = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));

                double comparableStart = Integer.parseInt(comparedStart.substring(0, 2)) + (Integer.parseInt(comparedStart.substring(3, 5)))*(0.01666);
                double comparableEnd = Integer.parseInt(comparedEnd.substring(0, 2)) + (Integer.parseInt(comparedEnd.substring(3, 5)))*(0.01666);

                double newStart = Integer.parseInt(startTime.substring(0, 2)) + (Integer.parseInt(startTime.substring(3, 5)))*(0.01666);
                double newEnd = Integer.parseInt(endTime.substring(0, 2)) + (Integer.parseInt(endTime.substring(3, 5)))*(0.01666);

                //if new time starts during existing time slot
                if (newStart >= comparableStart && newStart < comparableEnd) {
                    isValid = false;
                    startTimeInputLayout.setError("New session starts during an existing session");
                }
                //if new time ends during existing slot
                else if (newEnd > comparableStart && newEnd <= comparableEnd) {
                    isValid = false;
                    endTimeInputLayout.setError("New session ends during an existing session");
                }
                //if new time surrounds existing slot
                else if (newStart <= comparableStart && newEnd >= comparableEnd) {
                    isValid = false;
                    startTimeInputLayout.setError("New session surrounds existing session");
                }


            }

        }

        if (isValid){
            long result = db.addSlot(tutorId, date, startTime, endTime,autoApproveValue);
            if (result != -1){
                Intent intent = new Intent(TimeSlotActivity.this, TutorConsoleActivity.class);
                intent.putExtra("tutorId", tutorId);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            } else {
                // Duplicate slot or constraint violation
                dateInputLayout.setError("This time slot already exists or conflicts with another slot.");
            }

        }
    }
}
