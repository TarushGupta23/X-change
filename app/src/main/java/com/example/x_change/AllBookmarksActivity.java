package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import com.example.x_change.adapters.BookmarksAdapter;
import com.example.x_change.utility.SwappingItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllBookmarksActivity extends AppCompatActivity {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("people");
    DatabaseReference itemsReference = FirebaseDatabase.getInstance().getReference().child("items");
    ArrayList<SwappingItem> items = new ArrayList<>();
    RecyclerView recyclerView;
    String uId = FirebaseAuth.getInstance().getUid();
    BookmarksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_bookmarks);

        reference = reference.child(uId);

        recyclerView = findViewById(R.id.allBookmarksRV);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new BookmarksAdapter(items, this);
        recyclerView.setAdapter(adapter);

        reference.child("bookmarkIds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap: snapshot.getChildren()) {
                    String id = snap.getValue(String.class);
                    if (id != null) {
                        getItem(id);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    void getItem(String id) {
        itemsReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SwappingItem item = snapshot.getValue(SwappingItem.class);
                if (item != null) { items.add(item); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}