package com.techpearl.popularmovies.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.techpearl.popularmovies.DetailsActivity;
import com.techpearl.popularmovies.R;
import com.techpearl.popularmovies.adapters.MoviesAdapter;
import com.techpearl.popularmovies.model.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Nour on 3/2/2018.
 * a Base Fragment class for the three fragments (TopRatedFragment, PopularFragment, FavoriteFragment)
 */

abstract class BaseMoviesFragment extends Fragment implements MoviesAdapter.MovieClickListener {
    private static final String BUNDLE_RECYCLER_LAYOUT = "recyclerViewState";
    @BindView(R.id.moviesRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.errorView) View mErrorView;
    @BindView(R.id.errorTextView) TextView mErrorTextView;
    @BindView(R.id.refreshButton) Button mRefreshButton;
    private MoviesAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private Unbinder unbinder;
    private int mSavedRecyclerPosition;
    public BaseMoviesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh(view);
            }
        });
        mLayoutManager = new GridLayoutManager(this.getContext(), numberOfColumns());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MoviesAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);
        loadMovieList();
        Log.d("pos cr", getClass().getCanonicalName()+" "+mSavedRecyclerPosition);
    }

    //any fragment must override this method to load List<Movie>
    abstract void loadMovieList();

    protected void showResponse(List<Movie> body) {
        mErrorView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.setMovies(body);
        Log.d("pos sho", getClass().getCanonicalName()+" "+mSavedRecyclerPosition + " ");
        mRecyclerView.scrollToPosition(mSavedRecyclerPosition);
    }

    protected void showErrorMessage(String message) {
        mErrorTextView.setText(message);
        mErrorView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }
    public void refresh(View view) {
        loadMovieList();
    }
    @Override
    public void onMovieClicked(Movie movie) {
        Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
        detailsIntent.putExtra(getString(R.string.intent_extra_movie), movie.getId());
        startActivity(detailsIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //unbinder.unbind();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
        {
            mSavedRecyclerPosition = savedInstanceState.getInt(BUNDLE_RECYCLER_LAYOUT);
            Log.d("pos restore", getClass().getCanonicalName()+" "+mSavedRecyclerPosition);
            mRecyclerView.scrollToPosition(mSavedRecyclerPosition);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_RECYCLER_LAYOUT, mLayoutManager.findFirstVisibleItemPosition());
    }

    /* Dynamically determine number of columns for different widths
     */
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDividerDp = 200;
        float widthDividerPx = widthDividerDp * (displayMetrics.densityDpi / 160f);
        int width = displayMetrics.widthPixels;
        return Math.round(width / widthDividerPx);
    }
}
