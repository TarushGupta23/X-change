package com.example.x_change.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.x_change.R;
import com.example.x_change.utility.Chat;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatActivityAdapter extends RecyclerView.Adapter<ChatActivityAdapter.ViewHolder> {
    ArrayList<Chat> list;
    final String uId = FirebaseAuth.getInstance().getUid();

    public ChatActivityAdapter(ArrayList<Chat> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == R.layout.chat_sent) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sent, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_received, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(list.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = list.get(position);
        if (chat.sentBy.equals(uId)) {
            return R.layout.chat_sent;
        } else {
            return R.layout.chat_received;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView msg, date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.tv1);
            date = itemView.findViewById(R.id.tv2);
        }

        public void onBind(Chat chat) {
            msg.setText(chat.msg);
            date.setText(chat.date);
        }
    }
}
