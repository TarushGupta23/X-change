package com.example.x_change.utility;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.x_change.R;

import java.util.ArrayList;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {
    private ArrayList<SwappingItem> list;

    public MainActivityAdapter(ArrayList<SwappingItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindView(list.get(position));
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

        void onBindView(SwappingItem item) {
            bookmarkBtn.setOnClickListener(view -> {
                // check if user has those bookmarks and switch
//                bookmarkIcon.set(R.drawable.baseline_bookmark_24);
            });


        }
    }
}