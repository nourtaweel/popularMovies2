package com.techpearl.popularmovies.utils;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.techpearl.popularmovies.data.MoviesContract;
import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.model.Review;
import com.techpearl.popularmovies.model.ReviewsList;
import com.techpearl.popularmovies.model.Video;
import com.techpearl.popularmovies.model.VideosList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nour on 3/1/2018.
 */

public class DataUtils {

    public static void saveFavoriteMovie(Movie movie, Context context) throws RemoteException, OperationApplicationException {
        //save movie, trailers, reviews in batch mode
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        //insert movie
        operations.add(movieInsertOperation(movie));
        //insert trailers
        operations.addAll(trailersBulkInsertOperation(movie.getVideos().getResults(), movie.getId()));
        //insert reviews
        operations.addAll(reviewsBulkInsertOperation(movie.getReviews().getResults(), movie.getId()));
        //apply batch
        context.getContentResolver().applyBatch(MoviesContract.AUTHORITY, operations);
    }

    public static void deleteFavorite(Movie movie, Context context) throws RemoteException, OperationApplicationException {
        //delete movie from favorites based on api_id in batch mode
        String movieId = String.valueOf(movie.getId());
        int trailersCount = movie.getVideos().getResults().size();
        int reviewsCount = movie.getReviews().getResults().size();
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        //delete trailers
        operations.add(trailersBulkDeleteOperation(movieId, trailersCount));
        //delete reviews
        operations.add(reviewsBulkDeleteOperation(movieId, reviewsCount));
        //delete movie
        operations.add(movieDeleteOperation(movieId));
        context.getContentResolver().applyBatch(MoviesContract.AUTHORITY, operations);
    }

    public static List<Movie> getFavorites(Context context){
        //get all favorite movies. Movies in the returned List only contains id & poster
        Cursor allMovies = context.getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                new String [] {MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID,
                        MoviesContract.MovieEntry.COLUMN_POSTER,
                        MoviesContract.MovieEntry.COLUMN_TITLE},
                null,
                null,
                null);
        if (allMovies == null)
            return null;
        List<Movie> movies = new ArrayList<>();
        for (allMovies.moveToFirst(); !allMovies.isAfterLast(); allMovies.moveToNext()) {
            Movie curr = new Movie();
            curr.setId(allMovies.getInt(allMovies.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID)));
            curr.setTitle(allMovies.getString(allMovies.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE)));
            curr.setPosterPath(allMovies.getString(allMovies.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER)));
            movies.add(curr);
        }
        allMovies.close();
        return movies;
    }

    public static Movie getFavoriteMovie(String id, Context context){
        //get movie having its api_id
        Cursor cursor = context.getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        if(cursor == null){
            return null;
        }
        //construct the object
        Movie movie = movieFromCursor(cursor);

        //query trailers for this movie
        Uri trailersOfMovieUri =  MoviesContract.MovieEntry.CONTENT_URI
                .buildUpon()
                .appendPath(id)
                .appendPath(MoviesContract.PATH_TRAILERS)
                .build();
        cursor = context.getContentResolver().query(trailersOfMovieUri,
                null,
                null,
                null,
                null);
        //add trailers to Movie object
        movie.setVideos(videoListFromCursor(cursor));

        //query reviews for this movie
        Uri reviewsOfMovieUri =  MoviesContract.MovieEntry.CONTENT_URI
                .buildUpon()
                .appendPath(id)
                .appendPath(MoviesContract.PATH_REVIEWS)
                .build();
        cursor = context.getContentResolver().query(reviewsOfMovieUri,
                null,
                null,
                null,
                null);
        //add reviews to Movie object
        movie.setReviews(reviewListFromCursor(cursor));
        if(cursor != null){
            cursor.close();
        }
        return movie;
    }

    private static ReviewsList reviewListFromCursor(Cursor cursor) {
        if(cursor == null)
            return null;
        ReviewsList reviewsList = new ReviewsList();
        List<Review> list = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Review review = new Review();
            review.setAuthor(cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_AUTHOR)));
            review.setContent(cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_CONTENT)));
            list.add(review);
        }
        reviewsList.setResults(list);
        return reviewsList;
    }

    private static VideosList videoListFromCursor(Cursor cursor) {
        if(cursor == null)
            return null;
        VideosList videosList = new VideosList();
        List<Video> list = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Video v = new Video();
            v.setName(cursor.getString(cursor.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_NAME)));
            v.setKey(cursor.getString(cursor.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_KEY)));
            v.setSite(cursor.getString(cursor.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_SITE)));
            list.add(v);
        }
        videosList.setResults(list);
        return videosList;
    }

    private static Movie movieFromCursor(Cursor cursor) {
        if(cursor == null)
            return null;
        cursor.moveToFirst();
        Movie movie = new Movie();
        movie.setId(cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID)));
        movie.setTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE)));
        movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_USER_RATING)));
        movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER)));
        movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)));
        movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_BACKDROP)));
        movie.setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW)));
        movie.setRuntime(cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RUNTIME)));
        return movie;
    }

    public static boolean isFavorite(Integer id, Context context){
        //check if this id in favorites
        Uri movieWithIdUri = MoviesContract.MovieEntry.CONTENT_URI
                .buildUpon()
                .appendPath(String.valueOf(id))
                .build();
        Log.d("DataUtils", "uri: " + movieWithIdUri.toString());
        Cursor movieCursor = context.getContentResolver().query(movieWithIdUri,
                null,
                null,
                null,
                null);
        Log.d("DataUtils", "count: " + movieCursor.getCount());
        boolean isFavorite = (movieCursor != null && movieCursor.getCount() > 0);
        movieCursor.close();
        return isFavorite;
    }

    private static List<ContentProviderOperation> reviewsBulkInsertOperation(List<Review> reviews,
                                                                             Integer movieId) {
        List<ContentProviderOperation> ops = new ArrayList<>();
        for(Review review : reviews){
            ContentValues values = new ContentValues();
            values.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
            values.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
            values.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID, String.valueOf(movieId));
            ContentProviderOperation insertOp = ContentProviderOperation
                    .newInsert(MoviesContract.ReviewEntry.CONTENT_URI)
                    .withValues(values).build();
            ops.add(insertOp);
        }
        return ops;
    }

    private static List<ContentProviderOperation> trailersBulkInsertOperation(List<Video> videos,
                                                                              Integer movieId) {
        List<ContentProviderOperation> ops = new ArrayList<>();
        for(Video video : videos){
            ContentValues values = new ContentValues();
            values.put(MoviesContract.TrailerEntry.COLUMN_NAME, video.getName());
            values.put(MoviesContract.TrailerEntry.COLUMN_SITE, video.getSite());
            values.put(MoviesContract.TrailerEntry.COLUMN_KEY, video.getKey());
            values.put(MoviesContract.TrailerEntry.COLUMN_MOVIE_ID, String.valueOf(movieId));
            ContentProviderOperation insertOp = ContentProviderOperation
                    .newInsert(MoviesContract.TrailerEntry.CONTENT_URI)
                    .withValues(values).build();
            ops.add(insertOp);
        }
        return ops;
    }

    private static ContentProviderOperation movieInsertOperation(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID, String.valueOf(movie.getId()));
        values.put(MoviesContract.MovieEntry.COLUMN_USER_RATING, movie.getVoteAverage());
        values.put(MoviesContract.MovieEntry.COLUMN_BACKDROP, movie.getBackdropPath());
        values.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        values.put(MoviesContract.MovieEntry.COLUMN_POSTER, movie.getPosterPath());
        values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(MoviesContract.MovieEntry.COLUMN_RUNTIME, movie.getRuntime());
        return ContentProviderOperation
                .newInsert(MoviesContract.MovieEntry.CONTENT_URI)
                .withValues(values)
                .build();
    }

    private static ContentProviderOperation movieDeleteOperation(String id) {
        Uri deleteMovieUri = MoviesContract.MovieEntry.CONTENT_URI
                .buildUpon()
                .appendPath(id)
                .build();
        return ContentProviderOperation
                .newDelete(deleteMovieUri)
                //.withExpectedCount(1)
                .build();
    }

    private static ContentProviderOperation reviewsBulkDeleteOperation(String id, int count) {
        Uri deleteReviewsForMovieUri = MoviesContract.MovieEntry.CONTENT_URI
                .buildUpon()
                .appendPath(id)
                .appendPath(MoviesContract.PATH_REVIEWS)
                .build();
        return ContentProviderOperation
                .newDelete(deleteReviewsForMovieUri)
               // .withExpectedCount(count)
                .build();
    }

    private static ContentProviderOperation trailersBulkDeleteOperation(String id, int count) {
        Uri deleteTrailersForMovieUri = MoviesContract.MovieEntry.CONTENT_URI
                .buildUpon()
                .appendPath(id)
                .appendPath(MoviesContract.PATH_TRAILERS)
                .build();
        return ContentProviderOperation
                .newDelete(deleteTrailersForMovieUri)
               // .withExpectedCount(count)
                .build();
    }
}
