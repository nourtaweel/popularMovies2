package com.techpearl.popularmovies.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techpearl.popularmovies.DetailsActivity;
import com.techpearl.popularmovies.R;
import com.techpearl.popularmovies.adapters.MoviesAdapter;
import com.techpearl.popularmovies.model.Movie;

import java.util.List;

/**
 * Created by Nour on 3/2/2018.
 */

abstract class BaseMoviesFragment extends Fragment implements MoviesAdapter.MovieClickListener {
    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    private View mErrorView;
    private TextView mErrorTextView;

    public BaseMoviesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mErrorView = getView().findViewById(R.id.errorView);
        mErrorTextView = (TextView)getView().findViewById(R.id.errorTextView);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.moviesRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 3));
        mAdapter = new MoviesAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);
        loadMovieList();
    }

    //any fragment must override this method to load List<Movie>
    abstract void loadMovieList();

    protected void showResponse(List<Movie> body) {
        mErrorView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.setMovies(body);
    }

    protected void showErrorMessage(String message) {
        mErrorTextView.setText(message);
        mErrorView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }
    public void refresh(View view) {
        loadMovieList();
    }
    @Override
    public void onMovieClicked(Movie movie) {
        Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
        detailsIntent.putExtra(getString(R.string.intent_extra_movie), movie.getId());
        startActivity(detailsIntent);
    }
}
