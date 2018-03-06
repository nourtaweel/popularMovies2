package com.techpearl.popularmovies;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.techpearl.popularmovies.fragments.FavoriteFragment;
import com.techpearl.popularmovies.fragments.PopularFragment;
import com.techpearl.popularmovies.fragments.TopRatedFragment;
import com.techpearl.popularmovies.utils.PreferencesUtils;

public class MainActivity extends AppCompatActivity{
    private static String TAG = MainActivity.class.getSimpleName();
    private static final String FRAGMENT_RETAINED_TAG = "RetainedFragment";
    private Fragment mFragmentToShow;
    private int mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSortOrder = PreferencesUtils.getPreferredSortOrder(this);
        FragmentManager fm = getSupportFragmentManager();
        mFragmentToShow = fm.findFragmentByTag(FRAGMENT_RETAINED_TAG);
        if (mFragmentToShow == null) {
            showNewFragment();
            Log.d(TAG, "new fragment");
        }else {
            Log.d(TAG, "retained fragment");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.resultsFrameLayout, mFragmentToShow, FRAGMENT_RETAINED_TAG);
            transaction.commit();
        }
    }

    private void showNewFragment() {
            switch (mSortOrder){
                case 0:
                    mFragmentToShow = new PopularFragment();
                    break;
                case 1:
                    mFragmentToShow = new TopRatedFragment();
                    break;
                case 2:
                    mFragmentToShow = new FavoriteFragment();
                    break;
                default:
                    throw new UnsupportedOperationException("There is no fragment for option " + mSortOrder);
            }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.resultsFrameLayout, mFragmentToShow, FRAGMENT_RETAINED_TAG);
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
                mSortOrder = i;
                PreferencesUtils.setPreferredSortOrder(MainActivity.this, i);
                showNewFragment();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mSortOrder = getResources().getInteger(R.integer.pref_sort_order_default);
                showNewFragment();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()) {
            FragmentManager fm = getSupportFragmentManager();
            // we will not need this fragment anymore, this may also be a good place to signal
            // to the retained fragment object to perform its own cleanup.
            fm.beginTransaction().remove(mFragmentToShow).commit();
        }
    }
}
