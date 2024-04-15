package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.x_change.utility.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {
    CardView changeBanner, changeProfilePic, logOut;
    Button cancel, save;
    EditText name, location;
    ImageView banner, profilePic;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("people").child(FirebaseAuth.getInstance().getUid());
    Profile p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        changeProfilePic = findViewById(R.id.profileEditActivity_changeProfilePic);
        changeBanner = findViewById(R.id.profileEditActivity_changeBanner);
        logOut = findViewById(R.id.profileEditActivity_logOutBtn);
        cancel = findViewById(R.id.editProfileActivity_cancelBtn);
        save = findViewById(R.id.editProfileActivity_saveBtn);
        name = findViewById(R.id.editProfileActivity_name);
        location = findViewById(R.id.editProfileActivity_location);
        banner = findViewById(R.id.profileEditActivity_bannerImg);
        profilePic = findViewById(R.id.profileEditActivity_profileImage);

        logOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });

        cancel.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                p = snapshot.getValue(Profile.class);
                name.setText(p.profileName);
                location.setText(p.location);
                //TODO: change banner and profile imgs
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        save.setOnClickListener(view -> {
            p.profileName = name.getText().toString();
            p.location = location.getText().toString();

            if (p.location.equals("") || p.profileName.equals("")) {
                Toast.makeText(this, "Please enter name and location", Toast.LENGTH_SHORT).show();
            } else {
                reference.setValue(p);
            }
        });

        //TODO : banner and profile image btn
    }
}