package info.royarzun.popularmovies.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MoviesDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = MoviesDatabaseHelper.class.getSimpleName();
    static final int DB_VERSION = 3;
    static final String DB_NAME = "popular_movies_app.db";

    public MoviesDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createMoviesTable = "CREATE TABLE " + MoviesContract.Movies.TABLE_NAME + " ( "
                + MoviesContract.Movies._ID + " INTEGER PRIMARY KEY, "
                + MoviesContract.Movies.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MoviesContract.Movies.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + MoviesContract.Movies.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, "
                + MoviesContract.Movies.COLUMN_MOVIE_VOTE_AVERAGE + " REAL NOT NULL, "
                + MoviesContract.Movies.COLUMN_MOVIE_VOTE_COUNT + " INTEGER NOT NULL, "
                + MoviesContract.Movies.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, "
                + MoviesContract.Movies.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, "
                + MoviesContract.Movies.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL, "
                + MoviesContract.Movies.COLUMN_MOVIE_POPULARITY + " REAL NOT NULL, "
                + MoviesContract.Movies.COLUMN_MOVIE_FAVORED + " BOOLEAN DEFAULT FALSE, "
                + " UNIQUE (" + MoviesContract.Movies.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String createTrailersTable = "CREATE TABLE " + MoviesContract.Trailers.TABLE_NAME + " ( "
                + MoviesContract.Trailers._ID + " INTEGER PRIMARY_KEY, "
                + MoviesContract.Trailers.COLUMN_TRAILER_ID + " INTEGER NOT NULL, "
                + MoviesContract.Trailers.COLUMN_TRAILER_SITE + " TEXT NOT NULL, "
                + MoviesContract.Trailers.COLUMN_TRAILER_KEY + " TEXT NOT NULL, "
                + MoviesContract.Trailers.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + " FOREIGN KEY (" + MoviesContract.Trailers.COLUMN_MOVIE_ID + ") REFERENCES "
                + MoviesContract.Movies.TABLE_NAME + " (" + MoviesContract.Movies.COLUMN_MOVIE_ID + "),"
                + "UNIQUE (" + MoviesContract.Trailers.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE);";

        final String createReviewsTable = "CREATE TABLE " + MoviesContract.Reviews.TABLE_NAME + " ( "
                + MoviesContract.Reviews._ID + " INTEGER PRIMARY KEY, "
                + MoviesContract.Reviews.COLUMN_REVIEW_ID + " INTEGER NOT NULL, "
                + MoviesContract.Reviews.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, "
                + MoviesContract.Reviews.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, "
                + MoviesContract.Reviews.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + " FOREIGN KEY (" + MoviesContract.Reviews.COLUMN_MOVIE_ID + ") REFERENCES "
                + MoviesContract.Movies.TABLE_NAME + " (" + MoviesContract.Movies.COLUMN_MOVIE_ID + "),"
                + "UNIQUE (" + MoviesContract.Reviews.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(createMoviesTable);
        db.execSQL(createTrailersTable);
        db.execSQL(createReviewsTable);
        Log.d(TAG, "Database tables created...");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.Movies.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.Trailers.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.Reviews.TABLE_NAME);
        onCreate(db);
    }
}