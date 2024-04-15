package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.x_change.adapters.MainActivityAdapter;
import com.example.x_change.utility.Profile;
import com.example.x_change.utility.SwappingItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private CardView chatCard, profileCard;
    private RecyclerView recyclerView;
    MainActivityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatCard = findViewById(R.id.mainActivity_chatCard);
        profileCard = findViewById(R.id.mainActivity_profileCard);
        recyclerView = findViewById(R.id.mainActivity_recyclerView);

        chatCard.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChatListActivity.class);
            startActivity(intent);
            finish();
        });

        profileCard.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        ArrayList<SwappingItem> list = new ArrayList<>();
        ArrayList<String> bookmarkIds = new ArrayList<>();
        adapter = new MainActivityAdapter(list, this, bookmarkIds);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("items");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    SwappingItem item = snap.getValue(SwappingItem.class);
                    if (item != null) {
                        list.add(item);
                    }
                }

                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        updateBookmarkIds(bookmarkIds);

        recyclerView.setAdapter(adapter);
    }

    void updateBookmarkIds(ArrayList<String> bIds) {
        bIds.clear();
        FirebaseDatabase.getInstance().getReference().child("people").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Profile p = snapshot.getValue(Profile.class);
                        if (p != null) {
                            bIds.addAll(p.bookmarkIds);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}