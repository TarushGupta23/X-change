package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {
    CardView changeBanner, changeProfilePic, logOut;
    Button cancel, save;
    EditText name, location;
    ImageView banner, profilePic;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("people").child(FirebaseAuth.getInstance().getUid());
    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid());
    Profile p;
    Uri profileUri, bannerURI;
    boolean profileChange = false, bannerChange = false;
    final int PROFILE_REQ = 2, BANNER_REQ = 3;

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

        loadImageFromFirebase();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                p = snapshot.getValue(Profile.class);
                name.setText(p.profileName);
                location.setText(p.location);
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
                uploadPic();
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        changeBanner.setOnClickListener(view -> {
            fileSelector(BANNER_REQ);
        });

        changeProfilePic.setOnClickListener(view -> {
            fileSelector(PROFILE_REQ);
        });
    }

    public void loadImageFromFirebase() {
        storageRef.child("userProfileImage").getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(profilePic);
        });

        storageRef.child("userBannerImage").getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(banner);
        });
    }

    public void fileSelector(int reqCode) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, reqCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_REQ && resultCode == RESULT_OK) {
            profileUri = data.getData();
            Picasso.get().load(profileUri).into(profilePic);
            profileChange = true;
        } else if (requestCode == BANNER_REQ && resultCode == RESULT_OK) {
            bannerURI = data.getData();
            Picasso.get().load(bannerURI).into(banner);
            bannerChange = true;
        }
    }

    public void uploadPic() {
        if (bannerChange) {
            storageRef.child("userBannerImage").putFile(bannerURI);
        }
        if (profileChange) {
            storageRef.child("userProfileImage").putFile(profileUri);
        }
    }
}