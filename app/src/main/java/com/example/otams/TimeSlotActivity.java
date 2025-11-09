package com.example.otams;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.widget.Button;
import android.widget.EditText;
import java.util.regex.Pattern;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class TimeSlotActivity extends AppCompatActivity{
    private EditText dateText, startTimeText, endTimeText;
    private TextInputLayout dateInputLayout, startTimeInputLayout, endTimeInputLayout;
    private Button addButton;
    private Database db;

    private static final Pattern TIME_24H = Pattern.compile("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeslot);
        db = new Database(this);

        dateText = findViewById(R.id.dateText);
        startTimeText = findViewById(R.id.startTime);
        endTimeText = findViewById(R.id.endTime);
        addButton = findViewById(R.id.addButton);

        dateInputLayout = findViewById(R.id.dateInputLayout);
        startTimeInputLayout = findViewById(R.id.startTimeInputLayout);
        endTimeInputLayout = findViewById(R.id.endTimeInputLayout);

        dateText.setOnClickListener(v -> showDatePickerDialog());
        addButton.setOnClickListener(v -> checkAndSaveSlot());
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
        dateInputLayout.setError(null);
        startTimeInputLayout.setError(null);
        endTimeInputLayout.setError(null);

        boolean isValid = true;

        String date = dateText.getText().toString();
        String startTime = startTimeText.getText().toString();
        String endTime = endTimeText.getText().toString();

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

        if (isValid){
            long result = db.addSlot(date, startTime, endTime);
            if (result != -1){
                Intent intent = new Intent(TimeSlotActivity.this, TutorConsoleActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }
}
