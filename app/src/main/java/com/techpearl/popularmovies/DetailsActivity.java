package com.techpearl.popularmovies;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

public class DetailsActivity extends AppCompatActivity implements TrailersAdapter.TrailerClickListener, View.OnClickListener {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private Movie mMovie;
    private TextView mTitleTextView;
    private TextView mRuntimeTextView;
    private TextView mUserRatingTextView;
    private TextView mReleaseDateTextView;
    private TextView mOverviewTextView;
    private ImageView mBackdropImageView;
    private ImageView mPosterImageView;
    private ProgressBar mRatingBar;
    private RecyclerView mTrailersRecyclerView;
    private RecyclerView mReviewsRecyclerView;
    private ImageButton mFavoriteButton;


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
        mTitleTextView = (TextView) findViewById(R.id.titleTextView);
        mRuntimeTextView = (TextView) findViewById(R.id.runtimeTextView);
        mReleaseDateTextView = (TextView) findViewById(R.id.releaseDateTextView);
        mOverviewTextView = (TextView) findViewById(R.id.plotSynopsisTextView);
        mPosterImageView = (ImageView) findViewById(R.id.imageView);
        mBackdropImageView = (ImageView) findViewById(R.id.backdropImageView);
        mRatingBar = (ProgressBar) findViewById(R.id.progressBar);
        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.trailersRecyclerView);
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.reviewsRecyclerView);
        mFavoriteButton = (ImageButton) findViewById(R.id.favImageButton);
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
        mTitleTextView.setText(mMovie.getTitle());
        Picasso.with(this).load(mMovie.getFullPosterPath(this)).into(mPosterImageView);
        Picasso.with(this).load(mMovie.getFullBackdropPath(this)).into(mBackdropImageView);
        int userRating = mMovie.getVoteAverage().intValue() * 10;
        mRatingBar.setProgress(userRating);
        mUserRatingTextView.setText(userRating + "%");
        mReleaseDateTextView.setText(mMovie.getReleaseDate().substring(0,4));
        mRuntimeTextView.setText("\u23F2 " +
                getString(R.string.runtime_format,mMovie.getRuntime()/60, mMovie.getRuntime()%60));
        mOverviewTextView.setText(mMovie.getOverview());
        //favorite icon
        mFavoriteButton.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.favImageButton){
            mFavoriteButton.setSelected(!mFavoriteButton.isSelected());
        }
    }
}
