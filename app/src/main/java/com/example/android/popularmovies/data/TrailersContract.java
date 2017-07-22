package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class TrailersContract {
    public static final String AUTHORITY = "com.example.android.popularmovies.data.TrailersContract";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TRAILERS = "trailers";

    public static final class TrailerEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String TABLE_NAME = "trailers";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TRAILER_PATH = "trailer_path";
    }
}
