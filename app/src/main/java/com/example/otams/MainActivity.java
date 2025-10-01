package com.example.otams;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reference the Spinner
        Spinner spinner = findViewById(R.id.mySpinner);

        // Define options directly in Java
        String[] options1 = {"Student", "Tutor"};


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


        Spinner spinner2 = findViewById(R.id.program);

        String[] programs = {
                "Select your program",
                "Chemical and Biological Engineering",
                "Civil Engineering",
                "Mechanical Engineering",
                "Software Engineering",
                "Electrical Engineering",
                "Computer Engineering",
                "Biomedical Mechanical Engineering",
                "Multidisciplinary Design – Experiential Learning",
                "Biotechnology",
                "Data Science",
                "Engineering Management and Entrepreneurship Options",
                "Honours BSc in Mathematics",
                "Honours BSc in Mathematics and Computer Science (Data Science)",
                "Honours BSc in Mathematics and Economics",
                "Honours BSc in Financial Mathematics and Economics",
                "Major in Mathematics",
                "Minor in Mathematics",
                "Honours BSc in Biology",
                "Honours BSc in Biochemistry",
                "Honours BSc in Biomedical Science",
                "Honours BSc in Chemistry",
                "Honours BSc in Computer Science",
                "Honours BSc in Environmental Science",
                "Honours BSc in Geology",
                "Honours BSc in Human Kinetics",
                "Honours BSc in Mathematics",
                "Honours BSc in Physics",
                "Honours BSc in Psychology",
                "Honours BSc in Statistics"
        };



        // Create the ArrayAdapter
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                programs
        );
        // Set the dropdown layout style
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner2.setAdapter(adapter2);
    }


}
