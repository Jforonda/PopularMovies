package com.example.android.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoriteMovieContract;
import com.example.android.popularmovies.data.ReviewsContract;
import com.example.android.popularmovies.data.TrailersContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetailAdapter extends BaseAdapter {
    private static final String TAG = MovieDetailAdapter.class.getSimpleName();

    private static final String POSTER_PATH_URL =
            "http://image.tmdb.org/t/p/w154";
    private final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    private final String TRAILER_TEXT = "Trailer #";

    private static final int VIEW_TYPE_COUNT = 3;
    private static final int VIEW_TYPE_DETAIL = 0;
    private static final int VIEW_TYPE_TRAILER = 1;
    private static final int VIEW_TYPE_REVIEW = 2;

    private Context context;
    private Movie movieDetails;
    private ArrayList<String> trailers;
    private ArrayList<String> reviews;
    private ArrayList<String> authors;

    private TextView titleTextView;
    private ImageView posterImageView;
    private TextView releaseDateTextView;
    private TextView userRatingTextView;
    private TextView plotSynopsisTextView;
    private ImageView favoriteIconImageView;

    public MovieDetailAdapter(Activity context, Movie movieDetails, ArrayList<String> trailers,
                              ArrayList<String> reviews, ArrayList<String> authors) {
        this.movieDetails = movieDetails;
        this.trailers = trailers;
        this.reviews = reviews;
        this.authors = authors;
        this.context = context;
    }

    @Override
    public int getCount(){
        int count = 1;
        if (trailers != null) {
            if (trailers.size() != 0) {
                count += trailers.size();
            }
        }
        if (reviews != null) {
            if (reviews.size() != 0) {
                count += reviews.size();
            }
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return movieDetails;
        } else {
            return movieDetails;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getItemTypeCount(){
        return VIEW_TYPE_COUNT;
    }

    public int getItemType(int position){
        if (position == 0) {
            return VIEW_TYPE_DETAIL;
        }
        if (hasTrailers()) {
            if ((position > 0) && (position <= trailers.size())) {
                return VIEW_TYPE_TRAILER;
            }
        }
        if (hasReviews()) {
            if ((position > trailers.size()) && (position <= trailers.size() + reviews.size())) {
                return VIEW_TYPE_REVIEW;
            }
        }
        return -1;
    }

    public boolean hasTrailers() {
        return (trailers.size() > 0);
    }

    public boolean hasReviews() {
        return (reviews.size() > 0);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater.from(context));
        View v = convertView;
        int type = getItemType(position);

        switch (type) {
            case VIEW_TYPE_DETAIL:
                v = inflater.inflate(R.layout.movie_details_list_item, null);
                // MOVIEDETAILS START
                final Movie currentMovie = (Movie) getItem(position);
                titleTextView = (TextView) v.findViewById(R.id.tv_display_movie_title);
                posterImageView = (ImageView) v.findViewById(R.id.iv_display_movie_poster);
                releaseDateTextView = (TextView) v.findViewById(R.id.tv_display_movie_release_date);
                userRatingTextView = (TextView) v.findViewById(R.id.tv_display_movie_user_rating);
                plotSynopsisTextView = (TextView) v.findViewById(R.id.tv_display_movie_plot);

                favoriteIconImageView = (ImageView) v.findViewById(R.id.iv_display_favorite_icon);
                boolean checkIfFavorite = checkIsFavorite(currentMovie);
                if (checkIfFavorite) {
                    favoriteIconImageView.setImageResource(R.drawable.ic_favorite_check);
                } else {
                    favoriteIconImageView.setImageResource(R.drawable.ic_favorite_uncheck);
                }

                favoriteIconImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkIsFavorite(currentMovie)) {
                            removeFromFavorites(currentMovie);
                        } else {
                            addToFavorites(currentMovie);
                        }
                    }
                });

                titleTextView.setText(currentMovie.title);
                Picasso.with(context)
                        .load(POSTER_PATH_URL + currentMovie.posterPath)
                        .into(posterImageView);
                releaseDateTextView.setText(currentMovie.releaseDate);
                userRatingTextView.setText(context.getString(R.string.rating) +
                        currentMovie.userRating + context.getString(R.string.rating_ten));
                plotSynopsisTextView.setText(currentMovie.plotSynopsis);
                // MOVIEDETAILS END
                break;
            case VIEW_TYPE_TRAILER:
                v = inflater.inflate(R.layout.trailer_list_item, null);
                // TRAILER START
                final String trailer = trailers.get(position-1);
                TextView trailerTextView = (TextView) v.findViewById(R.id.trailer_item);
                trailerTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(YOUTUBE_URL + trailer));
                        context.startActivity(i);
                    }
                });
                ImageView trailerIcon = (ImageView) v.findViewById(R.id.iv_display_trailer_icon);
                trailerIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(YOUTUBE_URL + trailer));
                        context.startActivity(i);
                    }
                });
                trailerTextView.setText(TRAILER_TEXT + position);
                // TRAILER END
                break;
            case VIEW_TYPE_REVIEW:
                v = inflater.inflate(R.layout.review_list_item, null);
                // REVIEW START
                final String reviewText = reviews.get(position-trailers.size()-1);
                final String reviewAuthor = authors.get(position-trailers.size()-1);
                TextView reviewTextTextView = (TextView) v.findViewById(R.id.review_text_item);
                TextView reviewAuthorTextView = (TextView) v.findViewById(R.id.review_author_item);
                reviewTextTextView.setText(reviewText);
                reviewAuthorTextView.setText(context.getString(R.string.review_written_by) + reviewAuthor);
                // REVIEW END
                break;
        }

        return v;
    }


    public boolean checkIsFavorite(Movie movie) {
        Uri uri = FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movie.id).build();
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        return (c.getCount() > 0);
    }

    public void addToFavorites(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITLE,
                movie.title);
        contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID,
                movie.id);
        contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_PLOT_SYNOPSIS,
                movie.plotSynopsis);
        contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSER_PATH,
                movie.posterPath);
        contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE,
                movie.releaseDate);
        contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_USER_RATING,
                movie.userRating);
        Uri favoriteMovieUri = context.getContentResolver()
                .insert(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI, contentValues);
        if (favoriteMovieUri != null) {
            Toast.makeText(context, movie.title + context.getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show();
        }

        if (movie.trailerPathArray != null) {
            if (movie.trailerPathArray.size() > 0) {
                for (int i = 0; i < movie.trailerPathArray.size(); i++) {
                    ContentValues trailersContentValues = new ContentValues();
                    trailersContentValues.put(TrailersContract.TrailerEntry.COLUMN_ID, movie.id);
                    trailersContentValues.put(TrailersContract.TrailerEntry.COLUMN_TRAILER_PATH,
                            movie.trailerPathArray.get(i));
                    Uri trailerUri = context.getContentResolver().insert(TrailersContract.TrailerEntry.CONTENT_URI,
                            trailersContentValues);
                }
            }
        }

        if (movie.reviewAuthorArray != null) {
            if (movie.reviewAuthorArray.size() > 0) {
                for (int i = 0; i < movie.reviewAuthorArray.size(); i++) {
                    ContentValues reviewsContentValues = new ContentValues();
                    reviewsContentValues.put(ReviewsContract.ReviewEntry.COLUMN_ID, movie.id);
                    reviewsContentValues.put(ReviewsContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
                            movie.reviewAuthorArray.get(i));
                    reviewsContentValues.put(ReviewsContract.ReviewEntry.COLUMN_REVIEW_TEXT,
                            movie.reviewTextArray.get(i));
                    Uri reviewUri = context.getContentResolver().insert(ReviewsContract.ReviewEntry.CONTENT_URI,
                            reviewsContentValues);
                }
            }
        }

        favoriteIconImageView.setImageResource(R.drawable.ic_favorite_check);
    }

    public void removeFromFavorites(Movie movie) {
        Uri favoriteMovieUri = FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI;
        favoriteMovieUri = favoriteMovieUri.buildUpon().appendPath(movie.id).build();

        context.getContentResolver().delete(favoriteMovieUri, null, null);
        if (favoriteMovieUri != null) {
            Toast.makeText(context, movie.title + context.getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show();
        }

        Uri trailerUri = TrailersContract.TrailerEntry.CONTENT_URI;
        trailerUri = trailerUri.buildUpon().appendPath(movie.id).build();

        context.getContentResolver().delete(trailerUri, null, null);

        Uri reviewUri = ReviewsContract.ReviewEntry.CONTENT_URI;
        reviewUri = reviewUri.buildUpon().appendPath(movie.id).build();

        context.getContentResolver().delete(reviewUri, null, null);

        favoriteIconImageView.setImageResource(R.drawable.ic_favorite_uncheck);
    }
}
