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
                register();
            }
        });



    }
    private void register() {
        try {
            EditText FirstNameView = findViewById(R.id.editTextFirstName);
            EditText LastNameView = findViewById(R.id.editTextLastName);
            EditText EmailView = findViewById(R.id.editTextTextEmailAddress);
            EditText PasswordView = findViewById(R.id.editTextTextPassword);
            EditText PhoneView = findViewById(R.id.editTextPhone);
            Spinner Hdegree = findViewById(R.id.degree);
            EditText courses = findViewById(R.id.courses);
            Spinner RoleView = findViewById(R.id.roleSpinner);

            String FirstName = FirstNameView.getText().toString();
            String LastName = LastNameView.getText().toString();
            String Email = EmailView.getText().toString();
            String Password = PasswordView.getText().toString();
            String Phone = PhoneView.getText().toString();
            String role = RoleView.getSelectedItem().toString();
            String degree = Hdegree.getSelectedItem().toString();
            String course = courses.getText().toString();

            FirstNameView.setError(null);
            LastNameView.setError(null);
            EmailView.setError(null);
            PasswordView.setError(null);
            PhoneView.setError(null);
            courses.setError(null);

            if (FirstName.isEmpty()){
                FirstNameView.setError("First name can't be empty");
                FirstNameView.requestFocus();
                return;
            }
            if (LastName.isEmpty()){
                LastNameView.setError("Last name can't be empty");
                LastNameView.requestFocus();
                return;
            }
            if (Email.isEmpty()){
                EmailView.setError("Email can't be empty");
                EmailView.requestFocus();
                return;
            }
            if (Password.isEmpty()){
                PasswordView.setError("Password can't be empty");
                PasswordView.requestFocus();
                return;
            }
            if (Phone.isEmpty()){
                PhoneView.setError("Phone number can't be empty");
                PhoneView.requestFocus();
                return;
            } else if (Phone.length() != 10){
                PhoneView.setError("Invalid phone number");
                PhoneView.requestFocus();
                return;
            }
            if (role.equals("Tutor") && degree.equals("select your highest degree")) {
                EmailView.setError("Need to select your highest degree");
                return;
            }

            User user = new Tutor(role, FirstName, LastName, Email, Password, Phone, degree, course);
            Database db = new Database(this);
            db.addUser(user);

            Intent intent = new Intent(TutorActivity.this, LoginActivity.class);
            intent.putExtra("role", role);
            intent.putExtra("degree", degree);

            startActivity(intent);
            finish();
        } catch (IllegalArgumentException e) {
            Log.w("Registration Error","Missing required fields.");
        }
    }
}
