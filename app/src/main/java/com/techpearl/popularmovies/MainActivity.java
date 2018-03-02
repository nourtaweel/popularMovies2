package com.techpearl.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.techpearl.popularmovies.adapters.MoviesAdapter;
import com.techpearl.popularmovies.fragments.FavoriteFragment;
import com.techpearl.popularmovies.fragments.PopularFragment;
import com.techpearl.popularmovies.fragments.TopRatedFragment;
import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.utils.PreferencesUtils;

public class MainActivity extends AppCompatActivity{
    private static String TAG = MainActivity.class.getSimpleName();
    private int mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSortOrder = PreferencesUtils.getPreferredSortOrder(this);
    }

    private void showFragment() {
        Fragment fragmentToShow;
        switch (mSortOrder){
            case 0:
                fragmentToShow = new PopularFragment();
                break;
            case 1:
                fragmentToShow = new TopRatedFragment();
                break;
            case 2:
                fragmentToShow = new FavoriteFragment();
                break;
            default:
                throw new UnsupportedOperationException("There is no fragment for option " + mSortOrder);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.resultsFrameLayout, fragmentToShow);
        transaction.commit();
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
                //String[] sortOptions = getResources().getStringArray(R.array.order_by);
                mSortOrder = i;
                PreferencesUtils.setPreferredSortOrder(MainActivity.this, i);
                showFragment();
                /*String selectedOption = sortOptions[i];
                if(selectedOption.equals(getString(R.string.option_sort_by_popular))){
                    PreferencesUtils.setPreferredSortOrder(MainActivity.this, SORT_ORDER_POPULAR);
                }else if(selectedOption.equals(getString(R.string.option_sort_by_top_rated))){
                    PreferencesUtils.setPreferredSortOrder(MainActivity.this, SORT_ORDER_TOP_RATED);
                }else if(selectedOption.equals(getString(R.string.option_show_favorite))){
                    PreferencesUtils.setPreferredSortOrder(MainActivity.this, SHOW_FAVORITE);
                }*/

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mSortOrder = getResources().getInteger(R.integer.pref_sort_order_default);
                showFragment();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }



}
