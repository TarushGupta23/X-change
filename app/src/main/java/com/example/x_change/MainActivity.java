package com.example.x_change;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private CardView chatCard, profileCard;
    private RecyclerView recyclerView;

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
//            finish();
        });

        profileCard.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
//            finish();
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        recyclerView.setAdapter();
    }
}