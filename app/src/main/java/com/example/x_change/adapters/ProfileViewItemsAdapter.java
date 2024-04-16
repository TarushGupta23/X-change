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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileViewItemsAdapter extends RecyclerView.Adapter<ProfileViewItemsAdapter.ViewHolder> {
    private ArrayList<SwappingItem> list;
    private ArrayList<String> bookmarkList;
    private Context context;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("people").child(FirebaseAuth.getInstance().getUid()).child("bookmarkIds");
    public ProfileViewItemsAdapter(ArrayList<SwappingItem> list, ArrayList<String> bookmarkList, Context context) {
        this.list = list;
        this.bookmarkList = bookmarkList;
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
        holder.onBindView(list.get(position));
    }

    @Override
    public int getItemCount() {
        return Math.min(list.size(), SwappingItem.HORIZONTAL_ITEM_COUNT);
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

        void onBindView(SwappingItem item) {
            if (bookmarkList.contains(item.itemId)) {
                bookmarkBtn.setCardBackgroundColor(context.getResources().getColor(R.color.pink_light));
                bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_24);
            }
            name.setText(item.name);
            bookmarkBtn.setOnClickListener(view -> {
                if (bookmarkList.contains(item.itemId)) {
                    bookmarkBtn.setCardBackgroundColor(context.getResources().getColor(R.color.pink_light));
                    bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_24);
                    reference.child(bookmarkList.indexOf(item.itemId)+"").setValue(bookmarkList.get(bookmarkList.size()-1));
                    bookmarkList.remove(item.itemId);
                    reference.child(bookmarkList.size()+"").setValue(null);
                } else {
                    bookmarkBtn.setCardBackgroundColor(context.getResources().getColor(R.color.indigo_dark));
                    bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_border_24);
                    reference.child(bookmarkList.size()+"").setValue(item.itemId);
                    bookmarkList.add(item.itemId);
                }
            });
            FirebaseStorage.getInstance().getReference().child(item.itemId).child("mainImg")
                    .getDownloadUrl().addOnSuccessListener(uri -> {
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
