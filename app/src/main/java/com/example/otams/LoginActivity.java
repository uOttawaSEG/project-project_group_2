package com.example.otams;
import com.example.otams.Database;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private Database db ;
    private TextView loginTextView;
    private EditText EmailView;
    private EditText PasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new Database(this);
        // Initialize views
        EmailView = findViewById(R.id.editTextTextEmailAddress);
        PasswordView = findViewById(R.id.editTextTextPassword);
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


        Button loginButton = findViewById(R.id.Loginbtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();


            }
        });

    }

    private void login() {
        String email = EmailView.getText().toString();
        String password = PasswordView.getText().toString();


        //if admin check in data base for log in
        if (email.isEmpty()) {
            EmailView.setError("Email is required");
            EmailView.requestFocus();
            return;
        }
         if (password.isEmpty()){
            PasswordView.setError("Password is required");
            PasswordView.requestFocus();
            return;
        }

        if(email.equals("admin@otams.ca")&& password.equals("admin")){
            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if(db.checkUser(email, password)){
            String userRole = db.getUserRole(email, password);
            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class) ;
            intent.putExtra("role", userRole);

            startActivity(intent);
            finish();
            return;

        } else{ //if login fails
            String status = db.getUserRegistrationStatus(email, password);
            if(status != null){
                if(status.equals("pending approval")){
                    PasswordView.setError("Your registration is pending approval");
                } else if(status.equals("Rejected")){
                    PasswordView.setError("Your registration has been rejected, please contact 613 777 6789");
                }else {
                    PasswordView.setError("Invalid email or password");
                }
            }  else {PasswordView.setError("Invalid email or password");}

            PasswordView.requestFocus();
        }
        }

    }
