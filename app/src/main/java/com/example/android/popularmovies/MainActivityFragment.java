package com.example.android.popularmovies;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.popularmovies.data.FavoriteMovieContentProvider;
import com.example.android.popularmovies.data.FavoriteMovieContract;
import com.example.android.popularmovies.data.ReviewsContract;
import com.example.android.popularmovies.data.TrailersContract;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.OpenMovieJsonUtils;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();

    private static final String SORT_BY_POPULAR = "popular";
    private static final String SORT_BY_HIGHEST_RATED = "top_rated";

    private MovieAdapter mMovieAdapter;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessage;
    private TextView mNoFavoritesMessage;
    private GridView mGridView;

    private String sortMovieListBy = SORT_BY_POPULAR;
    private final String[] sortOptions = {"Popular", "Top Rated", "Favorites"};

    private int mMovieListLength = 20;

    private Movie[] movieList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.pb_loading_indicator);
        mErrorMessage = (TextView) rootView.findViewById(R.id.tv_display_error_message);
        mNoFavoritesMessage = (TextView) rootView.findViewById(R.id.tv_display_no_favorites_message);
        mGridView = (GridView) rootView.findViewById(R.id.movies_grid);

        movieList = new Movie[mMovieListLength];
        for (int i = 0; i < mMovieListLength; i++) {
            Movie movie = new Movie();
            movieList[i] = movie;
        }
        mMovieAdapter = new MovieAdapter(getActivity(), Arrays.asList(movieList));
        mGridView.setAdapter(mMovieAdapter);

        loadMovieData();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sort, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.action_sort);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_dropdown_item_1line,
                sortOptions);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    sortMovieListBy = SORT_BY_POPULAR;
                    Log.v(TAG, getString(R.string.sort_by_most_popular));
                    loadMovieData();
                }
                if (position == 1) {
                    sortMovieListBy = SORT_BY_HIGHEST_RATED;
                    Log.v(TAG, getString(R.string.sort_by_highest_rated));
                    loadMovieData();
                }
                if (position == 2) {
                    Log.v(TAG, getString(R.string.sort_by_favorites));

                    //ADD-START
                    Uri uri = FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI;
                    uri = uri.buildUpon().build();
                    Cursor c = getActivity().getContentResolver().query(uri, null, null, null, null);
                    //ADD-END
                    if (c.getCount() == 0) {
                        showNoFavoritesMessage();
                    } else {
                        loadFavoriteMovieData();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadMovieData() {
        new FetchMovieTask().execute(sortMovieListBy);
    }

    private void loadFavoriteMovieData() {
        new FetchFavoriteMovieTask().execute();
    }

    private void showLoadingIndicator() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.VISIBLE);
    }

    private void showMovieData() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mNoFavoritesMessage.setVisibility(View.INVISIBLE);
        mGridView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mNoFavoritesMessage.setVisibility(View.INVISIBLE);
        mGridView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        mErrorMessage.setVisibility(View.INVISIBLE);
    }

    private void showNoFavoritesMessage() {
        mGridView.setVisibility(View.INVISIBLE);
        mNoFavoritesMessage.setVisibility(View.VISIBLE);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hideErrorMessage();
            showLoadingIndicator();
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String sortBy = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(sortBy);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);
                Movie[] movieJsonData = OpenMovieJsonUtils
                        .getMovieListFromJson(getActivity(), jsonMovieResponse);
                return movieJsonData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                showMovieData();
                mMovieAdapter = new MovieAdapter(getActivity(), Arrays.asList(movies));
                mGridView.setAdapter(mMovieAdapter);
                mMovieAdapter.setMovieData(movies);
            } else {
                showErrorMessage();
            }
        }
    }

    public class FetchFavoriteMovieTask extends AsyncTask<Void, Void, Movie[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hideErrorMessage();
            showLoadingIndicator();
        }

        @Override
        protected Movie[] doInBackground(Void... params) {
            try {
                Uri uri = FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI;
                uri = uri.buildUpon().build();
                Cursor c = getActivity().getContentResolver().query(uri, null, null, null, null);
                Movie[] favoriteMovies;
                c.moveToFirst();
                ArrayList<Movie> movies = new ArrayList<>();
                while(!c.isAfterLast()) {
                    Movie tempMovie = new Movie();
                    tempMovie.title = c.getString(c.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITLE));
                    tempMovie.id = c.getString(c.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID));
                    tempMovie.plotSynopsis = c.getString(c.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_PLOT_SYNOPSIS));
                    tempMovie.posterPath = c.getString(c.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSER_PATH));
                    tempMovie.releaseDate = c.getString(c.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE));
                    tempMovie.userRating = c.getString(c.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_USER_RATING));
                    // Add Trailer
                    ArrayList<String> trailers = new ArrayList<>();
                    Uri trailerUri = TrailersContract.TrailerEntry.CONTENT_URI;
                    trailerUri = trailerUri.buildUpon().appendPath(tempMovie.id).build();
                    Cursor trailerCursor = getActivity().getContentResolver().query(trailerUri,
                            null, null, null, null);
                    trailerCursor.moveToFirst();
                    while(!trailerCursor.isAfterLast()) {
                        trailers.add(trailerCursor.getString(trailerCursor.getColumnIndex(TrailersContract.TrailerEntry.COLUMN_TRAILER_PATH)));
                        trailerCursor.moveToNext();
                    }
                    if (trailers != null) {
                        tempMovie.trailerPathArray = trailers;
                    }

                    // End Add Trailer
                    // Add Reviews
                    ArrayList<String> reviewsAuthors = new ArrayList<>();
                    ArrayList<String> reviewsText = new ArrayList<>();
                    Uri reviewUri = ReviewsContract.ReviewEntry.CONTENT_URI;
                    reviewUri = reviewUri.buildUpon().appendPath(tempMovie.id).build();
                    Cursor reviewCursor = getActivity().getContentResolver().query(reviewUri, null,
                            null, null, null);
                    reviewCursor.moveToFirst();
                    while(!reviewCursor.isAfterLast()) {
                        reviewsAuthors.add(reviewCursor.getString(reviewCursor.getColumnIndex(ReviewsContract.ReviewEntry.COLUMN_REVIEW_AUTHOR)));
                        reviewsText.add(reviewCursor.getString(reviewCursor.getColumnIndex(ReviewsContract.ReviewEntry.COLUMN_REVIEW_TEXT)));
                        reviewCursor.moveToNext();
                    }
                    if (reviewsAuthors != null) {
                        tempMovie.reviewAuthorArray = reviewsAuthors;
                        tempMovie.reviewTextArray = reviewsText;
                    }

                    // End add reviews
                    movies.add(tempMovie);
                    c.moveToNext();
                }
                c.close();
                favoriteMovies = movies.toArray(new Movie[movies.size()]);
                return favoriteMovies;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                showMovieData();
                mMovieAdapter = new MovieAdapter(getActivity(), Arrays.asList(movies));
                mGridView.setAdapter(mMovieAdapter);
                mMovieAdapter.setMovieData(movies);
            } else {
                showErrorMessage();
            }
        }
    }
}
