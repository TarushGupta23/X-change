package com.example.x_change.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.x_change.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemImagesAdapter extends RecyclerView.Adapter<ItemImagesAdapter.ViewHolder> {
    static StorageReference storage = FirebaseStorage.getInstance().getReference();
    ArrayList<String> imgIds;
    static String itemId;

    public ItemImagesAdapter(ArrayList<String> imgIds, String itemId) {
        this.imgIds = imgIds;
        this.itemId = itemId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null; //TODO
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return imgIds.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cardItemImage_img);
        }

        void onBind(int position) {
            storage.child(itemId).child(position+"").getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).into(image);
            });
        }
    }
}
