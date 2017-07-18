package com.example.android.popularmovies.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by alex.fanning on 14/06/2017.
 */

public class FavouriteContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVOURITE = "favourite";

    public static final class FavouriteEntry implements BaseColumns{


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE).build();


        public static final String TABLE_NAME = "favourites";

        public static final String COLUMN_MOV_ID = "movID";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_REL_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POSTER_PATH_LOCAL = "poster_path_local";
        public static Uri buildFavouriteUriWithID(int id){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }


    }

}
