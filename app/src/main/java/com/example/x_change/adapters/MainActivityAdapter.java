package com.example.x_change.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.x_change.ItemsDisplayActivity;
import com.example.x_change.R;
import com.example.x_change.utility.SwappingItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {
    private ArrayList<SwappingItem> list;
    Context context;
    private static ArrayList<String> bookmarkIds;

    public MainActivityAdapter(ArrayList<SwappingItem> list, Context context, ArrayList<String> bookmarkIds) {
        this.list = list;
        this.context = context;
        MainActivityAdapter.bookmarkIds = bookmarkIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindView(list.get(position), context);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, bookmarkIcon;
        TextView name;
        CardView bookmarkBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.itemCard_itemImage);
            bookmarkIcon = itemView.findViewById(R.id.itemCard_bookmarkIcon);
            name = itemView.findViewById(R.id.itemCard_itemName);
            bookmarkBtn = itemView.findViewById(R.id.itemCard_bookmarkBtn);
        }

        void onBindView(SwappingItem item, Context context) {
            if (bookmarkIds.contains(item.itemId)) {
                bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_24);
            }
            bookmarkBtn.setOnClickListener(view -> {
                if (bookmarkIds.contains(item.itemId)) {
                    bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_border_24);
                    bookmarkIds.remove(item.itemId);
                } else {
                    bookmarkIds.add(item.itemId);
                    bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_24);
                }
                FirebaseDatabase.getInstance().getReference().child("people").child(FirebaseAuth.getInstance().getUid())
                        .child("bookmarkIds").setValue(bookmarkIds);
            });

            name.setText(item.name);
            FirebaseStorage.getInstance().getReference().child(item.itemId).child("mainImg").getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).into(image);
            });
            image.setOnClickListener(view -> {
                Intent intent = new Intent(context, ItemsDisplayActivity.class);
                intent.putExtra("itemId", item.itemId);
                context.startActivity(intent);
            });
        }
    }
}