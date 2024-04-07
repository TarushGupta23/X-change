package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.atomic.AtomicInteger;

public class ProfileViewActivity extends AppCompatActivity {
    private String uId;
    private String currId = FirebaseAuth.getInstance().getUid();

    private DatabaseReference reference;
    private StorageReference storageRef;
    private final DatabaseReference reviewReference = FirebaseDatabase.getInstance().getReference().child("reviews");
    private final DatabaseReference itemReference = FirebaseDatabase.getInstance().getReference().child("items");

    private CardView topLeftBtn, topRightBtn, leftBtn, rightBtn, centerBtn;
    private TextView name, address, itemCount, avgReview, swapCount, viewReviews, viewItems;
    private ImageView profileImg, bannerImg;
    private RecyclerView itemsRV, reviewsRV;

    private ProfileItemsAdapter itemsAdapter;
    private ReviewsAdapter reviewsAdapter;
    ArrayList<Review> reviewList;

    private Profile p;

    private boolean reviewGiven = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        uId = getIntent().getStringExtra("uId");
        reference = FirebaseDatabase.getInstance().getReference().child("people").child(uId);
        storageRef = FirebaseStorage.getInstance().getReference().child(uId);

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
        itemsRV = findViewById(R.id.profileActivity_itemsRV);
        reviewsRV = findViewById(R.id.profileActivity_reviewsRV);
        viewItems = findViewById(R.id.profileActivity_viewItems);

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

        topLeftBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChatListActivity.class);
            startActivity(intent);
            finish();
        });

        topRightBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        rightBtn.setOnClickListener(view -> {
            if (!reviewGiven) {
                reviewDialog();
            } else {
                Toast.makeText(this, "Review already provided", Toast.LENGTH_SHORT).show();
            }
        });

        leftBtn.setOnClickListener(view -> {
            // TODO create a share btn
        });

        centerBtn.setOnClickListener(view -> { // TODO: add chatid in both users and redirect to chatActivity
            Intent intent = new Intent(this, ChatListActivity.class);
            String currUId = currId;
            if (currUId.compareTo(uId) < 0) {
                currUId = currUId + uId;
            } else {
                currUId = uId + currUId;
            }
            intent.putExtra("chatId", currUId);
            startActivity(intent);
        });
    }

    public void createAdapters() {
        // ---- Review List
        reviewList = new ArrayList<>();
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
                        if (r.creatorId.equals(currId)) {
                            reviewGiven = true;
                        }
                    }
                }
                if (reviewList.size() == 0) {
                    findViewById(R.id.reviewsNone).setVisibility(View.VISIBLE);
                    average = 0;
                } else {
                    if (reviewList.size() > 3) {
                        viewReviews.setVisibility(View.VISIBLE);
                    }
                    average /= reviewList.size();
                }
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String formattedAverage = decimalFormat.format(average);
                avgReview.setText(formattedAverage);

                reviewsAdapter = new ReviewsAdapter(reviewList, Math.min(3, reviewList.size()), ProfileViewActivity.this);
                reviewsRV.setLayoutManager(new LinearLayoutManager(ProfileViewActivity.this));
                reviewsRV.setAdapter(reviewsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        // ---- items
        ArrayList<SwappingItem> sellingItemList = new ArrayList<>();
        itemReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (String itemId: p.sellingItemIds) {
                    DataSnapshot snap = snapshot.child(itemId);
                    SwappingItem i = snap.getValue(SwappingItem.class);
                    if (i != null) {
                        sellingItemList.add(i);
                    }
                }
                itemsAdapter = new ProfileItemsAdapter(sellingItemList); // TODO : adapter changes
                itemsRV.setLayoutManager(new LinearLayoutManager(ProfileViewActivity.this, LinearLayoutManager.HORIZONTAL, false));
                itemsRV.setAdapter(itemsAdapter);
                if (sellingItemList.size() == 0) {
                    findViewById(R.id.itemsNone).setVisibility(View.VISIBLE);
                } else if (sellingItemList.size() > SwappingItem.HORIZONTAL_ITEM_COUNT) {
                    viewItems.setVisibility(View.VISIBLE);
                }
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

    void reviewDialog() {
        android.app.AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_review, null);
        EditText msg;
        Button done, cancel;
        ImageView stars[] = new ImageView[5];
        AtomicInteger starCount = new AtomicInteger(); // this is just an integer

        msg = dialogView.findViewById(R.id.reviewDialog_msg);
        stars[0] = dialogView.findViewById(R.id.reviewDialog_star1);
        stars[1] = dialogView.findViewById(R.id.reviewDialog_star2);
        stars[2] = dialogView.findViewById(R.id.reviewDialog_star3);
        stars[3] = dialogView.findViewById(R.id.reviewDialog_star4);
        stars[4] = dialogView.findViewById(R.id.reviewDialog_star5);
        done = dialogView.findViewById(R.id.reviewDialog_doneBtn);
        cancel = dialogView.findViewById(R.id.reviewDialog_cancelBtn);

        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        for (int i = 0; i<stars.length; i++) {
            int currStar = i; // do not remove this
            stars[i].setOnClickListener(view -> {
                for (int j = currStar+1; j<stars.length; j++) {
                    stars[j].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_star_border_24));
                    stars[j].setColorFilter(ContextCompat.getColor(this, R.color.indigo_dark), PorterDuff.Mode.SRC_IN);
                }

                for (int j = 0; j <= currStar; j++) {
                    stars[j].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_star_24));
                    stars[j].setColorFilter(ContextCompat.getColor(this, R.color.pink), PorterDuff.Mode.SRC_IN);
                }
                starCount.set(currStar + 1);
            });
        }

        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        done.setOnClickListener(view -> {
            String text = msg.getText().toString();
            DatabaseReference newSlot =  reviewReference.push();
            newSlot.setValue(new Review(newSlot.getKey(), currId, text, starCount.get()));
            reference.child("profileId").child(reviewList.size()+"").setValue(newSlot.getKey());
        });
        alertDialog.show();
    }
}