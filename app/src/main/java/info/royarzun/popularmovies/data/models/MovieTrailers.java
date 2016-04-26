package info.royarzun.popularmovies.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;


public class MovieTrailers {
    @SerializedName("results")
    public List<MovieTrailer> movieTrailers;
}
