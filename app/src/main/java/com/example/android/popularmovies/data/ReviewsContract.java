package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ReviewsContract {
    public static final String AUTHORITY = "com.example.android.popularmovies.data.ReviewsContract";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_REVIEWS = "reviews";

    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_REVIEW_TEXT = "review_text";
        public static final String COLUMN_REVIEW_AUTHOR = "review_author";
    }
}
