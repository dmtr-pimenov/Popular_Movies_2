package com.example.android.popularmovies.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.Trailer;
import com.example.android.popularmovies.data.network.NetworkApi;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.TrailerViewHolder> {

    private List<Trailer> mTrailers;
    private Context mContext;
    LayoutInflater mInflater;

    private ListItemClickListener mClickListener;

    public TrailerListAdapter(Context context, @NonNull List<Trailer> trailers, ListItemClickListener listener) {
        mTrailers = trailers;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mClickListener = listener;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.trailer_list_item, viewGroup, false);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder viewHolder, int i) {
        viewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView thumbnail;
        TextView trailerName;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.image_trailer_thumbnail);
            trailerName = itemView.findViewById(R.id.text_trailer_name);
            itemView.setOnClickListener(this);
        }

        void bind(int itemIndex) {
            Trailer t = mTrailers.get(itemIndex);
            Picasso.with(mContext)
                    .load(NetworkApi.getVideoThimbnailUrl(t.getKey()))
                    .into(thumbnail);
            trailerName.setText(t.getName());
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                Trailer t = mTrailers.get(getAdapterPosition());
                mClickListener.onListItemClick(t);
            }
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(Trailer trailer);
    }
}
