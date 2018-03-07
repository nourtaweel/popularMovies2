package com.techpearl.popularmovies.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.techpearl.popularmovies.api.MoviesDbClient;
import com.techpearl.popularmovies.api.ServiceGenerator;
import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.utils.NetworkUtils;
import com.techpearl.popularmovies.utils.DataUtils;

import java.io.IOException;

import retrofit2.Call;
/**
 * Created by Nour on 3/5/2018.
 * a loader that decides whether to fetch the Movie Object from the api directly (if there is
 * a connection)
 * or fetch it from content provider if there was no connection and the movie was one of the user's
 * favorites
 */
public class MovieLoader extends AsyncTaskLoader<Movie> {
    private static final String TAG = MovieLoader.class.getSimpleName();
    private int mMovieId;
    private Movie loadedMovie;
    public MovieLoader(@NonNull Context context, int movieId) {
        super(context);
        mMovieId = movieId;
    }

    @Override
    protected void onStartLoading() {
        if(loadedMovie != null){
            deliverResult(loadedMovie);
        }else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(@Nullable Movie data) {
        super.deliverResult(data);
        loadedMovie = data;
    }

    @Nullable
    @Override
    public Movie loadInBackground() {
        if(mMovieId == -1){
            return null;
        }
        boolean isFavorite = DataUtils.isFavorite(mMovieId, getContext());
        if(NetworkUtils.isConnected(getContext())){
            //fetch from online
            try{
                return fetchFromApi(mMovieId);
            }catch (IOException ioe){
                Log.e(TAG, "error fetching movie");
            }
        }else {
            //if favorite movie fetch data from ContentProvider, if not display an error message
            if(isFavorite){
                return DataUtils.getFavoriteMovie(String.valueOf(mMovieId), getContext());
            }
        }
        return null;
    }
    private Movie fetchFromApi(int movieId) throws IOException {
        MoviesDbClient moviesDbClient = ServiceGenerator.createService(MoviesDbClient.class);
        Call<Movie> call = moviesDbClient.movieWithTrailersAndReviews(movieId,
                ServiceGenerator.API_KEY,
                "videos,reviews");
        return call.execute().body();
    }
}