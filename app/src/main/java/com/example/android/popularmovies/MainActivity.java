package com.example.android.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Adapters.MovieDataAdapter;
import com.example.android.popularmovies.Loaders.MovieLoader;

import static com.example.android.popularmovies.Loaders.MovieLoader.MOVIE_LOADER_ID;

public class MainActivity extends AppCompatActivity implements MovieDataAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<Movie[]>{


    private TextView mTextViewError;
    private ProgressBar mProgBar;
    private final static String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mMovieGrid;
    private MovieDataAdapter mMovieDadapter;
    private static final String MENU_TOP_RATED = "Top Rated";
    private static final String MENU_MOST_POPULAR ="Most Popular" ;
    private static final String MENU_FAVOURITE = "Favourites";
    private static String MENU_SELECTED;
    private static final int NUM_ROWS_VERTICAL = 2;
    private static final int NUM_ROWS_HORIZONTAL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        findInitialViews();
        //getConfigMethod taken from https://stackoverflow.com/questions/3663665/how-can-i-get-the-current-screen-orientation

       int numColumns = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? NUM_ROWS_VERTICAL : NUM_ROWS_HORIZONTAL;
        mMovieGrid.setLayoutManager(new GridLayoutManager(getApplicationContext(), numColumns));

        //TODO SUGGESTION It's a coding style preference so your choice, you could rewrite your code above as:
        //TODO SUGGESTION  int numColumns = (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        //TODO SUGGESTION       ? NUM_COLUMNS_PORTRAIT : NUM_COLUMNS_LANDSCAPE;
        //TODO SUGGESTION  mMovieGrid.setLayoutManager(new GridLayoutManager(getApplicationContext(), numColumns));

        mMovieGrid.setHasFixedSize(true);
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID,null,this);
        String menuState = getString(R.string.empty_string);
        if (savedInstanceState != null && savedInstanceState.containsKey(getString(R.string.selected_menu_item_key))){
            menuState = savedInstanceState.getString(getString(R.string.selected_menu_item_key));

        }else{
            menuState = getString(R.string.menu_popular);
            MENU_SELECTED = MENU_MOST_POPULAR;
        }
        loadMovieData(menuState);
    }
    //Use of menu Methods taken from Alex_Android_Notes topic 2.8
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String menuState = getString(R.string.empty_string);
        Bundle b = getIntent().getExtras();
        if (b != null && b.containsKey(getString(R.string.selected_menu_item_key))) {
            menuState = b.getString(getString(R.string.selected_menu_item_key));
        }
        if (menuState.equals(getString(R.string.menu_favourite))){
            loadMovieData(menuState);
        }
        

    }

    @Override
    protected void onStop() {
        super.onStop();
        getIntent().putExtra(getString(R.string.selected_menu_item_key),MENU_SELECTED);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.selected_menu_item_key),MENU_SELECTED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.menuPopular){
            MENU_SELECTED = MENU_MOST_POPULAR;
            loadMovieData(getString(R.string.menu_popular));
            return true;
        }else if (itemID == R.id.menuTopRated){
            MENU_SELECTED = MENU_TOP_RATED;
            loadMovieData(getString(R.string.menu_top_rated));
            return true;
        }else if(itemID == R.id.menuFavourite){
            MENU_SELECTED = MENU_FAVOURITE;
            loadMovieData(getString(R.string.menu_favourite));

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findInitialViews(){
        mTextViewError = (TextView) findViewById(R.id.tv_error);
        mProgBar = (ProgressBar) findViewById(R.id.pb_loading);
        mMovieGrid = (RecyclerView) findViewById(R.id.rv_movies);

    }

    private void loadMovieData(String selectedMenuItem){
        LoaderManager lm = getSupportLoaderManager();
        android.support.v4.content.Loader<Movie[]> movieLoader = lm.getLoader(MOVIE_LOADER_ID);
        if (movieLoader == null){
            lm.initLoader(MOVIE_LOADER_ID,null,this);
        }else{
            lm.restartLoader(MOVIE_LOADER_ID,null,this).forceLoad();
        }

    }

    private void showRecyclerViewHideError(){
        mMovieGrid.setVisibility(View.VISIBLE);
        mTextViewError.setVisibility(View.INVISIBLE);
    }
    private void showErrorViewHideRv(String msg){
        mMovieGrid.setVisibility(View.INVISIBLE);
        mTextViewError.setVisibility(View.VISIBLE);
        mTextViewError.setText(msg);
    }

    //onListItemClick method taken from Udacity Creating Android Apps chapter 3
    @Override
    public void onListItemClick(int clickedItemIndex,Movie m) {
        Log.d(TAG, new Object(){}.getClass().getEnclosingMethod().getName() + m.getOrigTitle());
        Intent movieDetailsActivityIntent = new Intent(this,MovieDetailsActivity.class);
        movieDetailsActivityIntent.putExtra(getString(R.string.mov_key),m);
        startActivity(movieDetailsActivityIntent);
    }



    @Override
    public android.support.v4.content.Loader<Movie[]> onCreateLoader(final int id, final Bundle args) {
        MovieLoader ml = new MovieLoader(this,MENU_SELECTED);
        mProgBar.setVisibility(View.VISIBLE);
        return ml;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Movie[]> loader, Movie[] movies) {
        mProgBar.setVisibility(View.INVISIBLE);
        boolean isFave = false;

        if (movies == null){
            showErrorViewHideRv(getString(R.string.tv_error_str));
        }else if(movies.length == 0){
            showErrorViewHideRv(getString(R.string.no_favs_error));
        }else{
            showRecyclerViewHideError();
            Log.d(TAG, getString(R.string.on_post_execute) + movies.length);
            if (MENU_SELECTED.equals(MENU_FAVOURITE)){
                isFave = true;
            }
            mMovieDadapter = new MovieDataAdapter(movies.length,movies,getApplicationContext(),this,isFave);
            mMovieGrid.setAdapter(mMovieDadapter);
            Log.d(TAG, getString(R.string.adapter_set));
        }


    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Movie[]> loader) {
        //Do nothing
    }

}
