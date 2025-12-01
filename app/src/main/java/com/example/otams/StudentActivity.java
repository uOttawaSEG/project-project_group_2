package com.example.otams;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StudentActivity extends AppCompatActivity {
    private static final String TAG = "StudentActivity";
    private int studentId;
    private Database db;
    private LinearLayout futureSessions;
    private LinearLayout pastSessions;
    private TextView sectionTitle;
    private ImageView refreshbtn;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        
        studentId = getIntent().getIntExtra("studentId", -1);
        Log.d(TAG, "onCreate: studentId=" + studentId);

        db = new Database(this);
        futureSessions = findViewById(R.id.futureSessions);
        pastSessions = findViewById(R.id.pastSessions);
        sectionTitle = findViewById(R.id.sectionTitle);
        refreshbtn= findViewById(R.id.refresh);



        Button search = findViewById(R.id.searchbtn);
        search.setOnClickListener(v -> {
            Intent intent =new Intent(StudentActivity.this, StudentSearchActivity.class);
            intent.putExtra("studentId", studentId);
            startActivity(intent);
            finish();
        });
        refreshbtn.setOnClickListener(v -> {
            chargeSession();
        });



        Button futureBtn = findViewById(R.id.backbtns);
        Button pastBtn = findViewById(R.id.pastBtn);


        //log off btn for student console
        Button logOffBtn = findViewById(R.id.logOffBtn);
        logOffBtn.setOnClickListener(v -> {
            startActivity(new Intent(StudentActivity.this, LoginActivity.class));
            finish();

        });

        futureBtn.setOnClickListener(v -> {
            sectionTitle.setText("Upcoming Sessions");
            futureSessions.setVisibility(View.VISIBLE);
            pastSessions.setVisibility(View.GONE);

        });

        pastBtn.setOnClickListener(v -> {
            sectionTitle.setText("Past Sessions ");
            futureSessions.setVisibility(View.GONE);
            pastSessions.setVisibility(View.VISIBLE);


        });


        sectionTitle.setText("Upcoming Sessions");
        futureSessions.setVisibility(View.VISIBLE);
        pastSessions.setVisibility(View.GONE);






    }

    @Override
    protected void onResume() {
        super.onResume();
        db.markCompletedSessionsForStudent(studentId);
        chargeSession();
    }

    public  LinearLayout sessionLayout(String date, String startTime, String endTime, String tutorName, String status, int requestId, int periodId, boolean isPastSession, int tutorId, boolean rated) throws ParseException {
        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
        LinearLayout sessionInfo =  (LinearLayout) inflater.inflate(R.layout.item_student_session, null);

        TextView dateText = sessionInfo.findViewById(R.id.sessionDate);
        TextView timeText = sessionInfo.findViewById(R.id.sessionTime);
        TextView tutorText = sessionInfo.findViewById(R.id.tutorName);
        TextView statusText = sessionInfo.findViewById(R.id.statusBadge);
        LinearLayout actions = sessionInfo.findViewById(R.id.sessionActions);
        Button cancelSessionBtn = sessionInfo.findViewById(R.id.cancelSessionBtn);
        Button rateSessionBtn = sessionInfo.findViewById(R.id.rateSessionBtn);

        dateText.setText(date);
        timeText.setText(startTime + " - " + endTime);
        tutorText.setText(tutorName);
        String statusDisplay = status != null ? status : "approved";
        statusText.setText("Status: " + statusDisplay.toUpperCase());
        if ("approved".equalsIgnoreCase(statusDisplay)) {
            statusText.setBackgroundColor(0xFF4CAF50);
        } else if ("pending".equalsIgnoreCase(statusDisplay)) {
            statusText.setBackgroundColor(0xFFFFA726);
        } else if ("rejected".equalsIgnoreCase(statusDisplay)) {
            statusText.setBackgroundColor(0xFFF44336);
        } else if ("completed".equalsIgnoreCase(statusDisplay)) {
            statusText.setBackgroundColor(0xFF2196F3);
        }




            if( !isPastSession && "approved".equalsIgnoreCase(statusDisplay)){
                cancelSessionBtn.setVisibility(View.VISIBLE);
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm", Locale.getDefault());
                    Date sessionDateTime = sdf.parse(date + " " + startTime);


                    long sessionTime = sessionDateTime.getTime();
                    long presentTime = System.currentTimeMillis();
                    long twentyFourHour = 24 * 60 * 60 * 1000; //convert in hours

                    // Allow cancellation until 24 hours prior to session start
                    if (sessionTime - presentTime >= twentyFourHour) {
                        cancelSessionBtn.setEnabled(true);
                        cancelSessionBtn.setOnClickListener(v -> {
                            db.cancelRequest(requestId);
                            db.cancelTutoringSessionStudent(periodId);
                            Toast.makeText(StudentActivity.this, "Session cancelled", Toast.LENGTH_SHORT).show();
                            chargeSession();
                        });

                    } else {
                        cancelSessionBtn.setEnabled(false);
                        cancelSessionBtn.setText("Cancellation unavailable (<24h)");
                    }
                } catch(ParseException e){
                    e.printStackTrace();
                    cancelSessionBtn.setEnabled(false);
                    cancelSessionBtn.setVisibility(View.GONE);

                }
            }

        if(isPastSession && "completed".equalsIgnoreCase(statusDisplay) && !rated){
            rateSessionBtn.setVisibility(View.VISIBLE);
            rateSessionBtn.setOnClickListener(v -> {
                android.view.LayoutInflater li = android.view.LayoutInflater.from(StudentActivity.this);
                View dialogView = li.inflate(R.layout.dialog_rate_tutor,null);
                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(StudentActivity.this)
                        .setView(dialogView)
                        .create();
                android.widget.RatingBar rb = dialogView.findViewById(R.id.ratingBar);
                Button submit = dialogView.findViewById(R.id.submitRatingBtn);
                submit.setOnClickListener(btn -> {
                    int value = Math.round(rb.getRating());
                    if(value < 1){
                        Toast.makeText(StudentActivity.this,"Select a rating",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.calculateTutorNewRating(tutorId,value);
                    db.markRequestRated(requestId);
                    Toast.makeText(StudentActivity.this,"Rating submitted",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    chargeSession();
                });
                dialog.show();
            });
        }


        return sessionInfo;


    }

    //separate different session
    private View sessionSeparator(){
        View separator = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        separator.setLayoutParams(params);
        return separator;



    }







    private void chargeSession() {
        futureSessions.removeAllViews();
        pastSessions.removeAllViews();


        Cursor cursor = db.getAllStudentSessions(studentId);
        if (cursor == null) {
            return;
        }
        Log.d(TAG, "Retrieved student sessions from database");

        boolean hasFutureSessions = false;
        boolean hasPastSessions = false;
        try {

            while (cursor.moveToNext()) {
                //get all thesession info
                int requestId = cursor.getInt(cursor.getColumnIndexOrThrow("requestId"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow("startTime"));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow("endTime"));
                int periodId = cursor.getInt(cursor.getColumnIndexOrThrow("periodID"));
                String tutorName = cursor.getString(cursor.getColumnIndexOrThrow("tutorName"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                int tutorId = cursor.getInt(cursor.getColumnIndexOrThrow("tutorId"));
                boolean rated = cursor.getInt(cursor.getColumnIndexOrThrow("rated"))==1;
                Log.d(TAG, "PeriodId: "+periodId + ", Status: " + status);


                //date of session/now
                Calendar present = Calendar.getInstance();
                Calendar sessionDate = Calendar.getInstance();

                //check if the session is past
                boolean isPastSession;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm", Locale.getDefault());
                    Date sessionDateTime = sdf.parse(date + " " + startTime);
                    isPastSession = sessionDateTime.before(new Date());
                    Log.d(TAG, "isPastSession: " + isPastSession);
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }

                try {
                    LinearLayout itemSession = sessionLayout(date, startTime, endTime, tutorName, status, requestId, periodId, isPastSession, tutorId, rated);
                    Log.d(TAG, "Created session layout for period: " + periodId);
                    if (isPastSession) {
                        pastSessions.addView(itemSession);
                        hasPastSessions = true;
                        pastSessions.addView(sessionSeparator());
                    } else {
                        futureSessions.addView(itemSession);
                        hasFutureSessions = true;
                        futureSessions.addView(sessionSeparator());
                    }
                }
                catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }




            }
        }finally{
            cursor.close();
        }
        if(!hasFutureSessions){
            TextView emptyFuture = new TextView(this);
            emptyFuture.setText("No upcoming sessions");
            emptyFuture.setPadding(0, 24, 0, 0);
            futureSessions.addView(emptyFuture);
        }
        if(!hasPastSessions){
            TextView emptyPast = new TextView(this);
            emptyPast.setText("No past sessions");
            emptyPast.setPadding(0, 24, 0, 0);
            pastSessions.addView(emptyPast);
        }


    }


}


