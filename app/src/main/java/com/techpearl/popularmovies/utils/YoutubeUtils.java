package com.techpearl.popularmovies.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.techpearl.popularmovies.R;

/**
 * Created by Nour on 2/27/2018.
 * methods to construct Youtube paths
 */

public class YoutubeUtils {

    public static String constructYoutubeImagePath(String key, Context context) {
        return context.getString(R.string.youtube_format, key);
    }

    private static Intent constructYoutubeAppIntent(Context context, String key){
        return new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.youtube_intent_scheme) + key));
    }

    private  static Intent constructYoutubeWebIntent(Context context, String key){
        return new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.youtube_watch_link) + key));
    }
    public static void launchYoutube(Context context, String key){
        /* based on
        *  https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
         */
        Intent appIntent = constructYoutubeAppIntent(context, key);
        Intent browserIntent = constructYoutubeWebIntent(context, key);
        try{
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException exp){
            context.startActivity(browserIntent);
        }
    }
}
