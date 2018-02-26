package com.techpearl.popularmovies.api;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.techpearl.popularmovies.model.Movie;
import com.techpearl.popularmovies.model.Review;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nour on 2/26/2018.
 */

public class ReviewsDeserializer implements JsonDeserializer<List<Review>> {
    private static final String VIDEOS_ARRAY_KEY = "results";
    @Override
    public List<Review> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
            throws JsonParseException {
        Type reviewsListType = new TypeToken<ArrayList<Review>>(){}.getType();
        JsonElement results = je.getAsJsonObject().get(VIDEOS_ARRAY_KEY);
        return new Gson().fromJson(results, reviewsListType);

    }
}
