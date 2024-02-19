package com.example.x_change;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateProfileActivity extends AppCompatActivity {
    private String uId = FirebaseAuth.getInstance().getUid();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(uId);
    private EditText nameInput, addressInput;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        if (!reference.child("name").equals("")) {
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        submit = findViewById(R.id.createProfileBtn);
        nameInput = findViewById(R.id.createProfile_name);
        addressInput = findViewById(R.id.createProfile_address);

        submit.setOnClickListener(view -> {
            String name = nameInput.getText().toString();
            String address = addressInput.getText().toString();
            if (name.equals("") || address.equals("")) {
                Toast.makeText(this, "Please enter name and address", Toast.LENGTH_SHORT).show();
            } else {
                reference.child("name").setValue(name);
                reference.child("address").setValue(address);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}