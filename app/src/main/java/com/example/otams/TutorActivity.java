package com.example.otams;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class TutorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor);


        Spinner spinner = findViewById(R.id.roleSpinner);

        // Define options directly in Java
        String[] options1 = {"Tutor", "Student"};


        // Create the ArrayAdapter
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options1
        );

        // Set the dropdown layout style
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter1);

        Spinner spinner2 = findViewById(R.id.degree);

        // Define options directly in Java
        String[] options2 = {"select your highest degree","Bachelor’s Degree", "Master’s Degrees","PhD"};


        // Create the ArrayAdapter
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options2
        );

        // Set the dropdown layout style
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner2.setAdapter(adapter2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedOption = parent.getItemAtPosition(position).toString();

                if (selectedOption.equals("Student")) {
                    // Open the second activity
                    Intent intent = new Intent(TutorActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
            public void onNothingSelected(AdapterView<?> parent ){
                return;
            }
        });

        Button tutorRegisterBtn = findViewById(R.id.tutorRegisterBtn);

        tutorRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = "Tutor";
                Intent intent = new Intent(TutorActivity.this, WelcomeActivity.class);
                intent.putExtra("role", role);
                startActivity(intent);
            }
        });



    }
}
