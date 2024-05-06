package com.example.x_change;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    String chatId;
    String uId = FirebaseAuth.getInstance().getUid();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chats"); // getting chatId later in code
    ArrayList<Chat> chatList = new ArrayList<>();
    ImageView profileImg, sendBtn;
    CardView backBtn;
    RecyclerView recyclerView;
    EditText msgField;
    ChatActivityAdapter adapter;
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    boolean referenceValueChanged = false;
    ArrayList<String> tempChatIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = getIntent().getStringExtra("chatId");
        reference = reference.child(chatId);
        referenceValueChanged = true;

        profileImg = findViewById(R.id.chatActivity_profileImage);
        sendBtn = findViewById(R.id.chatActivity_sendBtn);
        backBtn = findViewById(R.id.chatActivity_backBtn);
        recyclerView = findViewById(R.id.chatActivity_recyclerView);
        msgField = findViewById(R.id.chatActivity_txtField);

        adapter = new ChatActivityAdapter(chatList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        backBtn.setOnClickListener(view -> { // end activity
            finish();
        });
        profileImg.setOnClickListener(view-> { // go to profile page
            Intent intent = new Intent(this, ProfileViewActivity.class);
            intent.putExtra("uId", chatId.replace(uId, ""));
            startActivity(intent);
            finish();
        });
        sendBtn.setOnClickListener(view -> { // push message to database
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
                msgField.setText("");
            }
        });

        reference.addValueEventListener(new ValueEventListener() { // constant updateing
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot snap: snapshot.getChildren()) {
                    Chat c = snap.getValue(Chat.class);
                    chatList.add(c);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        loadImageFromFirebase();
    }

    public void loadImageFromFirebase() { // set user image
        storageRef.child(chatId.replace(uId, "")).child("userProfileImage").getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(profileImg);
        });
    }

    @Override
    public void onBackPressed() { // if chat is empty then delete it
        super.onBackPressed();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!referenceValueChanged && !snapshot.child(chatId).hasChildren()) { // just in case when user quickly opened and closed chat activity
                    removeChatIds(uId);
                    removeChatIds(chatId.replace(uId, ""));
                } else if (!snapshot.hasChildren()) { // checking if chatId has any Chat child
                    removeChatIds(uId);
                    removeChatIds(chatId.replace(uId, ""));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    void removeChatIds(String id) { // remove reference of chat ids from both users
        tempChatIds.clear();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("people");
        userReference.child(id).child("chatIds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    tempChatIds.add(snap.getValue(String.class));
                }
                tempChatIds.remove(chatId);
                userReference.child(uId).child("chatIds").setValue(tempChatIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    };
}