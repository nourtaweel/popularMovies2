package com.techpearl.popularmovies.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.techpearl.popularmovies.model.Movie;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nour on 2/20/2018.
 */

public class MoviesDeserializer implements JsonDeserializer<List<Movie>> {
    private static final String MOVIES_ARRAY_KEY = "results";
@Override
public List<Movie> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
        throws JsonParseException {
    Type moviesListType = new TypeToken<ArrayList<Movie>>(){}.getType();
    JsonElement results = je.getAsJsonObject().get(MOVIES_ARRAY_KEY);
    return new Gson().fromJson(results, moviesListType);

        }
}
