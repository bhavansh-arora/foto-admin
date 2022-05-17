package com.foto.fotoadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize_ui();
    }

    private void initialize_ui() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        email.bringToFront();
    }

    public void login(View view) {
        String emailValue = email.getText().toString().trim();
        String passwordValue = password.getText().toString().trim();

        if(emailValue.isEmpty()){
            email.setError("Please enter a valid email address.");
            email.requestFocus();
        }
        else if (passwordValue.isEmpty()){
            password.setError("Please enter a valid password.");
            password.requestFocus();
        }
        else {
            if(emailValue.equals("justinelouisepeng@gmail.com")){
                if(passwordValue.equals("Qwerty@21")) {
                    Intent intent = new Intent(this, ListData.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"Password don't match the corresponding email address.",Toast.LENGTH_LONG).show();
                    password.setError("Please enter the correct password.");
                    password.requestFocus();
                }
            } else {
                Toast.makeText(getApplicationContext(), "This email address is not registered with us.", Toast.LENGTH_LONG).show();
                email.setError("This email address is not registered with us.");
                email.requestFocus();
            }
        }
    }
}