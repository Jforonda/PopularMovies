package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String POSTER_PATH_URL =
            "http://image.tmdb.org/t/p/w154";

    private String mMovieDetail;
    private String mMovieTitle;
    private String mMoviePosterPath;
    private String mMovieReleaseDate;
    private String mMovieUserRating;
    private String mMoviePlotSynopsis;
    private String mMovieId;
    private ArrayList<String> mMovieTrailerPath;
    private ArrayList<String> mMovieReviewText;
    private ArrayList<String> mMovieReviewAuthor;

    private ListView mMovieDetailListView;

    private Movie movieDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(getString(R.string.title))) {
                mMovieTitle = intentThatStartedThisActivity.getStringExtra(getString(R.string.title));
            }
            if (intentThatStartedThisActivity.hasExtra(getString(R.string.posterPath))) {
                mMoviePosterPath = intentThatStartedThisActivity.getStringExtra(getString(R.string.posterPath));
            }
            if (intentThatStartedThisActivity.hasExtra(getString(R.string.releaseDate))) {
                mMovieReleaseDate = intentThatStartedThisActivity.getStringExtra(getString(R.string.releaseDate));
            }
            if (intentThatStartedThisActivity.hasExtra(getString(R.string.userRating))) {
                mMovieUserRating = intentThatStartedThisActivity.getStringExtra(getString(R.string.userRating));
            }
            if (intentThatStartedThisActivity.hasExtra(getString(R.string.plotSynopsis))) {
                mMoviePlotSynopsis = intentThatStartedThisActivity.getStringExtra(getString(R.string.plotSynopsis));
            }
            if (intentThatStartedThisActivity.hasExtra(getString(R.string.id))) {
                mMovieId = intentThatStartedThisActivity.getStringExtra(getString(R.string.id));
            }
            if (intentThatStartedThisActivity.hasExtra(getString(R.string.trailerPath))){
                mMovieTrailerPath = intentThatStartedThisActivity.getStringArrayListExtra(getString(R.string.trailerPath));
            }
            if (intentThatStartedThisActivity.hasExtra(getString(R.string.reviewText))){
                mMovieReviewText = intentThatStartedThisActivity.getStringArrayListExtra(getString(R.string.reviewText));
            }
            if (intentThatStartedThisActivity.hasExtra(getString(R.string.reviewAuthor))){
                mMovieReviewAuthor = intentThatStartedThisActivity.getStringArrayListExtra(getString(R.string.reviewAuthor));
            }
        }
        movieDetail = new Movie(mMovieTitle, mMovieId, mMovieReleaseDate, mMoviePlotSynopsis,
                mMovieUserRating, mMoviePosterPath, mMovieTrailerPath, mMovieReviewText,
                mMovieReviewAuthor);

        MovieDetailAdapter movieDetailAdapter = new MovieDetailAdapter(this, movieDetail, mMovieTrailerPath,
                mMovieReviewText, mMovieReviewAuthor);
        mMovieDetailListView = (ListView) findViewById(R.id.lv_movie_detail_list);
        mMovieDetailListView.setAdapter(movieDetailAdapter);
    }
}
