package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.x_change.adapters.ChatActivityAdapter;
import com.example.x_change.utility.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    String chatId;
    String uId = FirebaseAuth.getInstance().getUid();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chats");
    ArrayList<Chat> chatList = new ArrayList<>();
    ImageView profileImg, sendBtn;
    CardView backBtn;
    RecyclerView recyclerView;
    EditText msgField;
    ChatActivityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = getIntent().getStringExtra("chatId");
        reference = reference.child(chatId);

        profileImg = findViewById(R.id.chatActivity_profileImage); //TODO - click profile to reach profile img
        sendBtn = findViewById(R.id.chatActivity_sendBtn);
        backBtn = findViewById(R.id.chatActivity_backBtn);
        recyclerView = findViewById(R.id.chatActivity_recyclerView);
        msgField = findViewById(R.id.chatActivity_txtField);

        adapter = new ChatActivityAdapter();
//        adapter;

        backBtn.setOnClickListener(view -> {
            finish();
        });
        profileImg.setOnClickListener(view-> {
            Intent intent = new Intent(this, ProfileActivity.class); // TODO - change this activity, and change profile pic
            intent.putExtra("uId", chatId.replace(uId, ""));
            startActivity(intent);
        });
        sendBtn.setOnClickListener(view -> {
            String msg = msgField.getText().toString();
            msg = msg.trim();
            if (msg.isEmpty()) {
                Toast.makeText(this, "Empty message", Toast.LENGTH_SHORT).show();
            } else {
                Date currentDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = dateFormat.format(currentDate);
                Chat c = new Chat(uId, msg, formattedDate);
                reference.child(chatList.size() + "").setValue(c);
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot snap: snapshot.getChildren()) {
                    Chat c = snap.getValue(Chat.class);
                    chatList.add(c);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}