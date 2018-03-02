package com.techpearl.popularmovies.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techpearl.popularmovies.DetailsActivity;
import com.techpearl.popularmovies.R;
import com.techpearl.popularmovies.adapters.MoviesAdapter;
import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.utils.DataUtils;

import java.util.List;

/**
 * A fragment to show user's favorite movies
 */
public class FavoriteFragment extends BaseMoviesFragment {
    private static final String TAG = FavoriteFragment.class.getSimpleName();

    public FavoriteFragment() {
    }

    @Override
    void loadMovieList() {
        List<Movie> faves = DataUtils.getFavorites(getContext());
        showResponse(faves);
    }
}
