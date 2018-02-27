package com.techpearl.popularmovies;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.techpearl.popularmovies.adapters.ReviewsAdapter;
import com.techpearl.popularmovies.adapters.TrailersAdapter;
import com.techpearl.popularmovies.api.MoviesDbClient;
import com.techpearl.popularmovies.api.ServiceGenerator;
import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.utils.YoutubeUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements TrailersAdapter.TrailerClickListener {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private Movie mMovie;
    private TextView mUserRatingTextView;
    private TextView mReleaseDateTextView;
    private TextView mOverviewTextView;
    private ImageView mPosterImageView;
    private ProgressBar mRatingBar;
    private RecyclerView mTrailersRecyclerView;
    private RecyclerView mReviewsRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent startingIntent = getIntent();
        if(!startingIntent.hasExtra(getString(R.string.intent_extra_movie))){
            Toast.makeText(this, R.string.missing_movie_data, Toast.LENGTH_SHORT).show();
            finish();
        }
        int movieId = startingIntent.getIntExtra(getString(R.string.intent_extra_movie), -1);
        fetchMovie(movieId);
        mUserRatingTextView = (TextView) findViewById(R.id.userRatingTextView);
        mReleaseDateTextView = (TextView) findViewById(R.id.releaseDateTextView);
        mOverviewTextView = (TextView) findViewById(R.id.plotSynopsisTextView);
        mPosterImageView = (ImageView) findViewById(R.id.imageView);
        mRatingBar = (ProgressBar) findViewById(R.id.progressBar);
        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.trailersRecyclerView);
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.reviewsRecyclerView);
    }

    private void fetchMovie(int movieId) {
        if(movieId == -1){
            return;
        }
        MoviesDbClient moviesDbClient = ServiceGenerator.createService(MoviesDbClient.class);
        Call<Movie> call = moviesDbClient.movieWithTrailersAndReviews(movieId,
                ServiceGenerator.API_KEY,
                "videos,reviews");
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                Log.d(TAG, response.body().toString());
                mMovie = response.body();
                populateUI();
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                Log.e(TAG, getString(R.string.retrofit_error) + t.getMessage());
            }
        });
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
        //trailers
        TrailersAdapter trailersAdapter = new TrailersAdapter(mMovie.getVideos().getResults(), this);
        RecyclerView.LayoutManager horizontalLayoutManager = new LinearLayoutManager(
                getApplicationContext(),
                RecyclerView.HORIZONTAL,
                false);
        mTrailersRecyclerView.setLayoutManager(horizontalLayoutManager);
        mTrailersRecyclerView.setHasFixedSize(true);
        mTrailersRecyclerView.setAdapter(trailersAdapter);
        //reviews
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(mMovie.getReviews().getResults());
        RecyclerView.LayoutManager verticalLayoutManager = new LinearLayoutManager(
                getApplicationContext(),
                RecyclerView.VERTICAL,
                false);
        mReviewsRecyclerView.setHasFixedSize(true);
        mReviewsRecyclerView.setLayoutManager(verticalLayoutManager);
        mReviewsRecyclerView.setAdapter(reviewsAdapter);
        mReviewsRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onTrailerClicked(String trailerKey) {
        YoutubeUtils.launchYoutube(this, trailerKey);
    }
}
