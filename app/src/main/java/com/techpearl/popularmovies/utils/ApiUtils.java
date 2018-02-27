package com.techpearl.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.techpearl.popularmovies.R;
import com.techpearl.popularmovies.api.MoviesDbClient;
import com.techpearl.popularmovies.api.ServiceGenerator;
import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.model.MovieList;
import com.techpearl.popularmovies.model.Review;
import com.techpearl.popularmovies.model.ReviewsList;
import com.techpearl.popularmovies.model.Video;
import com.techpearl.popularmovies.model.VideosList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nour on 2/26/2018.
 */

public class ApiUtils {
    private final static int SORT_ORDER_POPULAR = 0;
    private final static int SORT_ORDER_TOP_RATED = 1;
    
    public static void initLoadMovies(int sortOrder, Callback<MovieList> callback){
        //TODO: check if connected
        MoviesDbClient moviesDbClient = ServiceGenerator.createService(MoviesDbClient.class);
        Call<MovieList> call;
        if(sortOrder == SORT_ORDER_POPULAR){
            call = moviesDbClient.popularMovies(ServiceGenerator.API_KEY);
        }else {
            call = moviesDbClient.topRatedMovies(ServiceGenerator.API_KEY);
        }
        call.enqueue(callback);
    } 
    
    public static void initLoadMovieTrailers(int movieId, Callback<VideosList> callback){
        //TODO: check if connected
        MoviesDbClient moviesDbClient = ServiceGenerator.createService(MoviesDbClient.class);
        Call<VideosList> call = moviesDbClient.movieTrailers(movieId, ServiceGenerator.API_KEY);
        call.enqueue(callback);
        
    }
    
    public static void initLoadMovieReviews(int movieId, Callback<ReviewsList> callback){
        //TODO: check if connected
        MoviesDbClient moviesDbClient = ServiceGenerator.createService(MoviesDbClient.class);
        Call<ReviewsList> call = moviesDbClient.movieReviews(movieId, ServiceGenerator.API_KEY);
        call.enqueue(callback);
    }
    
    public static boolean isConnected(Context context){
        /* Based on code snippet in
         * https://developer.android.com/training/basics/network-ops/managing.html */
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
        
    }
}
