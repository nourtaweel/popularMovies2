package com.techpearl.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.utils.DataUtils;

import java.util.List;

/**
 * A fragment to show user's favorite movies
 */
public class FavoriteFragment extends BaseMoviesFragment implements
        LoaderManager.LoaderCallbacks<List<Movie>>{
    private static final String TAG = FavoriteFragment.class.getSimpleName();
    private static final int FAVORITES_LOADER_ID = 1;

    public FavoriteFragment() {
    }

    @Override
    void loadMovieList() {
//        List<Movie> faves = DataUtils.getFavorites(getContext());
//        showResponse(faves);
        getLoaderManager().initLoader(FAVORITES_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskLoader<List<Movie>>(getContext()) {
            List<Movie> mFavorites;
            @Override
            public List<Movie> loadInBackground() {
                return DataUtils.getFavorites(getContext());
            }

            @Override
            public void deliverResult(@Nullable List<Movie> data) {
                mFavorites = data;
                super.deliverResult(data);

            }

            @Override
            protected void onStartLoading() {
                if(mFavorites != null){
                    deliverResult(mFavorites);
                }else {
                    forceLoad();
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        showResponse(movies);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }

}
