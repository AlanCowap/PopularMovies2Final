package com.example.android.popularmovies.Loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utils.MovieJsonUtilities;
import com.example.android.popularmovies.Utils.NetworkUtilities;

import java.net.URL;

/**
 * Created by alex.fanning on 29/06/2017.
 */

public class ReviewLoader extends AsyncTaskLoader<String[]> {

    public static final int MOVIE_LOADER_ID = 21;
    private static final String TAG = ReviewLoader.class.getSimpleName();
    private Context c;
    private String movieID;

    public ReviewLoader(Context c) {
        super(c);
    }

    public ReviewLoader(Context _c, String _movieID) {
        super(_c);
        c = _c;
        movieID = _movieID;
    }

    @Override
    public String[] loadInBackground() {
        String[] reviews = null;
        if (movieID == null) {
            return null;
        }
        URL movieReviewUrl = NetworkUtilities.buildUrlVideoOrReview(c.getString(R.string.reviews_key), movieID, c);
        try {
            String reviewsJsonResponse = NetworkUtilities.getJSONresponseFromUrl(movieReviewUrl);
            reviews = MovieJsonUtilities.getReviewsFromJson(c, reviewsJsonResponse);
            return reviews;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
