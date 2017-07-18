package com.example.android.popularmovies.Utils;

import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 16-May-17.
 */

public final class MovieJsonUtilities {

    private static final String TAG = MovieJsonUtilities.class.getSimpleName();

    public static Movie[] getMoviesFromJSON(Context context, String movieJSONstr) throws JSONException{
        final String MOV_JSON_RESULTS = context.getString(R.string.movie_json_results);
        final String MOV_JSON_POSTER_PATH = context.getString(R.string.json_poster_path);
        final String MOV_JSON_ID = context.getString(R.string.json_id);
        final String MOV_JSON_SYNOPOSIS = context.getString(R.string.json_synoposis);
        final String MOV_JSON_RELEASE_DATE = context.getString(R.string.json_release_date);
        final String MOV_JSON_TITLE = context.getString(R.string.json_original_title);
        final String MOV_VOTE = context.getString(R.string.json_vote_average);

        Movie[] movies = null;


        JSONObject movieJsonObj = new JSONObject(movieJSONstr);

        JSONArray movieArray = movieJsonObj.getJSONArray(MOV_JSON_RESULTS);
        int numMovies = movieArray.length();
        Log.d(TAG, context.getString(R.string.mov_arr_count) + numMovies);

        movies = new Movie[numMovies];


        for (int i = 0; i < numMovies;  i ++){
            JSONObject movie = movieArray.getJSONObject(i);
            String posterPath = movie.getString(MOV_JSON_POSTER_PATH);
            String fullPathUrl = context.getString(R.string.movie_img_base_path) +
                                    context.getString(R.string.movie_img_size) +
                                    posterPath;
            int id = movie.getInt(MOV_JSON_ID);
            String synoposis = movie.getString(MOV_JSON_SYNOPOSIS);
            String relDate = movie.getString(MOV_JSON_RELEASE_DATE);
            String title = movie.getString(MOV_JSON_TITLE);
            Double movVote = movie.getDouble(MOV_VOTE);

            Movie m = new Movie(id,title,synoposis,movVote,relDate,fullPathUrl);
           // Log.d(TAG, context.getString(R.string.mov_poster_strs) + fullPathUrl);
            movies[i] = m;
        }

        return movies;
    }


    public static String[] getTrailersFromJson(Context c,String jsonStr ){
        final String RESULTS_KEY = c.getString(R.string.movie_json_results);
        final String KEY_KEY = c.getString(R.string.key_key);


        String[] trailerKeys = null;
        JSONObject jsonObj = null;
        try{
            jsonObj = new JSONObject(jsonStr);
            JSONArray trailerArray = jsonObj.getJSONArray(RESULTS_KEY);
            int numTrailers = trailerArray.length();
            trailerKeys = new String[numTrailers];

            for (int i = 0; i < numTrailers; i++){
                JSONObject trailer = trailerArray.getJSONObject(i);
                String key = trailer.getString(KEY_KEY);
                trailerKeys[i] = key;
            }


        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return trailerKeys;
    }

    public static String[] getReviewsFromJson(Context c,String jsonStr ){
        final String RESULTS_KEY = c.getString(R.string.movie_json_results);
        final String CONTENT_KEY = c.getString(R.string.content_key);


        String[] reviews = null;
        JSONObject jsonObj = null;
        try{
            jsonObj = new JSONObject(jsonStr);
            JSONArray reviewJsonArray = jsonObj.getJSONArray(RESULTS_KEY);
            int numReviews = reviewJsonArray.length();
            reviews = new String[numReviews];

            for (int i = 0; i < numReviews; i++){
                JSONObject reviewObj = reviewJsonArray.getJSONObject(i);
                String key = reviewObj.getString(CONTENT_KEY);
                reviews[i] = key;
            }


        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return reviews;
    }







}
