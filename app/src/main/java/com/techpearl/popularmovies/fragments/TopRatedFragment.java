package com.techpearl.popularmovies.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techpearl.popularmovies.DetailsActivity;
import com.techpearl.popularmovies.R;
import com.techpearl.popularmovies.adapters.MoviesAdapter;
import com.techpearl.popularmovies.api.MoviesDbClient;
import com.techpearl.popularmovies.api.ServiceGenerator;
import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.model.MovieList;
import com.techpearl.popularmovies.utils.ApiUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment to display top rated movies
 */
public class TopRatedFragment extends Fragment implements MoviesAdapter.MovieClickListener {
    private static final String TAG = TopRatedFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    private View mErrorView;
    private TextView mErrorTextView;


    public TopRatedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_top_rated, container, false);
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
        callApi();
    }

    private void callApi() {
        if(!ApiUtils.isConnected(getContext())){
            showErrorMessage(getString(R.string.error_message_no_network));
            return;
        }
        MoviesDbClient moviesDbClient = ServiceGenerator.createService(MoviesDbClient.class);
        Call<MovieList> call = moviesDbClient.topRatedMovies(ServiceGenerator.API_KEY);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(@NonNull Call<MovieList> call, @NonNull Response<MovieList> response) {
                Log.d(TAG, response.body().toString());
                showResponse(response.body().getResults());
            }

            @Override
            public void onFailure(@NonNull Call<MovieList> call, @NonNull Throwable t) {
                Log.e(TAG, getString(R.string.retrofit_error) + t.getMessage());
                showErrorMessage(getString(R.string.error_message));
            }
        });
    }

    private void showResponse(List<Movie> body) {
        mErrorView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.setMovies(body);
    }

    private void showErrorMessage(String message) {
        mErrorTextView.setText(message);
        mErrorView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }
    public void refresh(View view) {
        callApi();
    }
    @Override
    public void onMovieClicked(Movie movie) {
        Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
        detailsIntent.putExtra(getString(R.string.intent_extra_movie), movie.getId());
        startActivity(detailsIntent);
    }
}
