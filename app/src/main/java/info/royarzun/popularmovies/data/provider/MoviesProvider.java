package info.royarzun.popularmovies.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;


public class MoviesProvider extends ContentProvider {
    private MoviesDatabaseHelper dbHelper;
    private static final UriMatcher sUriMatcher = getUriMatcher();

    private static final int MOVIE = 100;
    private static final int MOVIE_ID = 101;

    private static final int TRAILER = 200;
    private static final int TRAILER_MOVIE_ID = 201;

    private static final int REVIEW = 300;
    private static final int REVIEW_MOVIE_ID = 301;


    public MoviesProvider() {
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDatabaseHelper(getContext());
        return true;
    }

    public static UriMatcher getUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.MOVIES_PATH, MOVIE);
        matcher.addURI(authority, MoviesContract.MOVIES_PATH + "/#", MOVIE_ID);

        matcher.addURI(authority, MoviesContract.TRAILERS_PATH, TRAILER);
        matcher.addURI(authority, MoviesContract.TRAILERS_PATH + "/#", TRAILER_MOVIE_ID);

        matcher.addURI(authority, MoviesContract.REVIEWS_PATH, REVIEW);
        matcher.addURI(authority, MoviesContract.REVIEWS_PATH + "/#", REVIEW_MOVIE_ID);

        return matcher;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                return MoviesContract.Movies.CONTENT_TYPE;

            case MOVIE_ID:
                return MoviesContract.Movies.CONTENT_ITEM_TYPE;

            case REVIEW:
                return MoviesContract.Reviews.CONTENT_TYPE;

            case REVIEW_MOVIE_ID:
                return MoviesContract.Reviews.CONTENT_ITEM_TYPE;

            case TRAILER:
                return MoviesContract.Trailers.CONTENT_TYPE;

            case TRAILER_MOVIE_ID:
                return MoviesContract.Trailers.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                cursor = db.query(MoviesContract.Movies.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case MOVIE_ID: {
                long _id = MoviesContract.Movies.getMovieId(uri);
                cursor = db.query(MoviesContract.Movies.TABLE_NAME, projection,
                        MoviesContract.Movies.COLUMN_MOVIE_ID + " = ?", new String[]{Long.toString(_id)}, null,
                        null, sortOrder);
                break;
            }

            case TRAILER:
                cursor = db.query(MoviesContract.Trailers.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case TRAILER_MOVIE_ID: {
                long _id = MoviesContract.Trailers.getMovieId(uri);
                cursor = db.query(MoviesContract.Trailers.TABLE_NAME, projection,
                        MoviesContract.Trailers.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Long.toString(_id)},
                        null, null, sortOrder);
                break;
            }

            case REVIEW:
                cursor = db.query(MoviesContract.Reviews.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case REVIEW_MOVIE_ID: {
                long _id = MoviesContract.Reviews.getMovieId(uri);
                cursor = db.query(MoviesContract.Reviews.TABLE_NAME, projection,
                        MoviesContract.Reviews.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Long.toString(_id)}, null, null, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri insertionUri;

        long insertedId;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                insertedId = db.insert(MoviesContract.Movies.TABLE_NAME, null, values);
                if (insertedId > 0) {
                    insertionUri = MoviesContract.Movies.buildMovieUri(insertedId);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            case TRAILER:
                insertedId = db.insert(MoviesContract.Trailers.TABLE_NAME, null, values);
                if (insertedId > 0) {
                    insertionUri = MoviesContract.Trailers.buildMovieUri(insertedId);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            case REVIEW:
                insertedId = db.insert(MoviesContract.Reviews.TABLE_NAME, null, values);
                if (insertedId > 0) {
                    insertionUri = MoviesContract.Reviews.buildMovieUri(insertedId);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return insertionUri;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                rowsUpdated = db.update(MoviesContract.Movies.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            case TRAILER:
                rowsUpdated = db.update(MoviesContract.Trailers.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            case REVIEW:
                rowsUpdated = db.update(MoviesContract.Reviews.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;
        //delete all rows and return the number of records deleted
        if (selection == null) {
            selection = "1";
        }

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                rowsDeleted = db.delete(MoviesContract.Movies.TABLE_NAME, selection, selectionArgs);
                break;

            case TRAILER:
                rowsDeleted = db.delete(MoviesContract.Trailers.TABLE_NAME, selection, selectionArgs);
                break;

            case REVIEW:
                rowsDeleted = db.delete(MoviesContract.Reviews.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }
}
