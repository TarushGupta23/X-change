package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.x_change.utility.SwappingItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemsDisplayActivity extends AppCompatActivity {
    ImageView mainImg, profileImg, bookmark;
    TextView itemName,profileName, location, description, lookingFor, value;
    RecyclerView recyclerView;
    Button swapBtn;

    String itemId;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("items");
    SwappingItem item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_display);

        mainImg = findViewById(R.id.itemDisplay_mainItemImg);
        profileImg = findViewById(R.id.itemDisplay_profilePic);
        bookmark = findViewById(R.id.itemDisplay_bookmark);
        itemName = findViewById(R.id.itemDisplay_itemName);
        profileName = findViewById(R.id.itemDisplay_profileName);
        location = findViewById(R.id.itemDisplay_location);
        description = findViewById(R.id.itemDisplay_description);
        lookingFor = findViewById(R.id.itemDisplay_lookingFor);
        value = findViewById(R.id.itemDisplay_value);
        recyclerView = findViewById(R.id.itemDisplay_recyclerView);
        swapBtn = findViewById(R.id.itemDisplay_swapBtn);

        itemId = getIntent().getStringExtra("itemId");
        reference = reference.child(itemId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                item = snapshot.getValue(SwappingItem.class);
                if (item == null) {
                    finish(); //TODO: finish
                } else {
                    // TODO add bookmark if current user has it
                    itemName.setText(item.name);
                    // TODO: profile name, location and profile img
                    description.setText(item.description);
                    lookingFor.setText(item.lookingFor);
                    value.setText(item.value);
                    // TODO : recycler view
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}