package com.techpearl.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nour on 2/27/2018.
 */

public class VideosList implements Parcelable{
    @SerializedName("results")
    @Expose
    private List<Video> results = null;
    public final static Parcelable.Creator<VideosList> CREATOR = new Creator<VideosList>() {


        @SuppressWarnings({
                "unchecked"
        })
        public VideosList createFromParcel(Parcel in) {
            return new VideosList(in);
        }

        public VideosList[] newArray(int size) {
            return (new VideosList[size]);
        }

    }
            ;

    protected VideosList(Parcel in) {
        in.readList(this.results, (Video.class.getClassLoader()));
    }

    public VideosList() {
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "results" + results;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(results);
    }

    public int describeContents() {
        return 0;
    }

}
