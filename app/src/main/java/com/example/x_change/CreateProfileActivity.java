package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class CreateProfileActivity extends AppCompatActivity {
    private final String uId = FirebaseAuth.getInstance().getUid();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(uId);
    private EditText nameInput, addressInput;
    private Button submit;

    private Uri imageUri;
    private ImageView imageView;
    private StorageReference storage = FirebaseStorage.getInstance().getReference().child(uId);
    private boolean isImageSet = false;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        reference.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = (String) dataSnapshot.getValue();
                if (userName != null && !userName.equals("")) {
                    Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        submit = findViewById(R.id.createProfileBtn);
        nameInput = findViewById(R.id.createProfile_name);
        addressInput = findViewById(R.id.createProfile_address);
        imageView = findViewById(R.id.createProfile_imageView);

        submit.setOnClickListener(view -> {
            String name = nameInput.getText().toString();
            String address = addressInput.getText().toString();
            if (name.equals("") || address.equals("")) {
                Toast.makeText(this, "Please enter name and address", Toast.LENGTH_SHORT).show();
            } else if (!isImageSet) {
                Toast.makeText(this, "Please select profile image", Toast.LENGTH_SHORT).show();
            }else {
                reference.child("name").setValue(name);
                reference.child("address").setValue(address);
                uploadPic();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imageView.setOnClickListener(view -> {
            fileSelector();
        });
    }

    public void fileSelector() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
            isImageSet = true;
        }
    }

    public void uploadPic() {
        StorageReference reference1 = storage.child("userProfileImage");
        reference1.putFile(imageUri);
    }
}