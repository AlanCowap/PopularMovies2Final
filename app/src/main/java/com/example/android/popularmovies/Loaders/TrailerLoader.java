package com.example.android.popularmovies.Loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utils.MovieJsonUtilities;
import com.example.android.popularmovies.Utils.NetworkUtilities;

import java.net.URL;

/**
 * Created by alex.fanning on 27-Jun-17.
 */
//TODO SUGGESTION This is one of at least three classes that "User" has Created, be mindful this give the impression the work is not yours.

public class TrailerLoader extends AsyncTaskLoader<String[]> {
    public static final int MOVIE_LOADER_ID = 22;
    private static final String TAG = TrailerLoader.class.getSimpleName();
    private Context c;
    private String movieID;

    public TrailerLoader(Context c) {
        super(c);
    }

    public TrailerLoader(Context _c, String _movieID) {
        super(_c);
        c = _c;
        movieID = _movieID;
    }

    @Override
    public String[] loadInBackground() {
        String[] trailers = null;
        if (movieID == null) {
            return null;
        }
        URL movieVideoUrl = NetworkUtilities.buildUrlVideoOrReview(c.getString(R.string.videos_key), movieID, c);
        try {
            String videosJsonResponse = NetworkUtilities.getJSONresponseFromUrl(movieVideoUrl);
            trailers = MovieJsonUtilities.getTrailersFromJson(c, videosJsonResponse);
            return trailers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
