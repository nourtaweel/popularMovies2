package com.techpearl.popularmovies.fragments;


import android.support.annotation.NonNull;
import android.util.Log;

import com.techpearl.popularmovies.R;
import com.techpearl.popularmovies.api.MoviesDbClient;
import com.techpearl.popularmovies.api.ServiceGenerator;
import com.techpearl.popularmovies.model.MovieList;
import com.techpearl.popularmovies.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment to show popular movies
 */
public class PopularFragment extends BaseMoviesFragment {
    private static final String TAG = PopularFragment.class.getSimpleName();

    public PopularFragment() {
    }

    @Override
    void loadMovieList() {
        if(!NetworkUtils.isConnected(getContext())){
            showErrorMessage(getString(R.string.error_message_no_network), true);
            return;
        }
        MoviesDbClient moviesDbClient = ServiceGenerator.createService(MoviesDbClient.class);
        Call<MovieList> call = moviesDbClient.popularMovies(ServiceGenerator.API_KEY);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(@NonNull Call<MovieList> call, @NonNull Response<MovieList> response) {
                showResponse(response.body().getResults());
            }

            @Override
            public void onFailure(@NonNull Call<MovieList> call, @NonNull Throwable t) {
                Log.e(TAG, getString(R.string.retrofit_error) + t.getMessage());
                showErrorMessage(getString(R.string.error_message), false);
            }
        });
    }


}
