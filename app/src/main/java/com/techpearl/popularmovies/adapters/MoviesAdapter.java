package com.techpearl.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.techpearl.popularmovies.R;
import com.techpearl.popularmovies.model.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nour on 2/20/2018.
 * RecyclerView Adapter used to view one Movie in MainActivity
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private static final String TAG = MoviesAdapter.class.getSimpleName();
    private MovieClickListener mListener;
    private List<Movie> mMovies;
    public MoviesAdapter(List<Movie> movies, MovieClickListener listener){
        mMovies = movies;
        mListener = listener;
    }

    public void setMovies(List<Movie> movies){
        mMovies = movies;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mMovies == null){
            return 0;
        }
        return mMovies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.posterImageView) ImageView posterImageView;
        @BindView(R.id.titleTextView) TextView titleTextView;
        private Context context;
        public MovieViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }
        private void bind(int listIndex){
            titleTextView.setText(mMovies.get(listIndex).getTitle());
            String fullPosterPath = mMovies.get(listIndex).getFullPosterPath(context);
            Picasso.with(context)
                    .load(fullPosterPath)
                    .placeholder(R.color.placehloderColor)
                    .error(R.color.placehloderColor)
                    .into(posterImageView);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mListener.onMovieClicked(mMovies.get(position));

        }
    }
    public interface MovieClickListener {
        void onMovieClicked(Movie movie);
    }
}
