package com.techpearl.popularmovies.api;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.techpearl.popularmovies.model.Review;
import com.techpearl.popularmovies.model.Video;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nour on 2/26/2018.
 */

public class VideosDeserializer implements JsonDeserializer<List<Video>> {
    private static final String VIDEOS_ARRAY_KEY = "results";

    @Override
    public List<Video> deserialize(JsonElement je, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        Type videosListType = new TypeToken<ArrayList<Video>>(){}.getType();
        JsonElement results = je.getAsJsonObject().get(VIDEOS_ARRAY_KEY);
        return new Gson().fromJson(results, videosListType);
    }
}
