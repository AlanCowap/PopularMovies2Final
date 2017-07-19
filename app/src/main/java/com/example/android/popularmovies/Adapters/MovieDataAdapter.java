package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by alex.fanning on 16/05/2017.
 */

public class MovieDataAdapter extends RecyclerView.Adapter<MovieDataAdapter.MovieViewHolder> {

    private static final String TAG = MovieDataAdapter.class.getSimpleName();
    private Movie[] movies;

    private int mNumberItems;
    private static int viewHolderCount;
    private Context context;
    private static boolean isFave = false;
    private final ListItemClickListener mOnClickListener;


    //All RecyclerViewDataAdapter methods have originated from Udacity chapter 3

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex,Movie m);
    }

    public MovieDataAdapter(int numItems,Movie[] _movies,Context _context,ListItemClickListener listener, boolean _isFave){
        mNumberItems = numItems;
        movies = _movies;
        context = _context;
        viewHolderCount = 0;
        mOnClickListener = listener;
        isFave = _isFave;
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Context context = parent.getContext();
        int layoutIdForGridItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForGridItem,parent,shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        viewHolderCount++;

        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        holder.bind(movies[position]);
    }

    @Override
    public int getItemCount() {
        return movies.length;
    }

    //    TODOne Set Onclick lsitener - Implement onclick listener
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgView;

        public MovieViewHolder(View itemView){
            super(itemView);

            imgView = (ImageView) itemView.findViewById(R.id.img_movie_grid);
            itemView.setOnClickListener(this);
        }
        void bind(Movie m){
            if (isFave == false){
                //TODO SUGGESTION Styling, you could rewrite the above as if(!isFave)
                Picasso.with(context).load(m.getPosterPath()).into(imgView);
            }else{
                File f = new File(m.getPosterPathLocal());
                Picasso.with(context).load(f).into(imgView);
            }

            imgView.setContentDescription(m.getOrigTitle());
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();

            Movie selectedMovie = movies[clickedPosition];
            mOnClickListener.onListItemClick(clickedPosition, selectedMovie);
        }
    }

}
