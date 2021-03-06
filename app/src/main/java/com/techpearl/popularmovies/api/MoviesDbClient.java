package com.techpearl.popularmovies.api;


import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.model.MovieList;
import com.techpearl.popularmovies.model.ReviewsList;
import com.techpearl.popularmovies.model.VideosList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Nour on 2/19/2018.
 */

public interface MoviesDbClient {
    String API_MOVIE_PATH = "movie";
    String API_POPULAR_PATH = "popular";
    String API_TOP_RATED_PATH = "top_rated";
    String API_VIDEO_PATH = "videos";
    String API_REVIEWS_PATH = "reviews";
    String API_APPEND_TO = "append_to_response";
    String API_KEY_PARAM = "api_key";
    @GET(API_MOVIE_PATH + "/" + API_TOP_RATED_PATH)
    Call<MovieList> topRatedMovies(@Query(API_KEY_PARAM) String apiKey);

    @GET(API_MOVIE_PATH + "/" + API_POPULAR_PATH)
    Call<MovieList> popularMovies(@Query(API_KEY_PARAM) String apiKey);

    @GET(API_MOVIE_PATH + "/{id}/" + API_VIDEO_PATH)
    Call<VideosList> movieTrailers(@Path("id") int movieId, @Query(API_KEY_PARAM) String apiKey);

    @GET(API_MOVIE_PATH + "/{id}/" + API_REVIEWS_PATH)
    Call<ReviewsList> movieReviews(@Path("id") int movieId, @Query(API_KEY_PARAM) String apiKey);

    @GET(API_MOVIE_PATH + "/{id}")
    Call<Movie> movieWithTrailersAndReviews(@Path("id") int movieId, @Query(API_KEY_PARAM) String apiKey,
                                    @Query(API_APPEND_TO) String append);
}
