package com.techpearl.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.techpearl.popularmovies.adapters.ReviewsAdapter;
import com.techpearl.popularmovies.adapters.TrailersAdapter;
import com.techpearl.popularmovies.loaders.FavoriteStatusLoader;
import com.techpearl.popularmovies.loaders.ToggleFavoriteStatusLoader;
import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.utils.YoutubeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements
        TrailersAdapter.TrailerClickListener,
        View.OnClickListener{
    private Movie mMovie;
    private boolean mIsFavorite;

    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final String MOVIE_ID_BUNDLE_KEY = "movie_id";
    private static final String MOVIE_BUNDLE_KEY = "movie_object";

    //loaders ids
    private static final int LOADER_MOVIE = 1;
    private static final int LOADER_IS_FAVORITE = 2;
    private static final int LOADER_TOGGLE_FAVORITE = 3;

    private LoaderManager.LoaderCallbacks toggleFavCallback;

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
        //getting id of movie to be displayed
        Intent startingIntent = getIntent();
        if(!startingIntent.hasExtra(getString(R.string.intent_extra_movie))){
            finishWithToast(getString(R.string.missing_movie_data));
        }
        int movieId = startingIntent.getIntExtra(getString(R.string.intent_extra_movie), -1);
        //construct callbacks for (movie loader + favorite status loader)
        LoaderManager.LoaderCallbacks<Movie> movieLoaderCallback = buildMovieLoaderCallback();
        LoaderManager.LoaderCallbacks<Boolean> favoriteStatusLoaderCallback = buildFavoriteLoaderCallback();
        toggleFavCallback = buildToggleFavCallback();
        //init loaders to display movie data
        LoaderManager loaderManager = getSupportLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_ID_BUNDLE_KEY, movieId);
        loaderManager.initLoader(LOADER_MOVIE, bundle, movieLoaderCallback);
        loaderManager.initLoader(LOADER_IS_FAVORITE, bundle, favoriteStatusLoaderCallback);
    }
    // populate views with mMovie object
    private void populateUI() {
        if(mMovie == null){
            //no data available
            finishWithToast(getString(R.string.error_message));
            return;
        }
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        mTitleTextView.setText(mMovie.getTitle());
        Picasso.with(this).load(mMovie.getFullPosterPath(this))
                .placeholder(R.color.placehloderColor)
                .error(R.color.placehloderColor)
                .into(mPosterImageView);
        Picasso.with(this).load(mMovie.getFullBackdropPath(this))
                .placeholder(R.color.placehloderColor)
                .error(R.color.placehloderColor)
                .into(mBackdropImageView);
        Double userRating = (mMovie.getVoteAverage() * 10);
        mRatingBar.setProgress(userRating.intValue());
        mUserRatingTextView.setText(getString(R.string.rating_of_ten_format, mMovie.getVoteAverage()));
        mReleaseDateTextView.setText(mMovie.getReleaseDate().substring(0,4));
        mRuntimeTextView.setText(getString(R.string.runtime_format,
                mMovie.getRuntime()/60,
                mMovie.getRuntime()%60));
        mOverviewTextView.setText(mMovie.getOverview());
        //trailers recyclerView
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
        //reviews recyclerView
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

    //prepares favorite based on data returned by loader
    private void populateFavoriteIcon(){
        mFavoriteButton.setSelected(mIsFavorite);
        mFavoriteButton.setVisibility(View.VISIBLE);
        mFavoriteButton.setOnClickListener(this);
    }
    //callback when trailer item has been clicked
    @Override
    public void onTrailerClicked(String trailerKey) {
        YoutubeUtils.launchYoutube(this, trailerKey);
    }

    @Override
    public void onClick(View view) {
        //if favorite button has been clicked, prepare mMovie in bundle and start loader to toggle
        //favorite state
        if(view.getId()==R.id.favImageButton){
            Bundle bundle = new Bundle();
            bundle.putParcelable(MOVIE_BUNDLE_KEY, mMovie);
            getSupportLoaderManager()
                    .restartLoader(LOADER_TOGGLE_FAVORITE, bundle, this.toggleFavCallback);
        }
    }

    //A method to finish the Activity gracefully when error occurs
    private void finishWithToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private LoaderManager.LoaderCallbacks<Boolean> buildToggleFavCallback() {
        return new LoaderManager.LoaderCallbacks<Boolean>() {
            @NonNull
            @Override
            public Loader onCreateLoader(int id, @Nullable Bundle args) {
                if(!(args != null && args.containsKey(MOVIE_BUNDLE_KEY))) {
                    return null;
                }
                Movie movie = (Movie) args.get(MOVIE_BUNDLE_KEY);
                return new ToggleFavoriteStatusLoader(DetailsActivity.this, movie);
            }

            @Override
            public void onLoadFinished(@NonNull Loader loader, Boolean isFav) {
                mIsFavorite = isFav;
                Toast.makeText(DetailsActivity.this,
                        isFav? getString(R.string.message_fav_added) : getString(R.string.message_fav_removed),
                        Toast.LENGTH_SHORT).show();
                populateFavoriteIcon();
            }

            @Override
            public void onLoaderReset(@NonNull Loader loader) {

            }
        };
    }

    private LoaderManager.LoaderCallbacks<Boolean> buildFavoriteLoaderCallback() {
        return new LoaderManager.LoaderCallbacks<Boolean>() {
            @NonNull
            @Override
            public Loader<Boolean> onCreateLoader(int id, @Nullable Bundle args) {
                if(!(args != null && args.containsKey(MOVIE_ID_BUNDLE_KEY))) {
                    return null;
                }
                int movieId = args.getInt(MOVIE_ID_BUNDLE_KEY);
                return new FavoriteStatusLoader(DetailsActivity.this, movieId);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean data) {
                mIsFavorite = data;
                populateFavoriteIcon();
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Boolean> loader) {
            }
        };
    }

    private LoaderManager.LoaderCallbacks<Movie> buildMovieLoaderCallback() {
        return new LoaderManager.LoaderCallbacks<Movie>() {
            @NonNull
            @Override
            public Loader<Movie> onCreateLoader(int id, @Nullable Bundle args) {
                if(!(args != null && args.containsKey(MOVIE_ID_BUNDLE_KEY))) {
                    return null;
                }
                int movieId = args.getInt(MOVIE_ID_BUNDLE_KEY);
                return new com.techpearl.popularmovies.loaders.MovieLoader(DetailsActivity.this, movieId);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Movie> loader, Movie data) {
                mMovie = data;
                populateUI();
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Movie> loader) {

            }
        };
    }

}
