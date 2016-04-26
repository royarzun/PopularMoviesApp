package info.royarzun.popularmovies.data.models;

import com.google.gson.annotations.SerializedName;


public class MovieComment {
    @SerializedName("id")
    public String id;


    @SerializedName("author")
    public String author;


    @SerializedName("content")
    public String content;
}