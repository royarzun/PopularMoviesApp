package info.royarzun.popularmovies.data.provider;

import android.net.Uri;
import android.provider.BaseColumns;


public final class MoviesContract {

    public interface MoviesColumns {
        String COLUMN_MOVIE_ID = "movie_id";
        String COLUMN_MOVIE_TITLE = "movie_title";
        String COLUMN_MOVIE_OVERVIEW = "movie_overview";
        String COLUMN_MOVIE_POPULARITY = "movie_popularity";
        String COLUMN_MOVIE_VOTE_COUNT = "movie_vote_count";
        String COLUMN_MOVIE_VOTE_AVERAGE = "movie_vote_average";
        String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        String COLUMN_MOVIE_FAVORED = "movie_favored";
        String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";
    }

    public interface ReviewsColumns {
        String COLUMN_MOVIE_ID = "movie_id";
        String COLUMN_REVIEW_ID = "review_id";
        String COLUMN_REVIEW_AUTHOR = "review_author";
        String COLUMN_REVIEW_CONTENT = "review_content";
    }

    public interface TrailerColumns {
        String COLUMN_MOVIE_ID = "movie_id";
        String COLUMN_TRAILER_ID = "trailer_id";
        String COLUMN_TRAILER_SITE = "trailer_site";
        String COLUMN_TRAILER_KEY = "trailer_key";
    }

    public static final String CONTENT_AUTHORITY = "info.royarzun.popularmovies.provider";
    public static final String MOVIES_PATH = "movies";
    public static final String TRAILERS_PATH = "trailers";
    public static final String REVIEWS_PATH = "reviews";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static class Movies implements MoviesColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_PATH).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.popularmovies.movie";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.popularmovies.movie";

        public static final String TABLE_NAME = "movies";
        public static final String POPULARITY_SORT = COLUMN_MOVIE_POPULARITY + " DESC";
        public static final String VOTE_SORT = COLUMN_MOVIE_VOTE_AVERAGE + " DESC";

        public static Uri buildMovieUri(long movieId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        }

        public static long getMovieId(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static class Reviews implements ReviewsColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(REVIEWS_PATH).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.popularmovies.review";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.popularmovies.review";

        public static final String TABLE_NAME = "reviews";

        public static Uri buildMovieUri(long movieId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        }

        public static long getMovieId(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static class Trailers implements TrailerColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TRAILERS_PATH).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.popularmovies.trailer";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.popularmovies.trailer";

        public static final String TABLE_NAME = "trailers";

        public static Uri buildMovieUri(long movieId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        }

        public static long getMovieId(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}
