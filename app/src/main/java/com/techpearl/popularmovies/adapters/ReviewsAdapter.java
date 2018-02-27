package com.techpearl.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techpearl.popularmovies.R;
import com.techpearl.popularmovies.model.Review;

import java.util.List;

/**
 * Created by Nour on 2/27/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder>{
    private List<Review> reviews;

    public ReviewsAdapter(List<Review> reviews){
        this.reviews = reviews;
    }

    public void setReviews(List<Review> reviews){
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewItem = inflater.inflate(R.layout.review_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(viewItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        if(reviews == null){
            return 0;
        }
        return reviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView userNameTextView;
        private TextView reviewTextView;
        private Context context;
        public ViewHolder(View itemView) {
            super(itemView);
            userNameTextView = (TextView) itemView.findViewById(R.id.userNameTextView);
            reviewTextView = (TextView) itemView.findViewById(R.id.reviewTextView);
            context = itemView.getContext();
        }
        private void bind(Review review){
            userNameTextView.setText(review.getAuthor());
            reviewTextView.setText(review.getContent());
        }
    }
}
