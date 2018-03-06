package com.techpearl.popularmovies.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.utils.DataUtils;

/**
 * Created by Nour on 3/5/2018.
 */

public class ToggleFavoriteStatusLoader extends AsyncTaskLoader<Boolean> {
    private static final String TAG = ToggleFavoriteStatusLoader.class.getSimpleName();
    private Movie mMovie;

    public ToggleFavoriteStatusLoader(@NonNull Context context, Movie movie) {
        super(context);
        this.mMovie = movie;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Boolean loadInBackground() {
        boolean isFav = DataUtils.isFavorite(mMovie.getId(), getContext());
        if(!isFav){
            try{
                DataUtils.saveFavoriteMovie(mMovie, getContext());
            }catch (Exception e){
                Log.e(TAG, e.getLocalizedMessage());
            }
        }else{
            try{
                DataUtils.deleteFavorite(mMovie, getContext());
            }catch (Exception e){
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
        isFav = DataUtils.isFavorite(mMovie.getId(), getContext());
        return isFav;
    }
}
