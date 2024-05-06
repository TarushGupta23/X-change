package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.x_change.adapters.ItemImageUriAdapter;
import com.example.x_change.utility.Profile;
import com.example.x_change.utility.SwappingItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddItemActivity extends AppCompatActivity {
    boolean mainImgAdded = false;
    Uri mainImgUri;
    ArrayList<Uri> imageUris = new ArrayList<>();
    ArrayList<String> imgList = new ArrayList<>();
    ItemImageUriAdapter adapter;

    Button addBtn, cancelBtn;
    CardView addImageBtn;
    ImageView mainImg;
    RecyclerView recyclerView;
    EditText name, value, description, lookingFor;
    String uId = FirebaseAuth.getInstance().getUid();

    final int MAIN_IMG_REQ = 5, IMG_REQ = 6;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("items");
    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("people").child(uId);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        recyclerView = findViewById(R.id.addItem_RV);
        mainImg = findViewById(R.id.addItem_mainImg);
        addBtn = findViewById(R.id.addItem_add);
        cancelBtn = findViewById(R.id.addItem_cancel);
        addImageBtn = findViewById(R.id.addItem_addImg);
        name = findViewById(R.id.addItem_name);
        value = findViewById(R.id.addItem_value);
        description = findViewById(R.id.addItem_description);
        lookingFor = findViewById(R.id.addItem_lookingFor);

        adapter = new ItemImageUriAdapter(imageUris);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        cancelBtn.setOnClickListener(view -> {
            finish();
        });

        addBtn.setOnClickListener(view -> {
            String itemName = name.getText().toString();
            String itemValue = value.getText().toString();
            String itemDescription = description.getText().toString();
            String itemLookingFor = lookingFor.getText().toString();

            if (itemName.isEmpty() || itemValue.isEmpty() || itemDescription.isEmpty() || itemLookingFor.isEmpty() || !mainImgAdded) {
                Toast.makeText(this, "Please enter data", Toast.LENGTH_SHORT).show();
            } else {
                SwappingItem item = new SwappingItem(itemName, "", FirebaseAuth.getInstance().getUid(), itemDescription, itemLookingFor, itemValue, imgList, "mainImg");
                addItemToDatabase(item);
                finish();
            }
        });

        addImageBtn.setOnClickListener(view -> {
            if (!mainImgAdded) {
                fileSelector(MAIN_IMG_REQ);
            } else {
                fileSelector(IMG_REQ);
            }
        });
    }

    void addItemToDatabase(SwappingItem item) {
        DatabaseReference newSlot = reference.push();
        item.itemId = newSlot.getKey();
        storageRef = storageRef.child(item.itemId);
        newSlot.setValue(item);
        uploadPic("mainImg", mainImgUri);
        for (int i=0; i<imageUris.size(); i++) {
            uploadPic(i+"", imageUris.get(i));
        }
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Profile p = snapshot.getValue(Profile.class);
                if (p!= null) {
                    userReference.child("sellingItemIds").child(p.sellingItemIds.size()+"").setValue(item.itemId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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

        if (requestCode==MAIN_IMG_REQ && resultCode == RESULT_OK) {
            mainImgUri = data.getData();
            Picasso.get().load(mainImgUri).into(mainImg);
            mainImgAdded = true;
        } else {
            imageUris.add(data.getData());
            imgList.add((imgList.size() - 1) + "");
            adapter.notifyItemChanged(imageUris.size() - 1);
        }
    }

    public void uploadPic(String position, Uri uri) {
        storageRef.child(position).putFile(uri);
    }
}