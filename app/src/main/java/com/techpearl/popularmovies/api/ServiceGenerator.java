package com.techpearl.popularmovies.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import com.techpearl.popularmovies.BuildConfig;
import com.techpearl.popularmovies.model.Movie;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Nour on 2/20/2018.
 * based on this tutorial: https://futurestud.io/tutorials/retrofit-2-creating-a-sustainable-android-client
 */

public class ServiceGenerator {
    public static final String API_KEY = BuildConfig.API_KEY;
    private static final String API_URL_BASE = "http://api.themoviedb.org/3/";

    private static OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(List.class, new MoviesDeserializer())
            .create();
    private static Gson gson1 = new GsonBuilder()
            .registerTypeAdapter(Movie.class, new MoviesDeserializer())
            .create();

    private static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(API_URL_BASE)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient);
    private static Retrofit retrofit = retrofitBuilder.build();

    private static Retrofit.Builder retrofitBuilder1 = new Retrofit.Builder()
            .baseUrl(API_URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient);

    private static Retrofit retrofit1 = retrofitBuilder1.build();

    public static <S> S createService(Class<S> serviceClass, boolean defaultGson) {
        if(defaultGson){
            return retrofit1.create(serviceClass);
        }
        return retrofit.create(serviceClass);
    }
}
