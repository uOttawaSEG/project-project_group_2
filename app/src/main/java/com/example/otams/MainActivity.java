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

public class MainActivity extends AppCompatActivity {
    private TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginTextView = findViewById(R.id.textView);
        loginTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


        // Reference the Spinner
        Spinner spinner = findViewById(R.id.roleSpinner);

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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedOption = parent.getItemAtPosition(position).toString();
                if (selectedOption.equals("Tutor")) {
                    // Open the second activity
                    Intent intent = new Intent(MainActivity.this, TutorActivity.class);
                    startActivity(intent);
                }
            }
            public void onNothingSelected(AdapterView<?> parent ){
                return;
            }
        });

        Button registerButton = findViewById(R.id.Registerbtn);
        registerButton.setOnClickListener(new View.OnClickListener() {

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
            Spinner ProgramView = findViewById(R.id.program);
            Spinner RoleView = findViewById(R.id.roleSpinner);

            String FirstName = FirstNameView.getText().toString();
            String LastName = LastNameView.getText().toString();
            String Email = EmailView.getText().toString();
            String Password = PasswordView.getText().toString();
            String Phone = PhoneView.getText().toString();
            String role = RoleView.getSelectedItem().toString();
            String program = ProgramView.getSelectedItem().toString();

            FirstNameView.setError(null);
            LastNameView.setError(null);
            EmailView.setError(null);
            PasswordView.setError(null);
            PhoneView.setError(null);

            if (FirstName.isEmpty()){
                FirstNameView.setError("First name can't be empty");
                //A neat trick that set the user's cursor into the input box after error check
                //And highlight it too
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
            if (role.isEmpty()){
                Log.w("Registration error","No role was chosen");
                return;
            }

            if (role.equals("Student") && program.equals("Select your program")) {
                EmailView.setError("Need to select a program");
                return;
            }

            User user = new User(role, FirstName, LastName, Email, Password, Phone);
            Database db = new Database(this);
            db.addUser(user);

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("role", role);
            intent.putExtra("program", program);

            startActivity(intent);
            finish();
        } catch (IllegalArgumentException e) {
            Log.w("Registration Error","Missing required fields.");
        }
    }

}
