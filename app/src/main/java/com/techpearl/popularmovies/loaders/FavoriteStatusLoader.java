package com.techpearl.popularmovies.loaders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.techpearl.popularmovies.utils.DataUtils;

/**
 * Created by Nour on 3/5/2018.
 * A loader that connects with content provider to check if a movie is in the favorite collection or
 * not based on the id
 */

public class FavoriteStatusLoader extends AsyncTaskLoader<Boolean> {
    private int movieId;
    private Boolean mIsFav;
    public FavoriteStatusLoader(Context context, int movieId) {
        super(context);
        this.movieId = movieId;
    }

    @Override
    protected void onStartLoading() {
        if(mIsFav != null){
            deliverResult(mIsFav);
        }else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(@Nullable Boolean data) {
        super.deliverResult(data);
        mIsFav = data;
    }

    @Override
    public Boolean loadInBackground() {
        return DataUtils.isFavorite(movieId, getContext());
    }
}
