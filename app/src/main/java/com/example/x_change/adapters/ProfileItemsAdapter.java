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
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileItemsAdapter extends RecyclerView.Adapter<ProfileItemsAdapter.ViewHolder> {
    private ArrayList<SwappingItem> list;
    Context context;

    public ProfileItemsAdapter(ArrayList<SwappingItem> list, Context context) {
        this.list = list;
        this.context = context;
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
        return Math.min(list.size(), SwappingItem.HORIZONTAL_ITEM_COUNT);
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
            bookmarkBtn.setVisibility(View.INVISIBLE);
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