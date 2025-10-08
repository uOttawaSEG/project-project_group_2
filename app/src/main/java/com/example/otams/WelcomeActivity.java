package com.example.otams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {


    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        welcomeText = findViewById(R.id.welcomeText);
        Intent intent = getIntent();

        String role = intent.getStringExtra("role");
        String messageError;
        if(role == null){
          messageError = "Error, you need to login with a role ";
        }

        String message = "Welcome! you are logged as '" + role + "'";
        welcomeText.setText(message);

        Button logOffBtn = findViewById(R.id.logOffBtn);


        logOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                Intent intent1 = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });
    }


}
