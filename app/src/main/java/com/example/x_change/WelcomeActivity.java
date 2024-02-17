package com.example.x_change;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {
    Button login, signUp;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        login = findViewById(R.id.welcomeActivity_loginBtn);
        signUp = findViewById(R.id.welcomeActivity_signUpBtn);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }

        login.setOnClickListener(view -> {
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("title", "login");
            startActivity(i);
        });

        signUp.setOnClickListener(view -> {
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("title", "signup");
            startActivity(i);
        });
    }
}