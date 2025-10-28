package com.example.otams;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity is the main entry point for user registration.
 * It allows users to register as either a Student or a Tutor.
 */
public class MainActivity extends AppCompatActivity {
    private TextView loginTextView;

    /**
     * Initializes the activity, sets up the user interface, and handles user interactions.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link to the login screen for users who already have an account
        loginTextView = findViewById(R.id.textView);
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Spinner for selecting user role (Student or Tutor)
        Spinner spinner = findViewById(R.id.roleSpinner);
        String[] options1 = {"Student", "Tutor"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options1
        );
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);

        // Spinner for selecting the student's program
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
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                programs
        );
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        // Listener for the role spinner to switch to the tutor registration if "Tutor" is selected
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                if (selectedOption.equals("Tutor")) {
                    Intent intent = new Intent(MainActivity.this, TutorActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Button to trigger the registration process
        Button registerButton = findViewById(R.id.Registerbtn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    /**
     * Handles the registration process for a new student.
     * It validates user input and creates a new Student object in the database.
     */
    private void register() {
        try {
            // Get user input from the form
            EditText FirstNameView = findViewById(R.id.editTextFirstName);
            EditText LastNameView = findViewById(R.id.editTextLastName);
            EditText EmailView = findViewById(R.id.editTextTextEmailAddress);
            EditText PasswordView = findViewById(R.id.editTextTextPassword);
            EditText PhoneView = findViewById(R.id.editTextPhone);
            Spinner ProgramView = findViewById(R.id.program);
            TextView ProgramText = findViewById((R.id.programTxt));
            Spinner RoleView = findViewById(R.id.roleSpinner);

            String FirstName = FirstNameView.getText().toString();
            String LastName = LastNameView.getText().toString();
            String Email = EmailView.getText().toString();
            String Password = PasswordView.getText().toString();
            String Phone = PhoneView.getText().toString();
            String role = RoleView.getSelectedItem().toString();
            String program = ProgramView.getSelectedItem().toString();

            // Clear previous errors
            FirstNameView.setError(null);
            LastNameView.setError(null);
            EmailView.setError(null);
            PasswordView.setError(null);
            PhoneView.setError(null);

            // Validate user input
            if (FirstName.isEmpty()) {
                FirstNameView.setError("First name can't be empty");
                FirstNameView.requestFocus();
                return;
            }
            if (LastName.isEmpty()) {
                LastNameView.setError("Last name can't be empty");
                LastNameView.requestFocus();
                return;
            }
            if (Email.isEmpty()) {
                EmailView.setError("Email can't be empty");
                EmailView.requestFocus();
                return;
            }
            if (Email.indexOf('@') == -1 || Email.substring(Email.indexOf('@')).indexOf('.') == -1 || Email.substring(Email.indexOf('@')).indexOf('.') == Email.substring(Email.indexOf('@')).length() - 1) {
                EmailView.setError("Email must be in format ____@___.___");
                EmailView.requestFocus();
                return;
            }
            if (Password.isEmpty()) {
                PasswordView.setError("Password can't be empty");
                PasswordView.requestFocus();
                return;
            }
            if (Phone.isEmpty()) {
                PhoneView.setError("Phone number can't be empty");
                PhoneView.requestFocus();
                return;
            } else if (Phone.length() != 10) {
                PhoneView.setError("Invalid phone number");
                PhoneView.requestFocus();
                return;
            }
            if (role.isEmpty()) {
                Log.w("Registration error", "No role was chosen");
                return;
            }

            if (role.equals("Student") && program.equals("Select your program")) {
                ProgramText.setError("Need to select a program");
                ProgramText.requestFocus();
                return;
            }

            // Create a new Student object and add it to the database
            User user = new Student(role, FirstName, LastName, Email, Password, Phone, program);
            Database db = new Database(this);
            db.addUser(user);

            // Redirect to the login activity after successful registration
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("role", role);
            intent.putExtra("program", program);
            startActivity(intent);
            finish();
        } catch (IllegalArgumentException e) {
            Log.w("Registration Error", "Missing required fields.");
        }
    }
}
