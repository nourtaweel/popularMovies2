package com.techpearl.popularmovies.fragments;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.techpearl.popularmovies.R;
import com.techpearl.popularmovies.adapters.MoviesAdapter;
import com.techpearl.popularmovies.api.MoviesDbClient;
import com.techpearl.popularmovies.api.ServiceGenerator;
import com.techpearl.popularmovies.model.MovieList;
import com.techpearl.popularmovies.utils.ApiUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment to display top rated movies
 */
public class TopRatedFragment extends BaseMoviesFragment {
    private static final String TAG = TopRatedFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    private View mErrorView;
    private TextView mErrorTextView;


    public TopRatedFragment() {
        // Required empty public constructor
    }

    @Override
    void loadMovieList() {
        if(!ApiUtils.isConnected(getContext())){
            showErrorMessage(getString(R.string.error_message_no_network));
            return;
        }
        MoviesDbClient moviesDbClient = ServiceGenerator.createService(MoviesDbClient.class);
        Call<MovieList> call = moviesDbClient.topRatedMovies(ServiceGenerator.API_KEY);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(@NonNull Call<MovieList> call, @NonNull Response<MovieList> response) {
                showResponse(response.body().getResults());
            }

            @Override
            public void onFailure(@NonNull Call<MovieList> call, @NonNull Throwable t) {
                Log.e(TAG, getString(R.string.retrofit_error) + t.getMessage());
                showErrorMessage(getString(R.string.error_message));
            }
        });
    }
}
