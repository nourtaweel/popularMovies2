package com.techpearl.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Nour on 2/28/2018.
 */

public class MoviesContentProvider extends ContentProvider {
    private SQLiteOpenHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildMatcher();

    public static final int MOVIES = 100;
    public static final int TRAILERS = 200;
    public static final int REVIEWS = 300;
    public static final int MOVIES_WITH_ID = 101;
    public static final int TRAILERS_FOR_MOVIE = 201;
    public static final int REVIEWS_FOR_MOVIE = 301;

    private static UriMatcher buildMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);
        matcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#/" +
                MoviesContract.PATH_TRAILERS, TRAILERS_FOR_MOVIE);
        matcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#/" +
                MoviesContract.PATH_REVIEWS, REVIEWS_FOR_MOVIE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mOpenHelper = new MoviesDbOpenHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String order) {
        int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor = null;
        int id = -1;
        switch (match){
            case MOVIES:
                retCursor = db.query(MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        order);
                break;
            case MOVIES_WITH_ID:
                retCursor = db.query(MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID  + "=?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        order);
                break;
            case TRAILERS_FOR_MOVIE:
                retCursor = db.query(MoviesContract.TrailerEntry.TABLE_NAME,
                        projection,
                        MoviesContract.TrailerEntry.COLUMN_MOVIE_ID  + "=?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        order);
                break;
            case REVIEWS_FOR_MOVIE:
                retCursor = db.query(MoviesContract.ReviewEntry.TABLE_NAME,
                        projection,
                        MoviesContract.ReviewEntry.COLUMN_MOVIE_ID  + "=?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        order);
                break;
                default:
                    throw new UnsupportedOperationException("Can't query from Uri: " +uri.toString());
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri = null;
        long id;
        switch (match){
            case MOVIES:
                id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, contentValues);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI, id);
                }else{
                    throw new SQLException("failed to insert in Uri " + uri.toString());
                }
                break;
            case TRAILERS:
                id = db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, contentValues);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(MoviesContract.TrailerEntry.CONTENT_URI, id);
                }else{
                    throw new SQLException("failed to insert in Uri " + uri.toString());
                }
                break;
            case REVIEWS:
                id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, contentValues);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(MoviesContract.ReviewEntry.CONTENT_URI, id);
                }else{
                    throw new SQLException("failed to insert in Uri " + uri.toString());
                }
                break;
            default:
                throw new UnsupportedOperationException("Can't insert into Uri: " + uri.toString());
        }
        if(returnUri != null){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int numDeleted = 0;
        String id;
        switch (match){
            case MOVIES_WITH_ID:
                id = uri.getPathSegments().get(1);
                numDeleted = db.delete(MoviesContract.MovieEntry.TABLE_NAME,
                        MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID + "=?", new String[] {id});
                break;
            case TRAILERS_FOR_MOVIE:
                id = uri.getPathSegments().get(1);
                numDeleted = db.delete(MoviesContract.TrailerEntry.TABLE_NAME,
                        MoviesContract.TrailerEntry.COLUMN_MOVIE_ID + "=?", new String[] {id});
                break;
            case REVIEWS_FOR_MOVIE:
                id = uri.getPathSegments().get(1);
                numDeleted = db.delete(MoviesContract.ReviewEntry.TABLE_NAME,
                        MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + "=?", new String[] {id});
                break;
            default:
                throw new UnsupportedOperationException("Can't delete from Uri: " + uri.toString());
        }
        if(numDeleted > 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return numDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numOfRows = 0;
        switch (match){
            case TRAILERS:
                db.beginTransaction();
                try{
                    for(ContentValues value : values){
                        long _id = db.insert(MoviesContract.TrailerEntry.TABLE_NAME,
                                null,
                                value);
                        if(_id != -1){
                            numOfRows++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                if(numOfRows > 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                break;
            case REVIEWS:
                db.beginTransaction();
                try{
                    for(ContentValues value : values){
                        long _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME,
                                null,
                                value);
                        if(_id != -1){
                            numOfRows++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                if(numOfRows > 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        return numOfRows;
    }
}
