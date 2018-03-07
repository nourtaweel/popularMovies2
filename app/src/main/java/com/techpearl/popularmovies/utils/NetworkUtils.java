package com.techpearl.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Nour on 2/26/2018.
 * methods for network
 */

public class NetworkUtils {
    private final static int SORT_ORDER_POPULAR = 0;
    private final static int SORT_ORDER_TOP_RATED = 1;
    
    public static boolean isConnected(Context context){
        /* Based on code snippet in
         * https://developer.android.com/training/basics/network-ops/managing.html */
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
        
    }
}
