package com.techpearl.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.techpearl.popularmovies.loaders.FavoriteMoviesLoader;
import com.techpearl.popularmovies.model.Movie;

import java.util.List;

/**
 * A fragment to show user's favorite movies
 * uses an AsyncTaskLoader to load favorite Movies from ContentProvider
 */
public class FavoriteFragment extends BaseMoviesFragment implements
        LoaderManager.LoaderCallbacks<List<Movie>>{
    private static final String TAG = FavoriteFragment.class.getSimpleName();
    private static final int FAVORITES_LOADER_ID = 1;

    public FavoriteFragment() {
    }

    @Override
    void loadMovieList() {
        getLoaderManager().initLoader(FAVORITES_LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        return new FavoriteMoviesLoader(getContext());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> movies) {
        showResponse(movies);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
    }

}
