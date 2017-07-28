package com.example.android.popularmovies.Loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utils.MovieJsonUtilities;
import com.example.android.popularmovies.Utils.NetworkUtilities;
import com.example.android.popularmovies.Data.FavouriteContract;

import java.net.URL;

/**
 * Created by alex.fanning on 28/06/2017.
 */

public class MovieLoader extends AsyncTaskLoader<Movie[]> {
    //TODO AWESOME  This modular design helps code reuse and keeps your project cleaner and easier to maintain
    public static final int MOVIE_LOADER_ID = 20;
    private static final String TAG = MovieLoader.class.getSimpleName();
    private Context c;
    private Movie[] movies = null;
    private String selectedMenuKey;

    public MovieLoader(Context c) {
        super(c);
    }

    public MovieLoader(Context _c, String _selectedMenuKey) {
        super(_c);
        c = _c;
        selectedMenuKey = _selectedMenuKey;
    }

    @Override
    public Movie[] loadInBackground() {
        if (selectedMenuKey == null){return null;}
        if (selectedMenuKey.equals(c.getString(R.string.menu_favourite))){
            return populateFavourites();
        }
        else{
            return populateNonFavourites(selectedMenuKey);
        }
    }

    private Movie[] populateFavourites(){
        //TODO AWESOME Leveraging your Content Provider is the way to go.
        Cursor movCurosr = null;
        try{
            movCurosr = c.getContentResolver().query(FavouriteContract.FavouriteEntry.CONTENT_URI,
                    null, null,null,null);
        }catch(Exception e){
            Log.e(TAG, c.getString(R.string.failed_loading_error) +(e.getMessage()));
            return null;
        }
        int INDEX_MOVIE_ID = movCurosr.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_MOV_ID);
        int INDEX_TITLE = movCurosr.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_TITLE);
        int INDEX_SYNOP = movCurosr.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_SYNOPSIS);
        int INDEX_USER_RATING = movCurosr.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_USER_RATING);
        int INDEX_DATE = movCurosr.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_REL_DATE);
        int INDEX_POSTER_PATH = movCurosr.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_POSTER_PATH);
        int INDEX_POSTER_PATH_LOCAL = movCurosr.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_POSTER_PATH_LOCAL);


        Movie[] movs = new Movie[movCurosr.getCount()];
        //TODO SUGGESTION Be mindful that some reviewers may not approve of commented-out code.
        int i = 0;
        try{
            while(movCurosr.moveToNext()){
                int movieId = movCurosr.getInt(INDEX_MOVIE_ID);
                String movTitle = movCurosr.getString(INDEX_TITLE);
                String synop = movCurosr.getString(INDEX_SYNOP);
                double userRating = movCurosr.getDouble(INDEX_USER_RATING);
                String date = movCurosr.getString(INDEX_DATE);
                String pp = movCurosr.getString(INDEX_POSTER_PATH);
                String ppl = movCurosr.getString(INDEX_POSTER_PATH_LOCAL);
                Movie m = new Movie(movieId,movTitle,synop,userRating,date,pp,ppl);
                movs[i] = m;
                i++;
            }
        }finally {
            movCurosr.close();
        }

        return movs;
    }

    private Movie[] populateNonFavourites(String selectedItem){
        Movie[] movs = null;

        URL movieRequestURL = NetworkUtilities.buildUrl(selectedItem,c);
        Log.d(TAG, c.getString(R.string.do_in_bground)+movieRequestURL.toString());
        try {
            String movieJSONresponse = NetworkUtilities.getJSONresponseFromUrl(movieRequestURL);
            Log.d(TAG, c.getString(R.string.get_json_response) + movieJSONresponse);
            movs = MovieJsonUtilities.getMoviesFromJSON(c,movieJSONresponse);
            return movs;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
