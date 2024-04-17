package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.x_change.adapters.ReviewsAdapter;
import com.example.x_change.utility.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllReviewsActivity extends AppCompatActivity {
    ReviewsAdapter adapter;
    RecyclerView recyclerView;
    String uId;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("people");
    DatabaseReference reviewReference = FirebaseDatabase.getInstance().getReference().child("reviews");
    ArrayList<Review> reviewList = new ArrayList<>();
    ArrayList<String> reviewIds = new ArrayList<>();
    String currId = FirebaseAuth.getInstance().getUid();
    boolean reviewProvided = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reviews);

        recyclerView = findViewById(R.id.allReviewsRV);

        uId = getIntent().getStringExtra("uId");
        reference.child(uId).child("reviewIds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String reviewId = snap.getValue(String.class);
                    if (reviewId != null) {
                        reviewIds.add(reviewId);
                    }
                }

                getReviews();
                adapter = new ReviewsAdapter(reviewList, reviewList.size(), AllReviewsActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    void getReviews() {
        for (String rId: reviewIds) {
            reviewReference.child(rId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Review r = snapshot.getValue(Review.class);
                    if (r != null) {
                        reviewList.add(r);
                        if (r.creatorId.equals(currId)) {
                            reviewProvided = true;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}