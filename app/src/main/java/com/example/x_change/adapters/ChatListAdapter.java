package com.example.x_change.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.x_change.R;
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

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>{
    ArrayList<String> chatIdList = new ArrayList<>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chats");
    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("people");
    static StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    String uId = FirebaseAuth.getInstance().getUid();

    public ChatListAdapter(ArrayList<String> chatIdList) {
        this.chatIdList = chatIdList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_chat_with, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String senderId = chatIdList.get(position).replace(uId, "");
        reference.child(chatIdList.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot snap = snapshot.child((snapshot.getChildrenCount()-1)+""); // getting the last chat item
                String lastMsg = snap.child("msg").getValue(String.class);
                String date = snap.child("date").getValue(String.class);

                userReference.child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        String userName = snapshot2.child("profileName").getValue(String.class);
                        ViewItem item = new ViewItem(lastMsg, date, senderId, userName);
                        holder.onBind(item);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public int getItemCount() {
        return chatIdList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImg;
        TextView name, msg, date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.chatWithCard_img);
            name = itemView.findViewById(R.id.chatWithCard_name);
            msg = itemView.findViewById(R.id.chatWithCard_msg);
            date = itemView.findViewById(R.id.chatWithCard_date);
        }

        void onBind(ViewItem item) {
            name.setText(item.userName);
            date.setText(item.date);
            msg.setText(item.lastMsg);
            storageRef.child(item.userId).child("userProfileImage").getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).into(profileImg);
            });
        }
    }

    static class ViewItem {
        String lastMsg, date, userId, userName;

        public ViewItem(String lastMsg, String date, String userId, String userName) {
            this.lastMsg = lastMsg;
            this.date = date;
            this.userId = userId;
            this.userName = userName;
        }
    }
}

// TODO: add click listener to sent to next activity
//  add top buttons for home and profile