package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteMovieAdapter extends ArrayAdapter<Movie> {
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private static final String POSTER_PATH_URL =
            "http://image.tmdb.org/t/p/w154";

    private Context context;
    private Movie[] mMovieData;

    public FavoriteMovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
        this.context = context;
        mMovieData = new Movie[movies.size()];
        for (int i = 0; i < movies.size(); i++) {
            mMovieData[i] = getItem(i);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Movie movie = mMovieData[position];

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_list_item, parent, false);
        }
        ImageView moviePosterImageView = (ImageView) convertView.findViewById(R.id.movie_poster);
        Picasso.with(context)
                .load(POSTER_PATH_URL + movie.posterPath)
                .into(moviePosterImageView);

        moviePosterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class destinationClass = MovieDetailActivity.class;
                Intent intentToStartMovieDetailFragment = new Intent(context, destinationClass);
                intentToStartMovieDetailFragment.putExtra(context.getString(R.string.title), movie.title);
                intentToStartMovieDetailFragment.putExtra(context.getString(R.string.userRating), movie.userRating);
                intentToStartMovieDetailFragment.putExtra(context.getString(R.string.releaseDate), movie.releaseDate);
                intentToStartMovieDetailFragment.putExtra(context.getString(R.string.plotSynopsis), movie.plotSynopsis);
                intentToStartMovieDetailFragment.putExtra(context.getString(R.string.id), movie.id);
                intentToStartMovieDetailFragment.putExtra(context.getString(R.string.posterPath), movie.posterPath);
                intentToStartMovieDetailFragment.putStringArrayListExtra(context.getString(R.string.trailerPath), movie.trailerPathArray);
                intentToStartMovieDetailFragment.putStringArrayListExtra(context.getString(R.string.reviewText), movie.reviewTextArray);
                intentToStartMovieDetailFragment.putStringArrayListExtra(context.getString(R.string.reviewAuthor), movie.reviewAuthorArray);
                context.startActivity(intentToStartMovieDetailFragment);
            }
        });

        return convertView;
    }

    public void setMovieData(Movie[] movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }
}
