package com.techpearl.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.techpearl.popularmovies.R;

/**
 * Created by Nour on 2/21/2018.
 */

public class PreferencesUtils {
    public static int getPreferredSortOrder(Context context){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(
                context.getString(R.string.pref_sort_order),
                context.getResources().getInteger(R.integer.pref_sort_order_default));
    }
    public static void setPreferredSortOrder(Context context, int sortOrder){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.pref_sort_order), sortOrder);
        editor.apply();
    }
}
