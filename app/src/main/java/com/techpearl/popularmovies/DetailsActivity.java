package com.techpearl.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements
        TrailersAdapter.TrailerClickListener,
        View.OnClickListener,
        LoaderManager.LoaderCallbacks<Movie>{
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final String MOVIE_ID_BUNDLE_KEY = "movie_id";
    private static int LOADER_MOVIE_ID = 1;
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
        LoaderManager loaderManager = getSupportLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_ID_BUNDLE_KEY, movieId);
        loaderManager.initLoader(LOADER_MOVIE_ID, bundle, this);
    }

    private void populateUI() {
        if(mMovie == null){
            finishWithToast(getString(R.string.error_message));
            return;
        }
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

    @NonNull
    @Override
    public Loader<Movie> onCreateLoader(int id, @Nullable Bundle args) {
        if(!(args != null && args.containsKey(MOVIE_ID_BUNDLE_KEY)))
            return null;
        int movieId = args.getInt(MOVIE_ID_BUNDLE_KEY);
        return new MovieLoader(this, movieId);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Movie> loader, Movie data) {
        mMovie = data;
        populateUI();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Movie> loader) {

    }
    /**
     * a loader that decides whether to fetch the Movie Object from the api directly (if there is
     * a connection)
     * or fetch it from content provider if there was no connection and the movie was one of the user's
     * favorites
     */
    private static class MovieLoader extends AsyncTaskLoader<Movie> {
        private int mMovieId;
        private Movie loadedMovie;
        MovieLoader(@NonNull Context context, int movieId) {
            super(context);
            mMovieId = movieId;
        }

        @Override
        protected void onStartLoading() {
            if(loadedMovie != null){
                deliverResult(loadedMovie);
            }else {
                forceLoad();
            }
        }

        @Override
        public void deliverResult(@Nullable Movie data) {
            super.deliverResult(data);
            loadedMovie = data;
        }

        @Nullable
        @Override
        public Movie loadInBackground() {
            if(mMovieId == -1){
                return null;
            }
            boolean isFavorite = DataUtils.isFavorite(mMovieId, getContext());
            if(ApiUtils.isConnected(getContext())){
                //fetch from online
                try{
                    return fetchFromApi(mMovieId);
                }catch (IOException ioe){
                    Log.e(TAG, "error fetching movie");
                }
            }else {
                //if favorite movie fetch data from ContentProvider, if not display an error message
                if(isFavorite){
                    return DataUtils.getFavoriteMovie(String.valueOf(mMovieId), getContext());
                }
            }
            return null;
        }
        private Movie fetchFromApi(int movieId) throws IOException {
            MoviesDbClient moviesDbClient = ServiceGenerator.createService(MoviesDbClient.class);
            Call<Movie> call = moviesDbClient.movieWithTrailersAndReviews(movieId,
                    ServiceGenerator.API_KEY,
                    "videos,reviews");
            return call.execute().body();
        }
    }
}
