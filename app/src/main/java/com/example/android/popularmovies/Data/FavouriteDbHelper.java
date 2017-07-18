package com.example.android.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alex.fanning on 15/06/2017.
 */

public class FavouriteDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";
    private static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";

    private static final int DATABASE_VERSION = 1;

    public FavouriteDbHelper(Context c){
        super(c,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVOURITE_TABLE =
                "CREATE TABLE " + FavouriteContract.FavouriteEntry.TABLE_NAME + " (" +
                        FavouriteContract.FavouriteEntry._ID                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavouriteContract.FavouriteEntry.COLUMN_MOV_ID      + " INTEGER NOT NULL, " +
                        FavouriteContract.FavouriteEntry.COLUMN_TITLE       + " TEXT NOT NULL, " +
                        FavouriteContract.FavouriteEntry.COLUMN_SYNOPSIS    + " TEXT NOT NULL, "  +
                        FavouriteContract.FavouriteEntry.COLUMN_USER_RATING + " REAL NOT NULL, " +
                        FavouriteContract.FavouriteEntry.COLUMN_REL_DATE    + " TEXT NOT NULL, " +
                        FavouriteContract.FavouriteEntry.COLUMN_POSTER_PATH    + " TEXT NOT NULL, " +
                        FavouriteContract.FavouriteEntry.COLUMN_POSTER_PATH_LOCAL + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_FAVOURITE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_IF_EXISTS + FavouriteContract.FavouriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
