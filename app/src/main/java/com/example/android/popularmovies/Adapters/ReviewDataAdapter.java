package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

/**
 * Created by alex.fanning on 29/06/2017.
 */

public class ReviewDataAdapter extends  RecyclerView.Adapter<ReviewDataAdapter.ReviewViewHolder> {
    private static final String TAG = ReviewDataAdapter.class.getSimpleName();
    private String[] reviews;
    private static int sViewHolderCount;
    private int mNumberItems;
    private Context context;


    public ReviewDataAdapter(String[] _reviews, Context _c){
        mNumberItems = _reviews.length;
        reviews = _reviews;
        context = _c;
        sViewHolderCount = 0;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForGridItem = R.layout.review_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForGridItem,parent,false);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);
        sViewHolderCount++;
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(reviews[position]);
    }

    @Override
    public int getItemCount() {
        return reviews.length;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        ExpandableTextView eTv = null;
        public ReviewViewHolder(View itemView){
            super(itemView);

            eTv = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
        }
        void bind(String review){
            eTv.setText(review);
        }
    }
}
