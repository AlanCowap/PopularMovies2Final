package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.Adapters.ReviewDataAdapter;
import com.example.android.popularmovies.Adapters.TrailerDataAdapter;
import com.example.android.popularmovies.Loaders.ReviewLoader;
import com.example.android.popularmovies.Loaders.TrailerLoader;
import com.example.android.popularmovies.Data.FavouriteContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

public class MovieDetailsActivity extends AppCompatActivity implements TrailerDataAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<String[]> {

    private static final String TAG =  MovieDetailsActivity.class.getSimpleName();

    private TextView mTvTitle;
    private ImageView mImgViewPoster;
    private TextView mPlotSynop;
    private TextView mUserRating;
    private TextView mRelDate;
    private CheckBox mChkBoxFave;
    private static Movie m;

    private RecyclerView mTrailerGrid;
    private TextView mTrailerTvError;
    private TrailerDataAdapter mTrailerDadapter;


    private ReviewDataAdapter mReviewDadapter;
    private TextView mReviewRvError;
    private RecyclerView mReviewGrid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ActionBar ab = this.getSupportActionBar();
        if (ab!= null)
            ab.setDisplayHomeAsUpEnabled(true);

        findViews();

        m = getIntent().getParcelableExtra(getString(R.string.mov_key));
        Log.d(TAG, new Object(){}.getClass().getEnclosingMethod().getName() + m.getOrigTitle());
        populateFields(m);
        mTrailerGrid.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mTrailerGrid.setHasFixedSize(false);
        DividerItemDecoration did = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        did.setDrawable(getDrawable(R.drawable.border_trailer));
        mTrailerGrid.addItemDecoration(did);

        mReviewGrid.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mReviewGrid.setHasFixedSize(true);

        getSupportLoaderManager().initLoader(TrailerLoader.MOVIE_LOADER_ID,null,this);
        getSupportLoaderManager().initLoader(ReviewLoader.MOVIE_LOADER_ID,null,this);
        setUpLoaders();




    }

    private void setUpLoaders(){
        LoaderManager lm = getSupportLoaderManager();
        Loader<String[]> trailerLoader = lm.getLoader(TrailerLoader.MOVIE_LOADER_ID);
        if (trailerLoader == null){
            lm.initLoader(TrailerLoader.MOVIE_LOADER_ID,null,this);
        }else{
            lm.restartLoader(TrailerLoader.MOVIE_LOADER_ID,null,this).forceLoad();
        }
        Loader<String[]> reviewLoader = lm.getLoader(ReviewLoader.MOVIE_LOADER_ID);
        if (reviewLoader == null){
            lm.initLoader(ReviewLoader.MOVIE_LOADER_ID,null,this);
        }else{
            lm.restartLoader(ReviewLoader.MOVIE_LOADER_ID,null,this).forceLoad();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavUtils.navigateUpFromSameTask(this);
        return super.onOptionsItemSelected(item);
    }

    private void findViews(){
        mTvTitle = (TextView) findViewById(R.id.movie_title);
        mImgViewPoster = (ImageView) findViewById(R.id.img_view_poster);
        mPlotSynop = (TextView) findViewById(R.id.tv_plot_synop);
        mUserRating = (TextView) findViewById(R.id.tv_user_rating);
        mRelDate= (TextView) findViewById(R.id.tv_rel_date);
        mChkBoxFave = (CheckBox) findViewById(R.id.chkbox_fav);
        mTrailerGrid = (RecyclerView) findViewById(R.id.rv_trailers);
        mTrailerTvError = (TextView) findViewById(R.id.rv_trailer_error);
        mReviewRvError = (TextView) findViewById(R.id.rv_review_error);
        mReviewGrid = (RecyclerView) findViewById(R.id.rv_reviews);
    }



    private void populateFields(Movie m){
        mTvTitle.setText(m.getOrigTitle());
        mPlotSynop.setText(m.getSynoposis());
        mUserRating.setText(String.valueOf(m.getUserRating()));
        mRelDate.setText(m.getReleaseDate());
        boolean isFave = checkIsMovieFave(m.getId());
        mChkBoxFave.setChecked(isFave);
        if (isFave){
            File f = new File(m.getPosterPathLocal());
            Picasso.with(this).load(f).into(mImgViewPoster);
        }else{
            Picasso.with(this).load(m.getPosterPath()).into(mImgViewPoster);
        }
    }

    private boolean checkIsMovieFave(int id){
        String strId = Integer.toString(id);
        Uri uri = FavouriteContract.FavouriteEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(strId).build();
        Cursor c = null;
        try{
            c = getContentResolver().query(uri, null, null,null,null);
            if (c.getCount() > 0){  //If movie is fave and local img path is not already set - it must be set
                setLocalPathFromCurosr(c);
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            Log.e(TAG, getString(R.string.failed_loading_error) + e.getMessage());
        }finally {
            c.close();
        }
        return false;
    }

    private void setLocalPathFromCurosr(Cursor c){
        c.moveToFirst();
        int INDEX_POSTER_PATH_LOCAL = c.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_POSTER_PATH_LOCAL);
        String ppl = c.getString(INDEX_POSTER_PATH_LOCAL);
        m.setPosterPathLocal(ppl);
    }

    public void chkbox_fave_Clicked(View v){
        boolean checked = ((CheckBox) v).isChecked();
        if (checked){
            saveImageLocally();
            ContentValues cv = new ContentValues();

            cv.put(FavouriteContract.FavouriteEntry.COLUMN_MOV_ID,m.getId());
            cv.put(FavouriteContract.FavouriteEntry.COLUMN_TITLE,m.getOrigTitle());
            cv.put(FavouriteContract.FavouriteEntry.COLUMN_SYNOPSIS,m.getSynoposis());
            cv.put(FavouriteContract.FavouriteEntry.COLUMN_USER_RATING,m.getUserRating());
            cv.put(FavouriteContract.FavouriteEntry.COLUMN_REL_DATE,m.getReleaseDate());
            cv.put(FavouriteContract.FavouriteEntry.COLUMN_POSTER_PATH,m.getPosterPath());
            cv.put(FavouriteContract.FavouriteEntry.COLUMN_POSTER_PATH_LOCAL,m.getPosterPathLocal());

            try{
                Uri uri = getContentResolver().insert(FavouriteContract.FavouriteEntry.CONTENT_URI,cv);
            }catch(Exception e){
                Log.e(TAG, e.getMessage() );
            }
        }else{
            deleteImgLocally();
            String stringId = Integer.toString(m.getId());
            Uri uri = FavouriteContract.FavouriteEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();
            getContentResolver().delete(uri,null,null);
        }
    }

    private void saveImageLocally(){

        Picasso.with(this).load(m.getPosterPath()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try{
                    //String root = Environment.getRootDirectory().toString();
                    ContextWrapper c = new ContextWrapper(getApplicationContext());
                   String root = c.getFilesDir().toString();
                    File myDir = new File(root + c.getString(R.string.fave_imgs_folder));
                    if (!myDir.exists()){
                        myDir.mkdirs();
                    }
                    String name = m.getOrigTitle() + c.getString(R.string.jpg_ext);
                    myDir = new File(myDir,name);
                    String imgPath = myDir.getPath();
                    FileOutputStream out = new FileOutputStream(myDir);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
                    out.flush();
                    out.close();
                    m.setPosterPathLocal(imgPath);
                }catch (Exception e){
                    Log.d(TAG, e.getMessage() );
                }
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    private void deleteImgLocally(){
        try{
            File file = new File(m.getPosterPathLocal());
            boolean deleted = file.delete();
        }catch(Exception e){
            Log.e(TAG, e.getMessage() );
        }



    }
    @Override
    public void onListItemClick(int clickedItemIndex, String trailerKey) {
           //Youtube intent
        watchYoutubeVideo(trailerKey);
    }

    private void watchYoutubeVideo(String id){
        //Code taken from https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
        Intent appIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(getString(R.string.youtube_app_intent) + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(getString(R.string.youtube_web_intent) + id));
        try{
            startActivity(appIntent);
        }catch(ActivityNotFoundException e){
            startActivity(webIntent);
        }
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        if (id == TrailerLoader.MOVIE_LOADER_ID){
            TrailerLoader tl = new TrailerLoader(this,Integer.toString(m.getId()));
            return tl;
        }else if(id == ReviewLoader.MOVIE_LOADER_ID){
            ReviewLoader rl = new ReviewLoader(this,Integer.toString(m.getId()));
            return rl;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        if (loader.getId() == TrailerLoader.MOVIE_LOADER_ID){
            displayTrailers(data);
        }else if(loader.getId() == ReviewLoader.MOVIE_LOADER_ID){
            displayReviews(data);
        }
    }

    private void displayTrailers(String[] trailers){
        if (trailers == null){
            //display error retrieving trailers
            showErrorHideRvtrailer();
            mTrailerTvError.setText(getString(R.string.trailer_error));
        }else if(trailers.length == 0){
            //There was no trailers
            showErrorHideRvtrailer();
            mTrailerTvError.setText(getString(R.string.no_trailers_error));
        }else{
            mTrailerDadapter = new TrailerDataAdapter(trailers,getBaseContext(),this);
            mTrailerGrid.setAdapter(mTrailerDadapter);
            showRvTrailerHideError();
        }
    }

    private void displayReviews(String[] reviews){
        if (reviews == null){
            //display error retrieving trailers
            showErrorHideRvReview();
            mReviewRvError.setText(getString(R.string.trailer_error));
        }else if(reviews.length == 0){
            //There was no trailers
            showErrorHideRvReview();
            mReviewRvError.setText(getString(R.string.no_reviews_error));
        }else{
            mReviewDadapter = new ReviewDataAdapter(reviews,getBaseContext());
            mReviewGrid.setAdapter(mReviewDadapter);
            showRvReviewHideError();
        }
    }

    private void showErrorHideRvReview(){
        mReviewGrid.setVisibility(View.INVISIBLE);
        mReviewRvError.setVisibility(View.VISIBLE);
    }
    private void showRvReviewHideError(){
        mReviewGrid.setVisibility(View.VISIBLE);
        mReviewRvError.setVisibility(View.INVISIBLE);
    }


    private void showErrorHideRvtrailer(){
        mTrailerGrid.setVisibility(View.INVISIBLE);
        mTrailerTvError.setVisibility(View.VISIBLE);
    }
    private void showRvTrailerHideError(){
        mTrailerGrid.setVisibility(View.VISIBLE);
        mTrailerTvError.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }
}
