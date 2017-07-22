package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String SEARCH_MOVIE_DATABASE_URL =
            "https://api.themoviedb.org/3/search/movie";

    private static final String DISCOVER_MOVIE_DATABASE_URL =
            "https://api.themoviedb.org/3/discover/movie";

    private static final String MOVIE_DATABASE_URL =
            "https://api.themoviedb.org/3/movie";

    // No API KEY included in code, must include to run as intended
    private static final String API_KEY =
            "e9c55d4b3f907c30e8463e86711f92ec";

    private static final String API_KEY_PARAM = "api_key";
    private static final String QUERY_PARAM = "query";
    private static final String VIDEOS_PARAM = "videos";
    private static final String REVIEWS_PARAM = "reviews";

    /**
     * Returns URL used to query themoviedb.com based on selected sort option
     */
    public static URL buildUrl(String sortByQuery) {
        Uri builtUri = Uri.parse(MOVIE_DATABASE_URL).buildUpon()
                .appendPath(sortByQuery)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * Returns URL used to query trailers for a specific movie on themoviedb.com
     */
    public static URL buildVideoUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIE_DATABASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(VIDEOS_PARAM)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * Returns URL used to query review for a specific movie on themoviedb.com
     */
    public static URL buildReviewUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIE_DATABASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(REVIEWS_PARAM)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //Log.v(TAG, "Built URI " + url);

        return url;

    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
