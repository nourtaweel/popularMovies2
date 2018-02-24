package com.techpearl.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.techpearl.popularmovies.api.MoviesDbClient;
import com.techpearl.popularmovies.api.ServiceGenerator;
import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.utils.PreferencesUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener{
    private static String TAG = MainActivity.class.getSimpleName();
    private final int SORT_ORDER_POPULAR = 0;
    private final int SORT_ORDER_TOP_RATED = 1;
    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    private View mErrorView;
    private TextView mErrorTextView;
    private int mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mErrorView = findViewById(R.id.errorView);
        mErrorTextView = (TextView) findViewById(R.id.errorTextView);
        mRecyclerView = (RecyclerView) findViewById(R.id.moviesRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new MoviesAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);
        callApi();
    }

    private void callApi() {
        if(!checkDeviceConnected()){
            showErrorMessage(getString(R.string.error_message_no_network));
            return;
        }
        mSortOrder = PreferencesUtils.getPreferredSortOrder(this);
        MoviesDbClient moviesDbClient = ServiceGenerator.createService(MoviesDbClient.class);
        Call<List<Movie>> call;
        if(mSortOrder == SORT_ORDER_POPULAR){
            call = moviesDbClient.popularMovies(ServiceGenerator.API_KEY);
        }else {
            call = moviesDbClient.topRatedMovies(ServiceGenerator.API_KEY);
        }
        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(@NonNull Call<List<Movie>> call, @NonNull Response<List<Movie>> response) {
                Log.d(TAG, response.body().toString());
                showResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Movie>> call, @NonNull Throwable t) {
                Log.e(TAG, getString(R.string.retrofit_error) + t.getMessage());
                showErrorMessage(getString(R.string.error_message));
            }
        });
    }

    private void showResponse(List<Movie> body) {
        mErrorView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.setMovies(body);
    }

    private void showErrorMessage(String message) {
        mErrorTextView.setText(message);
        mErrorView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem sortByItem = menu.findItem(R.id.action_sort);
        Spinner sortBySpinner = (Spinner) sortByItem.getActionView();
        setupSortBySpinner(sortBySpinner);
        return true;
    }

    private void setupSortBySpinner(Spinner sortBySpinner) {
        //this code is based on this tutorial
        // http://www.viralandroid.com/2016/03/how-to-add-spinner-dropdown-list-to-android-actionbar-toolbar.html
        ArrayAdapter<CharSequence> sortSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.order_by,
                R.layout.spinner_layout);
        sortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortSpinnerAdapter);
        //end
        sortBySpinner.setSelection(mSortOrder);
        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] sortOptions = getResources().getStringArray(R.array.order_by);
                String selectedOption = sortOptions[i];
                if(selectedOption.equals(
                        getResources().getString(R.string.option_sort_by_popular))){
                    PreferencesUtils.setPreferredSortOrder(MainActivity.this, SORT_ORDER_POPULAR);
                }else if(selectedOption.equals(
                        getResources().getString(R.string.option_sort_by_top_rated))){
                    PreferencesUtils.setPreferredSortOrder(MainActivity.this, SORT_ORDER_TOP_RATED);
                }
                callApi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mSortOrder = SORT_ORDER_POPULAR;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.action_settings){
            Toast.makeText(this, "open settings", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClicked(Movie movie) {
        Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
        detailsIntent.putExtra(getString(R.string.intent_extra_movie), movie);
        startActivity(detailsIntent);
    }

    public void refresh(View view) {
        callApi();
    }

    private boolean checkDeviceConnected() {
        /* Based on code snippet in
         * https://developer.android.com/training/basics/network-ops/managing.html */
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
