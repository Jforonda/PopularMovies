package com.example.android.popularmovies;

import java.util.ArrayList;

public class Movie {
    String title;
    String id;
    String releaseDate;
    String plotSynopsis;
    String userRating;
    String posterPath = "";
    ArrayList<String> trailerPathArray;
    ArrayList<String> reviewTextArray;
    ArrayList<String> reviewAuthorArray;

    public Movie(String title, String id, String releaseDate, String plotSynopsis,
                 String userRating, String posterPath) {
        this.title = title;
        this.id = id;
        this.releaseDate = releaseDate;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.posterPath = posterPath;
    }

    public Movie(String title, String id, String releaseDate, String plotSynopsis,
                 String userRating, String posterPath, ArrayList<String> trailerPathArray,
                 ArrayList<String> reviewTextArray, ArrayList<String> reviewAuthorArray) {
        this.title = title;
        this.id = id;
        this.releaseDate = releaseDate;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.posterPath = posterPath;
        this.trailerPathArray = trailerPathArray;
        this.reviewTextArray = reviewTextArray;
        this.reviewAuthorArray = reviewAuthorArray;
    }

    public Movie(String posterPath) {
        this.posterPath = posterPath;
    }

    public Movie() {

    }
}
