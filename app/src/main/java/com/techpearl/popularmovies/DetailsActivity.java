package com.techpearl.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.model.Review;
import com.techpearl.popularmovies.model.Video;
import com.techpearl.popularmovies.utils.ApiUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private Movie mMovie;
    private TextView mUserRatingTextView;
    private TextView mReleaseDateTextView;
    private TextView mOverviewTextView;
    private ImageView mPosterImageView;
    private ProgressBar mRatingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent startingIntent = getIntent();
        if(!startingIntent.hasExtra(getString(R.string.intent_extra_movie))){
            Toast.makeText(this, R.string.missing_movie_data, Toast.LENGTH_SHORT).show();
            finish();
        }
        mMovie = startingIntent.getParcelableExtra(getString(R.string.intent_extra_movie));
        mUserRatingTextView = (TextView) findViewById(R.id.userRatingTextView);
        mReleaseDateTextView = (TextView) findViewById(R.id.releaseDateTextView);
        mOverviewTextView = (TextView) findViewById(R.id.plotSynopsisTextView);
        mPosterImageView = (ImageView) findViewById(R.id.imageView);
        mRatingBar = (ProgressBar) findViewById(R.id.progressBar);
        populateUI();

    }

    private void populateUI() {
        if(mMovie == null)
            return;
        setTitle(mMovie.getTitle());
        Picasso.with(this).load(mMovie.getFullPosterPath(this)).into(mPosterImageView);
        int userRating = mMovie.getVoteAverage().intValue() * 10;
        mRatingBar.setProgress(userRating);
        mUserRatingTextView.setText(userRating + "%");
        mReleaseDateTextView.setText(mMovie.getReleaseDate());
        mOverviewTextView.setText(mMovie.getOverview());
        //loadTrailers();
       // loadReviews();
    }

    private void loadTrailers(){
        ApiUtils.initLoadMovieTrailers(mMovie.getId(), new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                Log.d(TAG, "movie id:" + mMovie.getId() + response.body().toString());
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                Log.e(TAG, getString(R.string.retrofit_error) +mMovie.getId() + t.getMessage());
            }
        });
    }

    private void loadReviews(){
        ApiUtils.initLoadMovieReviews(mMovie.getId(), new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                Log.d(TAG, response.body().toString());
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Log.e(TAG, getString(R.string.retrofit_error) + t.getMessage());
            }
        });
    }
}
