package com.techpearl.popularmovies.model;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.techpearl.popularmovies.R;

/**
 * Created by Nour on 2/19/2018.
 */

public class Movie implements Parcelable {

    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("video")
    @Expose
    private Boolean video;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("popularity")
    @Expose
    private Double popularity;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("original_language")
    @Expose
    private String originalLanguage;
    @SerializedName("original_title")
    @Expose
    private String originalTitle;
    @SerializedName("genre_ids")
    @Expose
    private List<Integer> genreIds = null;
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("adult")
    @Expose
    private Boolean adult;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("videos")
    @Expose
    private VideosList videos = null;
    @SerializedName("reviews")
    @Expose
    private ReviewsList reviews = null;
    public final static Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @SuppressWarnings({"unchecked"})
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return (new Movie[size]);
        }
    };

    public Movie() {
    }

    protected Movie(Parcel in) {
        this.voteCount = in.readInt();
        this.id = in.readInt();
        this.video = in.readByte() != 0;
        this.voteAverage = in.readDouble();
        this.title = in.readString();
        this.popularity = in.readDouble();
        this.posterPath = in.readString();
        this.originalLanguage = in.readString();
        this.originalTitle = in.readString();
        this.genreIds = new ArrayList<>();
        in.readList(this.genreIds, (java.lang.Integer.class.getClassLoader()));
        this.backdropPath = in.readString();
        this.adult = in.readByte() != 0;
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.videos = in.readParcelable(VideosList.class.getClassLoader());
        this.reviews = in.readParcelable(ReviewsList.class.getClassLoader());
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public VideosList getVideos() {
        return videos;
    }

    public void setVideos(VideosList videos) {
        this.videos = videos;
    }

    public ReviewsList getReviews() {
        return reviews;
    }

    public void setReviews(ReviewsList reviews) {
        this.reviews = reviews;
    }

    public String getFullPosterPath(Context context){
        return context.getString(R.string.poster_base_path) + this.posterPath;
    }
    public String getFullBackdropPath(Context context){
        return context.getString(R.string.backdrop_base_path) + this.backdropPath;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(voteCount);
        dest.writeInt(id);
        dest.writeByte((byte) (video ? 1 : 0));//based on this answer https://stackoverflow.com/questions/6201311/how-to-read-write-a-boolean-when-implementing-the-parcelable-interface
        dest.writeDouble(voteAverage);
        dest.writeString(title);
        dest.writeDouble(popularity);
        dest.writeString(posterPath);
        dest.writeString(originalLanguage);
        dest.writeString(originalTitle);
        dest.writeList(genreIds);
        dest.writeString(backdropPath);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeValue(videos);
        dest.writeValue(reviews);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "voteCount: " + voteCount + "\n" +
                "id: " + id + "\n" +
                "video: " + video + "\n" +
                "voteAverage: " + voteAverage + "\n" +
                "title: " + title + "\n" +
                "popularity: " + popularity + "\n" +
                "posterPath: " + posterPath + "\n" +
                "originalLanguage: " + originalLanguage + "\n" +
                "originalTitle: " + originalTitle + "\n" +
                "genreIds: " + genreIds + "\n" +
                "backdropPath: " + backdropPath + "\n" +
                "adult: " + adult + "\n" +
                "overview: " + overview + "\n" +
                "releaseDate: " + releaseDate + "\n" +
                "trailers: " + videos + "\n" +
                "reviews: " + reviews;
    }
}
