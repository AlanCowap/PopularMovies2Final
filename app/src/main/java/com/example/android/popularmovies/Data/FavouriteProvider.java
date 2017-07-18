package com.example.android.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by alex.fanning on 15/06/2017.
 */

public class FavouriteProvider extends ContentProvider {

    private static final String TAG = FavouriteProvider.class.getSimpleName();

    public static final int FAVOURITES = 100;
    public static final int FAVOURITES_WITH_ID = 101;

    private static final String HASH_PATH = "/#";
    private static final String UNKOWN_URI = "Unknown Uri:";
    private static final String NOT_YET_IMPLEMENT_EX = "Not yet implemented:";
    private static final String FAILED_TO_INSERT_EX = "Failed to insert row :";
    private static final String MOVIE_SELECTION_ID = "movID=?";
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavouriteContract.CONTENT_AUTHORITY, FavouriteContract.PATH_FAVOURITE, FAVOURITES);
        uriMatcher.addURI(FavouriteContract.CONTENT_AUTHORITY,FavouriteContract.PATH_FAVOURITE + HASH_PATH, FAVOURITES_WITH_ID);

        return uriMatcher;
    }

    private FavouriteDbHelper mFavouriteDbHelper;

    @Override
    public boolean onCreate() {
        mFavouriteDbHelper = new FavouriteDbHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFavouriteDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)){
            case FAVOURITES:

                long id = db.insert(FavouriteContract.FavouriteEntry.TABLE_NAME,null,values);
                if (id > 0){
                    returnUri = ContentUris.withAppendedId(FavouriteContract.FavouriteEntry.CONTENT_URI,id);
                }else{
                    throw new SQLException(FAILED_TO_INSERT_EX + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException(UNKOWN_URI + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }


    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mFavouriteDbHelper.getReadableDatabase();

        Cursor retCursor;
        switch (sUriMatcher.match(uri)){

            case FAVOURITES:
                retCursor = db.query(FavouriteContract.FavouriteEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case FAVOURITES_WITH_ID:
                String id = uri.getLastPathSegment();
                String[] mselectionArgs = new String[]{id};
                retCursor = db.query(FavouriteContract.FavouriteEntry.TABLE_NAME,projection,MOVIE_SELECTION_ID,mselectionArgs,null,null,sortOrder);
                break;
            default :
                throw new UnsupportedOperationException(UNKOWN_URI + uri);
        }

        return retCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFavouriteDbHelper.getWritableDatabase();

        int tasksDeleted;

        switch (sUriMatcher.match(uri)){
            case FAVOURITES_WITH_ID:
                String id = uri.getLastPathSegment();
                tasksDeleted = db.delete(FavouriteContract.FavouriteEntry.TABLE_NAME, MOVIE_SELECTION_ID, new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException(UNKOWN_URI + uri);
        }

        if (tasksDeleted != 0)
            getContext().getContentResolver().notifyChange(uri,null);

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException(NOT_YET_IMPLEMENT_EX);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException(NOT_YET_IMPLEMENT_EX);
    }
}
