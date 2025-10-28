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
 * TutorActivity allows tutors to register for an account.
 * It provides fields for personal information, degree, and courses they can teach.
 */
public class TutorActivity extends AppCompatActivity {
    private TextView loginbtn;

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
        setContentView(R.layout.activity_tutor);

        // Link to the login screen for users who already have an account
        loginbtn = findViewById(R.id.textView);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Spinner for selecting user role (Tutor or Student)
        Spinner spinner = findViewById(R.id.roleSpinner);
        String[] options1 = {"Tutor", "Student"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options1
        );
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);

        // Spinner for selecting the tutor's highest degree
        Spinner spinner2 = findViewById(R.id.degree);
        String[] options2 = {"select your highest degree", "Bachelor’s Degree", "Master’s Degrees", "PhD"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options2
        );
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        // Listener for the role spinner to switch to the student registration if "Student" is selected
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                if (selectedOption.equals("Student")) {
                    Intent intent = new Intent(TutorActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Button to trigger the registration process
        Button tutorRegisterBtn = findViewById(R.id.tutorRegisterBtn);
        tutorRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    /**
     * Handles the registration process for a new tutor.
     * It validates user input and creates a new Tutor object in the database.
     */
    private void register() {
        try {
            // Get user input from the form
            EditText FirstNameView = findViewById(R.id.editTextFirstName);
            EditText LastNameView = findViewById(R.id.editTextLastName);
            EditText EmailView = findViewById(R.id.editTextTextEmailAddress);
            EditText PasswordView = findViewById(R.id.editTextTextPassword);
            EditText PhoneView = findViewById(R.id.editTextPhone);
            Spinner Hdegree = findViewById(R.id.degree);
            EditText courses = findViewById(R.id.courses);
            Spinner RoleView = findViewById(R.id.roleSpinner);
            TextView DegreeView = findViewById(R.id.textView2);

            String FirstName = FirstNameView.getText().toString();
            String LastName = LastNameView.getText().toString();
            String Email = EmailView.getText().toString();
            String Password = PasswordView.getText().toString();
            String Phone = PhoneView.getText().toString();
            String role = RoleView.getSelectedItem().toString();
            String degree = Hdegree.getSelectedItem().toString();
            String course = courses.getText().toString().toUpperCase();

            // Clear previous errors
            FirstNameView.setError(null);
            LastNameView.setError(null);
            EmailView.setError(null);
            PasswordView.setError(null);
            PhoneView.setError(null);
            courses.setError(null);

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
            if (role.equals("Tutor") && degree.equals("select your highest degree")) {
                DegreeView.setError("Need to select your highest degree");
                DegreeView.requestFocus();
                return;
            }
            if (course.isEmpty()) {
                courses.setError("Must offer at least 1 course");
                courses.requestFocus();
                return;
            }

            // Ensures courses are provided in format ABC1234
            String[] courseList = course.split("/");
            for (int i = 0; i < courseList.length; i++) {
                if (courseList[i].length() != 7 || !Character.isLetter(courseList[i].charAt(0)) ||
                        !Character.isLetter(courseList[i].charAt(1)) || !Character.isLetter(courseList[i].charAt(2)) ||
                        !Character.isDigit(courseList[i].charAt(3)) || !Character.isDigit(courseList[i].charAt(4)) ||
                        !Character.isDigit(courseList[i].charAt(5)) || !Character.isDigit(courseList[i].charAt(6))) {

                    courses.setError("Course codes must be formatted ABC1234");
                    courses.requestFocus();
                    return;
                }
            }

            // Create a new Tutor object and add it to the database
            User user = new Tutor(role, FirstName, LastName, Email, Password, Phone, degree, course);
            Database db = new Database(this);
            db.addUser(user);

            // Redirect to the login activity after successful registration
            Intent intent = new Intent(TutorActivity.this, LoginActivity.class);
            intent.putExtra("role", role);
            intent.putExtra("degree", degree);

            startActivity(intent);
            finish();
        } catch (IllegalArgumentException e) {
            Log.w("Registration Error", "Missing required fields.");
        }
    }
}
