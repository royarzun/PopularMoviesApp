package info.royarzun.popularmovies.data.models;

import com.google.gson.annotations.SerializedName;


public class MovieTrailer {

    @SerializedName("id")
    public String id;

    @SerializedName("key")
    public String key;

    @SerializedName("name")
    public String name;

    @SerializedName("site")
    public String site;

    @SerializedName("size")
    public int size;

    @SerializedName("type")
    public String type;
}
