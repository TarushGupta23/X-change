package com.example.x_change.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.x_change.R;
import com.example.x_change.utility.SwappingItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProfileBookmarksAdapter extends RecyclerView.Adapter<ProfileBookmarksAdapter.ViewHolder> {
    private String uId = FirebaseAuth.getInstance().getUid();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("people").child(uId).child("bookmarkIds");
    private ArrayList<SwappingItem> list;
    private Context context;
    private TextView noneText;

    public ProfileBookmarksAdapter(ArrayList<SwappingItem> list, Context context, TextView textView) {
        this.list = list;
        this.context = context;
        this.noneText = textView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindView(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    void removeItem(int position) {
        list.remove(position);
        ArrayList<String> newIds = new ArrayList<>();
        for (SwappingItem item: list) {
            newIds.add(item.itemId);
        }
        reference.setValue(newIds);
        notifyItemRemoved(position);
        if (list.isEmpty()) {
            noneText.setVisibility(View.VISIBLE);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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

        void onBindView(SwappingItem item, int position) {
            bookmarkBtn.setCardBackgroundColor(context.getResources().getColor(R.color.pink_light));
            bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_24);
            bookmarkBtn.setOnClickListener(view -> {
                removeItem(position);
            });
            name.setText(item.name);
            // TODO: change image
        }
    }
}