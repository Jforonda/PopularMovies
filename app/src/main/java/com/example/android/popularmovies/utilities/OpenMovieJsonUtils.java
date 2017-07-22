package com.example.android.popularmovies.utilities;

import android.content.Context;

import com.example.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OpenMovieJsonUtils {

    public static Movie[] getMovieListFromJson(Context context, String movieJsonStr)
        throws JSONException, IOException {

        final String RESULTS_LIST = "results";
        final String ORIGINAL_TITLE = "original_title";
        final String ID = "id";
        final String RELEASE_DATE = "release_date";
        final String PLOT_SYNOPSIS = "overview";
        final String USER_RATING = "vote_average";
        final String POSTER_PATH = "poster_path";

        Movie[] parsedMovieData;

        JSONObject movieListJson = new JSONObject(movieJsonStr);
        JSONArray movieListArray = movieListJson.getJSONArray(RESULTS_LIST);

        parsedMovieData = new Movie[movieListArray.length()];

        for (int i = 0; i < movieListArray.length(); i++) {
            JSONObject movieDetails = movieListArray.getJSONObject(i);

            // Individual movie ID
            String movieId = movieDetails.getString(ID);

            // Retrieve data for movie trailers
            URL trailerRequestUrl = NetworkUtils.buildVideoUrl(movieId);
            String jsonTrailerResponse = NetworkUtils
                    .getResponseFromHttpUrl(trailerRequestUrl);
            ArrayList<String> trailerJsonData = OpenMovieJsonUtils
                    .getVideoListFromJson(context, jsonTrailerResponse);

            // Retrieve data for movie reviews
            URL reviewRequestUrl = NetworkUtils.buildReviewUrl(movieId);
            String jsonReviewResponse = NetworkUtils
                    .getResponseFromHttpUrl(reviewRequestUrl);
            ArrayList<String> reviewTextData = OpenMovieJsonUtils
                    .getReviewTextListFromJson(context, jsonReviewResponse);
            ArrayList<String> reviewAuthorData = OpenMovieJsonUtils
                    .getReviewAuthorListFromJson(context, jsonReviewResponse);



            Movie tempMovie = new Movie(movieDetails.getString(ORIGINAL_TITLE),
                    movieDetails.getString(ID),
                    movieDetails.getString(RELEASE_DATE),
                    movieDetails.getString(PLOT_SYNOPSIS),
                    movieDetails.getString(USER_RATING),
                    movieDetails.getString(POSTER_PATH),
                    trailerJsonData,
                    reviewTextData,
                    reviewAuthorData);

            parsedMovieData[i] = tempMovie;
        }
        return parsedMovieData;
    }

    public static ArrayList<String> getVideoListFromJson(Context context, String trailerJsonStr)
        throws JSONException {

        final String RESULTS_LIST = "results";
        final String KEY = "key";
        final String TYPE = "type";
        final String TYPE_TRAILER = "Trailer";

        ArrayList<String> parsedVideoDataList = new ArrayList<>();

        JSONObject videoListJson = new JSONObject(trailerJsonStr);
        JSONArray videoListArray = videoListJson.getJSONArray(RESULTS_LIST);

        for (int i = 0; i < videoListArray.length(); i++) {
            JSONObject videoDetails = videoListArray.getJSONObject(i);
            if (videoDetails.getString(TYPE).contains(TYPE_TRAILER)) {
                parsedVideoDataList.add(videoDetails.getString(KEY));
            }
        }

        return parsedVideoDataList;
    }

    public static ArrayList<String> getReviewTextListFromJson(Context context, String reviewJsonStr)
        throws JSONException {

        final String RESULTS_LIST = "results";
        final String CONTENT = "content";

        ArrayList<String> parsedReviewTextDataList = new ArrayList<>();

        JSONObject reviewTextListJson = new JSONObject(reviewJsonStr);
        JSONArray reviewTextListArray = reviewTextListJson.getJSONArray(RESULTS_LIST);

        for (int i = 0; i < reviewTextListArray.length(); i++) {
            JSONObject reviewText = reviewTextListArray.getJSONObject(i);
            parsedReviewTextDataList.add(reviewText.getString(CONTENT));
        }

        return parsedReviewTextDataList;
    }

    public static ArrayList<String> getReviewAuthorListFromJson(Context context, String reviewJsonStr)
            throws JSONException {

        final String RESULTS_LIST = "results";
        final String AUTHOR = "author";

        ArrayList<String> parsedReviewAuthorDataList = new ArrayList<>();

        JSONObject reviewAuthorListJson = new JSONObject(reviewJsonStr);
        JSONArray reviewAuthorListArray = reviewAuthorListJson.getJSONArray(RESULTS_LIST);

        for (int i = 0; i < reviewAuthorListArray.length(); i++) {
            JSONObject reviewText = reviewAuthorListArray.getJSONObject(i);
            parsedReviewAuthorDataList.add(reviewText.getString(AUTHOR));
        }

        return parsedReviewAuthorDataList;

    }
}
