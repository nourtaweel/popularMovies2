package com.techpearl.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nour on 2/28/2018.
 */

public class MoviesDbOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 2;

    public MoviesDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME +
                " ( " + MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_API_ID + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_POSTER + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_BACKDROP + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_RUNTIME + " TEXT )";

        final String CREATE_TRAILERS_TABLE = "CREATE TABLE " + MoviesContract.TrailerEntry.TABLE_NAME +
                " ( " + MoviesContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MoviesContract.TrailerEntry.COLUMN_NAME + " TEXT, " +
                MoviesContract.TrailerEntry.COLUMN_SITE + " TEXT NOT NULL, " +
                MoviesContract.TrailerEntry.COLUMN_KEY + " TEXT NOT NULL )";
        final String CREATE_REVIEWS_TABLE = "CREATE TABLE " + MoviesContract.ReviewEntry.TABLE_NAME +
                " ( " + MoviesContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.ReviewEntry.COLUMN_AUTHOR + " TEXT, " +
                MoviesContract.ReviewEntry.COLUMN_CONTENT + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(CREATE_REVIEWS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int l) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
