package com.example.x_change.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
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


public class ItemImageUriAdapter extends RecyclerView.Adapter<ItemImageUriAdapter.ViewHolder> {
    static StorageReference storage = FirebaseStorage.getInstance().getReference();
    ArrayList<Uri> imgUris;

    public ItemImageUriAdapter(ArrayList<Uri> imageUri) {
        this.imgUris = imageUri;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_image, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(imgUris.get(position)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imgUris.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cardItemImage_img);
        }
    }
}
