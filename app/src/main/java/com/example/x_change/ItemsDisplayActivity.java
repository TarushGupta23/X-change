package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.x_change.adapters.ItemImagesAdapter;
import com.example.x_change.utility.Profile;
import com.example.x_change.utility.SwappingItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemsDisplayActivity extends AppCompatActivity {
    ImageView mainImg, profileImg, bookmark;
    TextView itemName,profileName, location, description, lookingFor, value;
    CardView profileCard;
    RecyclerView recyclerView;
    Button swapBtn;

    String itemId = "";
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("items");
    DatabaseReference sellerReference = FirebaseDatabase.getInstance().getReference().child("people");
    DatabaseReference bookmarkReference = FirebaseDatabase.getInstance().getReference().child("people").child(FirebaseAuth.getInstance().getUid()).child("bookmarkIds");
    SwappingItem item;
    ArrayList<String> bookmarks = new ArrayList<>();
    ArrayList<String> sellingItemIds = new ArrayList<>();
    boolean itemBookmarked = false;

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
        profileCard = findViewById(R.id.itemDisplay_profileCard);

        itemId = getIntent().getStringExtra("itemId");
        reference = reference.child(itemId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        bookmarkReference.addListenerForSingleValueEvent(new ValueEventListener() { // check if user has bookmarked the current item
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String bId = "";
                for (DataSnapshot snap: snapshot.getChildren()) {
                    bId = snap.getValue(String.class);
                    if (bId != null) {
                        bookmarks.add(bId);
                        if (bId.equals(itemId)) {
                            itemBookmarked = true;
                            bookmark.setImageResource(R.drawable.baseline_bookmark_24);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        reference.addListenerForSingleValueEvent(new ValueEventListener() { // get basic item data
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                item = snapshot.getValue(SwappingItem.class);
                if (item == null) {
                    Intent intent = new Intent(ItemsDisplayActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    updateItemData(item);
                    setImages(item);
                    if (item.sellerId.equals(FirebaseAuth.getInstance().getUid())) {
                        sellerViewing(item);
                    }

                    profileCard.setOnClickListener(view -> { // go to profile view
                        Intent intent = new Intent(ItemsDisplayActivity.this, ProfileViewActivity.class);
                        intent.putExtra("uId", item.sellerId);
                        startActivity(intent);
                    });

                    profileImg.setOnClickListener(view -> { // go to profile view
                        Intent intent = new Intent(ItemsDisplayActivity.this, ProfileViewActivity.class);
                        intent.putExtra("uId", item.sellerId);
                        startActivity(intent);
                    });

                    updateSellerData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        bookmark.setOnClickListener(view -> {
            if (itemBookmarked) {
                bookmark.setImageResource(R.drawable.baseline_bookmark_border_24);
                bookmarks.remove(itemId);
            } else {
                bookmark.setImageResource(R.drawable.baseline_bookmark_24);
                bookmarks.remove(itemId);
                bookmarks.add(itemId);
            }
            bookmarkReference.setValue(bookmarks);
            itemBookmarked = !itemBookmarked;
        });
    }

    void setImages(SwappingItem item) {
        FirebaseStorage.getInstance().getReference().child(item.sellerId).child("userProfileImage").getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(profileImg);
        });
        FirebaseStorage.getInstance().getReference().child(item.itemId).child("mainImg").getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(mainImg);
        });
    }

    void updateSellerData() {
        sellerReference.addListenerForSingleValueEvent(new ValueEventListener() { // seller data
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Profile sellerProfile = snapshot.getValue(Profile.class);
                if (sellerProfile != null) {
                    profileName.setText(sellerProfile.profileName);
                    location.setText(sellerProfile.location);
                    sellingItemIds = sellerProfile.sellingItemIds;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    void updateItemData(SwappingItem item) {
        sellerReference = sellerReference.child(item.sellerId);
        itemName.setText(item.name);
        description.setText(item.description);
        lookingFor.setText(item.lookingFor);
        value.setText(item.value);
        recyclerView.setAdapter(new ItemImagesAdapter(item.images, item.itemId));
        if (item.images.size() == 0) {
            recyclerView.setVisibility(View.GONE);
        }
    }

    void sellerViewing(SwappingItem item) {
        swapBtn.setText("Remove Item");
        bookmark.setVisibility(View.GONE);
        swapBtn.setOnClickListener(view -> {
            sellingItemIds.remove(item.itemId);
            sellerReference.child("sellingItemIds").setValue(sellingItemIds);
            reference.setValue(null);
            FirebaseStorage.getInstance().getReference().child(item.itemId).delete();
            finish();
        });
    }
}