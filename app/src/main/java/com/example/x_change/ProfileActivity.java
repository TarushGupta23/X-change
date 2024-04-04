package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.x_change.adapters.ProfileBookmarksAdapter;
import com.example.x_change.adapters.ProfileItemsAdapter;
import com.example.x_change.adapters.ReviewsAdapter;
import com.example.x_change.utility.Profile;
import com.example.x_change.utility.Review;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private String uId = FirebaseAuth.getInstance().getUid();

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("people").child(uId);
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(uId);
    private DatabaseReference reviewReference = FirebaseDatabase.getInstance().getReference().child("reviews");
    private DatabaseReference itemReference = FirebaseDatabase.getInstance().getReference().child("items");

    private CardView topLeftBtn, topRightBtn, leftBtn, rightBtn, centerBtn;
    private TextView name, address, itemCount, avgReview, swapCount, viewReviews;
    private ImageView profileImg, bannerImg;
    private RecyclerView bookmarksRV, itemsRV, reviewsRV;

    private ProfileBookmarksAdapter bookmarksAdapter;
    private ProfileItemsAdapter itemsAdapter;
    private ReviewsAdapter reviewsAdapter;

    private Profile p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        topLeftBtn = findViewById(R.id.profileActivity_topLeftBtn);
        topRightBtn = findViewById(R.id.profileActivity_topRightBtn);
        leftBtn = findViewById(R.id.profileActivity_leftBtn);
        rightBtn = findViewById(R.id.profileActivity_rightBtn);
        centerBtn = findViewById(R.id.profileActivity_centerBtn);
        name = findViewById(R.id.profileActivity_name);
        address = findViewById(R.id.profileActivity_address);
        itemCount = findViewById(R.id.profileActivity_itemCount);
        avgReview = findViewById(R.id.profileActivity_avgReviews);
        swapCount = findViewById(R.id.profileActivity_swapCount);
        viewReviews = findViewById(R.id.profileActivity_viewReviews);
        profileImg = findViewById(R.id.profileActivity_profileImage);
        bannerImg = findViewById(R.id.profileActivity_bannerImage);
        bookmarksRV = findViewById(R.id.profileActivity_bookmarkRV);
        itemsRV = findViewById(R.id.profileActivity_itemsRV);
        reviewsRV = findViewById(R.id.profileActivity_reviewsRV);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                p = snapshot.getValue(Profile.class);
                loadImageFromFirebase();
                name.setText(p.profileName);
                address.setText(p.location);
                if (p.sellingItemIds != null) {
                    itemCount.setText(p.sellingItemIds.size() + "");
                } else {
                    itemCount.setText("0");
                }
                swapCount.setText(""+p.successfulSwaps);
                createAdapters();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void createAdapters() {
        // ---- Review List
        ArrayList<Review> reviewList = new ArrayList<>();
        reviewReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float average = 0;
                for (String reviewId: p.reviewIds) {
                    DataSnapshot snap = snapshot.child(reviewId);
                    Review r = snap.getValue(Review.class);
                    if (r != null) {
                        average += r.stars;
                        reviewList.add(r);
                    }
                }
                if (reviewList.size() == 0) {
                    average = 0;
                } else {
                    average /= reviewList.size();
                }
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String formattedAverage = decimalFormat.format(average);
                avgReview.setText(formattedAverage);

                reviewsAdapter = new ReviewsAdapter(reviewList, Math.min(3, reviewList.size()), ProfileActivity.this);
                reviewsRV.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                reviewsRV.setAdapter(reviewsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // ---- items and bookmarks
        ArrayList<SwappingItem> bookmarkList = new ArrayList<>();
        ArrayList<SwappingItem> sellingItemList = new ArrayList<>();

        itemReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (String bookmarkId: p.bookmarkIds) {
                    DataSnapshot snap = snapshot.child(bookmarkId);
                    SwappingItem i = snap.getValue(SwappingItem.class);
                    if (i != null) {
                        bookmarkList.add(i);
                    }
                }
                bookmarksAdapter = new ProfileBookmarksAdapter(bookmarkList, ProfileActivity.this);
                bookmarksRV.setLayoutManager(new LinearLayoutManager(ProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
                bookmarksRV.setAdapter(bookmarksAdapter);

                for (String itemId: p.sellingItemIds) {
                    DataSnapshot snap = snapshot.child(itemId);
                    SwappingItem i = snap.getValue(SwappingItem.class);
                    if (i != null) {
                        sellingItemList.add(i);
                    }
                }
                itemsAdapter = new ProfileItemsAdapter(sellingItemList);
                itemsRV.setLayoutManager(new LinearLayoutManager(ProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
                itemsRV.setAdapter(itemsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    public void loadImageFromFirebase() {
        storageRef.child("userProfileImage").getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).into(profileImg);
            });

        storageRef.child("userBannerImage").getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).into(bannerImg);
            });
    }

    //TODO: view all reviews - if > 3
    //  display "none" for 0 items
}