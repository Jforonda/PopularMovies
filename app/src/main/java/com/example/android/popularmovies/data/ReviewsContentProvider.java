package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.example.android.popularmovies.data.ReviewsContract.ReviewEntry.COLUMN_ID;
import static com.example.android.popularmovies.data.ReviewsContract.ReviewEntry.CONTENT_URI;
import static com.example.android.popularmovies.data.ReviewsContract.ReviewEntry.TABLE_NAME;

public class ReviewsContentProvider extends ContentProvider {

    public static final int REVIEWS = 100;
    public static final int REVIEWS_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(ReviewsContract.AUTHORITY, ReviewsContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(ReviewsContract.AUTHORITY, ReviewsContract.PATH_REVIEWS + "/#",
                REVIEWS_WITH_ID);

        return uriMatcher;
    }

    private ReviewsDbHelper mReviewsDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mReviewsDbHelper = new ReviewsDbHelper(context);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mReviewsDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case REVIEWS:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REVIEWS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                selection = TABLE_NAME + "." + COLUMN_ID + " = " + id;

                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mReviewsDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case REVIEWS:
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mReviewsDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int taskDeleted;

        if (selection == null) selection = "1";

        switch(match) {
            case REVIEWS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                taskDeleted = db.delete(TABLE_NAME, "id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return taskDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}
