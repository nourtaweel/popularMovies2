package com.techpearl.popularmovies.api;


import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.model.Review;
import com.techpearl.popularmovies.model.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Nour on 2/19/2018.
 */

public interface MoviesDbClient {
    static final String API_MOVIE_PATH = "movie";
    static final String API_POPULAR_PATH = "popular";
    static final String API_TOP_RATED_PATH = "top_rated";
    static final String API_VIDEO_PATH = "videos";
    static final String API_REVIEWS_PATH = "reviews";
    static final String API_APPEND_TO = "append_to_response";
    static final String API_KEY_PARAM = "api_key";
    @GET(API_MOVIE_PATH + "/" + API_TOP_RATED_PATH)
    Call<List<Movie>> topRatedMovies(@Query(API_KEY_PARAM) String apiKey);

    @GET(API_MOVIE_PATH + "/" + API_POPULAR_PATH)
    Call<List<Movie>> popularMovies(@Query(API_KEY_PARAM) String apiKey);

    @GET(API_MOVIE_PATH + "/{id}/" + API_VIDEO_PATH)
    Call<List<Video>> movieTrailers(@Path("id") int movieId, @Query(API_KEY_PARAM) String apiKey);

    @GET(API_MOVIE_PATH + "/{id}/" + API_REVIEWS_PATH)
    Call<List<Review>> movieReviews(@Path("id") int movieId, @Query(API_KEY_PARAM) String apiKey);

    @GET(API_MOVIE_PATH + "/{id}")
    Call<Movie> movieWithTrailersAndReviews(@Path("id") int movieId, @Query(API_KEY_PARAM) String apiKey,
                                    @Query(API_APPEND_TO) String append);
}
