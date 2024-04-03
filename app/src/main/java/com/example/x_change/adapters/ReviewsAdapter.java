package com.example.x_change.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.x_change.R;
import com.example.x_change.utility.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    ArrayList<Review> list;
    int itemCount;
    Context context;

    public ReviewsAdapter(ArrayList<Review> list, int itemCount, Context context) {
        this.list = list;
        this.itemCount = itemCount;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(list.get(position), context);
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView[] stars = new ImageView[5];
        TextView name, content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.reviewName);
            content = itemView.findViewById(R.id.reviewContent);
            stars[0] = itemView.findViewById(R.id.reviewStar1);
            stars[1] = itemView.findViewById(R.id.reviewStar2);
            stars[2] = itemView.findViewById(R.id.reviewStar3);
            stars[3] = itemView.findViewById(R.id.reviewStar4);
            stars[4] = itemView.findViewById(R.id.reviewStar5);
        }

        void onBind(Review review, Context context) {
            content.setText(review.content);
            for (int i=0; i<review.stars; i++) {
                stars[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_star_24));
                stars[i].setColorFilter(ContextCompat.getColor(context, R.color.pink), PorterDuff.Mode.SRC_IN);
            }

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("people").child(review.creatorId).child("profileName");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    name.setText(snapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}
