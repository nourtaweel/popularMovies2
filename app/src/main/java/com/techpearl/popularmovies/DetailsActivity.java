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
import com.techpearl.popularmovies.utils.ApiUtils;
import com.techpearl.popularmovies.utils.DataUtils;
import com.techpearl.popularmovies.utils.PreferencesUtils;
import com.techpearl.popularmovies.utils.YoutubeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements TrailersAdapter.TrailerClickListener, View.OnClickListener {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private Movie mMovie;
    private boolean mIsFavorite;
    @BindView(R.id.titleTextView) TextView mTitleTextView;
    @BindView(R.id.runtimeTextView) TextView mRuntimeTextView;
    @BindView(R.id.userRatingTextView) TextView mUserRatingTextView;
    @BindView(R.id.releaseDateTextView) TextView mReleaseDateTextView;
    @BindView(R.id.plotSynopsisTextView) TextView mOverviewTextView;
    @BindView(R.id.backdropImageView) ImageView mBackdropImageView;
    @BindView(R.id.imageView) ImageView mPosterImageView;
    @BindView(R.id.progressBar) ProgressBar mRatingBar;
    @BindView(R.id.trailersRecyclerView) RecyclerView mTrailersRecyclerView;
    @BindView(R.id.reviewsRecyclerView) RecyclerView mReviewsRecyclerView;
    @BindView(R.id.favImageButton) ImageButton mFavoriteButton;
    @BindView(R.id.trailersTextView) TextView mTrailersNumTextView;
    @BindView(R.id.reviewsTextView) TextView mReviewsNumTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        Intent startingIntent = getIntent();
        if(!startingIntent.hasExtra(getString(R.string.intent_extra_movie))){
            finishWithToast(getString(R.string.missing_movie_data));
        }
        int movieId = startingIntent.getIntExtra(getString(R.string.intent_extra_movie), -1);
        fetchMovie(movieId);
    }

    private void fetchMovie(int movieId) {
        if(movieId == -1){
            return;
        }
        //TODO move off the main thread
        mIsFavorite = DataUtils.isFavorite(movieId, this);
        if(ApiUtils.isConnected(this)){
            //fetch from online
            fetchFromApi(movieId);
        }else {
            //if favorite movie fetch data from ContentProvider, if not display an error message
            if(mIsFavorite){
                fetchFromContentProvider(movieId);
            }else {
                //error message
                finishWithToast(getString(R.string.message_not_connected));
            }
        }
    }

    private void fetchFromContentProvider(int movieId) {
        mMovie = DataUtils.getFavoriteMovie(String.valueOf(movieId), this);
        populateUI();
    }

    private void fetchFromApi(int movieId) {
        MoviesDbClient moviesDbClient = ServiceGenerator.createService(MoviesDbClient.class);
        Call<Movie> call = moviesDbClient.movieWithTrailersAndReviews(movieId,
                ServiceGenerator.API_KEY,
                "videos,reviews");
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
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
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        mTitleTextView.setText(mMovie.getTitle());
        Picasso.with(this).load(mMovie.getFullPosterPath(this)).into(mPosterImageView);
        Picasso.with(this).load(mMovie.getFullBackdropPath(this)).into(mBackdropImageView);
        Double userRating = (mMovie.getVoteAverage() * 10);
        mRatingBar.setProgress(userRating.intValue());
        mUserRatingTextView.setText(getString(R.string.rating_of_ten_format, mMovie.getVoteAverage()));
        mReleaseDateTextView.setText(mMovie.getReleaseDate().substring(0,4));
        mRuntimeTextView.setText(getString(R.string.runtime_format,
                mMovie.getRuntime()/60,
                mMovie.getRuntime()%60));
        mOverviewTextView.setText(mMovie.getOverview());
        //favorite icon

        mFavoriteButton.setSelected(mIsFavorite);
        mFavoriteButton.setVisibility(View.VISIBLE);
        mFavoriteButton.setOnClickListener(this);
        //trailers
        mTrailersNumTextView.setText(getString(R.string.trailers_num_format,
                mMovie.getVideos().getResults().size()));
        TrailersAdapter trailersAdapter = new TrailersAdapter(mMovie.getVideos().getResults(), this);
        RecyclerView.LayoutManager horizontalLayoutManager = new LinearLayoutManager(
                getApplicationContext(),
                RecyclerView.HORIZONTAL,
                false);
        mTrailersRecyclerView.setLayoutManager(horizontalLayoutManager);
        mTrailersRecyclerView.setHasFixedSize(true);
        mTrailersRecyclerView.setAdapter(trailersAdapter);
        //reviews
        mReviewsNumTextView.setText(getString(R.string.reviews_num_format,
                mMovie.getReviews().getResults().size()));
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
        //TODO move off ui thread as in APIUtils idea
        if(view.getId()==R.id.favImageButton){
            if(!mIsFavorite){
                try{
                    DataUtils.saveFavoriteMovie(mMovie, getApplicationContext());
                    mIsFavorite = true;
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }else{
                try{
                    DataUtils.deleteFavorite(mMovie, getApplicationContext());
                    mIsFavorite = false;
                }catch (Exception e){
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
            mFavoriteButton.setSelected(mIsFavorite);
            Toast.makeText(this,
                    mIsFavorite? getString(R.string.message_fav_added) : getString(R.string.message_fav_removed),
                    Toast.LENGTH_SHORT).show();

        }
    }
    private void finishWithToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

}
