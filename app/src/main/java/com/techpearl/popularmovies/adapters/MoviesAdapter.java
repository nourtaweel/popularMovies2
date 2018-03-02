package com.techpearl.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.techpearl.popularmovies.R;
import com.techpearl.popularmovies.model.Movie;

import java.util.List;

/**
 * Created by Nour on 2/20/2018.
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
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
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
        private ImageView posterImageView;
        private Context context;
        public MovieViewHolder(View itemView) {
            super(itemView);
            posterImageView = (ImageView) itemView.findViewById(R.id.posterImageView);
            context = itemView.getContext();
            itemView.setOnClickListener(this);
        }
        private void bind(int listIndex){
            String fullPosterPath = mMovies.get(listIndex).getFullPosterPath(context);
            Picasso.with(context).load(fullPosterPath).into(posterImageView);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mListener.onMovieClicked(mMovies.get(position));

        }
    }
    public interface MovieClickListener {
        public void onMovieClicked(Movie movie);
    }
}
