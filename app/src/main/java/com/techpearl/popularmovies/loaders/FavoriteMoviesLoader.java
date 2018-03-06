package com.techpearl.popularmovies.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.utils.DataUtils;

import java.util.List;

/**
 * Created by Nour on 3/6/2018.
 * A Loader that loads all favorite movies from the content provider
 */

public class FavoriteMoviesLoader extends AsyncTaskLoader<List<Movie>> {

    public FavoriteMoviesLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    public List<Movie> loadInBackground() {
        return DataUtils.getFavorites(getContext());
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
