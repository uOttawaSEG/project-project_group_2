package com.example.otams;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

public class SessionRequestsActivity extends AppCompatActivity {

    private int tutorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tutorId = getIntent().getIntExtra("tutorId", -1);

        Intent intent = new Intent(SessionRequestsActivity.this, SessionRequestCard.class);
        intent.putExtra("tutorId", tutorId);
        startActivity(intent);

        finish();
    }
}
