package com.example.otams;
import com.example.otams.Database;

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

public class LoginActivity extends AppCompatActivity {
    private Database db;
    private TextView loginTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText EmailView = findViewById(R.id.editTextTextEmailAddress);
        EditText PasswordView = findViewById(R.id.editTextTextPassword);
        Spinner RoleView = findViewById(R.id.roleSpinner);
        loginTextView = findViewById(R.id.textView);
        loginTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        String Email = EmailView.getText().toString();
        String Password = PasswordView.getText().toString();

        db = new Database(this);

        setupRole(RoleView);


        Button loginButton = findViewById(R.id.Loginbtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = EmailView.getText().toString();
                String Password = PasswordView.getText().toString();
                String Role = RoleView.getSelectedItem().toString();
                boolean isValid = true;
                if (Email.isEmpty()) {
                    EmailView.setError("Email can't be empty");
                    EmailView.requestFocus();
                    isValid = false;
                }
                if (Password.isEmpty()) {
                    PasswordView.setError("Password can't be empty");
                    PasswordView.requestFocus();
                    isValid = false;
                }

                if (isValid){
                    login(Role, Email, Password);
                }

            }
        });

    }

    private void setupRole(Spinner spinner){
        String[] roles = {"Student", "Tutor"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    private void login(String role, String email, String password) {
        if (db.checkUser(email, password)){
            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
            intent.putExtra("role", role);
            startActivity(intent);
            finish();
        } else {
            EditText PasswordView = findViewById(R.id.editTextTextPassword);
            PasswordView.setError("Incorrect Password. Try again");
            PasswordView.requestFocus();
        }

    }
}
