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
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StudentActivity extends AppCompatActivity {
    private int studentId;
    private Database db;
    private LinearLayout futureSessions;
    private LinearLayout pastSessions;
    private TextView sectionTitle;
    private ImageView refreshbtn;
    Log log;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        studentId = getIntent().getIntExtra("studentId", -1);

        log.d("StudentActivity", "onCreate: studentId=" + studentId);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        db = new Database(this);
        studentId = getIntent().getIntExtra("studentId", -1);
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
        chargeSession();
    }

    public  LinearLayout sessionLayout(String date, String startTime, String endTime, String tutorName, int periodId, boolean isPastSession) throws ParseException {

        LinearLayout sessionInfo =  new LinearLayout(this);
        sessionInfo.setPadding(8, 8, 8, 8);
        sessionInfo.setOrientation(LinearLayout.VERTICAL);

        //date of session
        TextView dateText = new TextView(this);
        dateText.setText(date);
        sessionInfo.addView(dateText);

        //time of  session
        TextView timeText = new TextView(this);
        timeText.setText(startTime + "  -  " + endTime);
        sessionInfo.addView(timeText);

        //tutor name
        TextView tutorText = new TextView(this);
        tutorText.setText(tutorName);
        sessionInfo.addView(tutorText);




            if( !isPastSession){

                Button cancelSessionBtn = new Button(this);
                cancelSessionBtn.setText("Cancel ");
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm", Locale.getDefault());
                    Date sessionDateTime = sdf.parse(date + " " + startTime);


                    long sessionTime = sessionDateTime.getTime();
                    long presentTime = System.currentTimeMillis();
                    long twentyFourHour = 24 * 60 * 60 * 1000; //convert in hours

                    //if (cancel< 24h) ==  cancel  ; else{ cancellation accepted
                    if (sessionTime - presentTime > twentyFourHour) {
                        cancelSessionBtn.setEnabled(true);
                        cancelSessionBtn.setOnClickListener(v -> {
                            db.cancelTutoringSessionStudent(periodId);
                            Toast.makeText(StudentActivity.this, "Session cancelled", Toast.LENGTH_SHORT).show();
                            chargeSession();
                        });

                    } else {
                        cancelSessionBtn.setEnabled(false);
                        cancelSessionBtn.setText("Cancellation unavailable, session is in less than 24 hours");
                    }
                } catch(ParseException e){
                    e.printStackTrace();
                    cancelSessionBtn.setEnabled(false);
                    cancelSessionBtn.setText("Error");
                    cancelSessionBtn.setVisibility(View.GONE);

                }

                sessionInfo.addView(cancelSessionBtn);
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


        Cursor cursor = db.getBookedTutoringSessionsStudent(studentId);
        if (cursor == null) {
            return;
        }
        log.d("StudentActivity","we got to the db.getBookedTutoringSessionsStudent ");

        boolean hasFutureSessions = false;
        boolean hasPastSessions = false;
        try {

            while (cursor.moveToNext()) {
                //get all thesession info
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow("startTime"));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow("endTime"));
                int periodId = cursor.getInt(cursor.getColumnIndexOrThrow("periodID"));
                String tutorName = cursor.getString(cursor.getColumnIndexOrThrow("tutorName"));
                log.d("StudentActivity", "PeriodId: "+periodId);


                //date of session/now
                Calendar present = Calendar.getInstance();
                Calendar sessionDate = Calendar.getInstance();

                //check if the session is past
                boolean isPastSession;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm", Locale.getDefault());
                    Date sessionDateTime = sdf.parse(date + " " + startTime);
                    isPastSession = sessionDateTime.before(new Date());
                    log.d("StudentActivity", "isPastSession: " + isPastSession);
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }

                try {
                    LinearLayout itemSession = sessionLayout(date, startTime, endTime, tutorName, periodId, isPastSession);
                    log.d("StudentActivity", "itemSession: " + itemSession);
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




    }


}


