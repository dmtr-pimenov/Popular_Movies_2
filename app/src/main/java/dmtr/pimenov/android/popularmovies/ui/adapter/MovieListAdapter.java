package dmtr.pimenov.android.popularmovies.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import dmtr.pimenov.android.popularmovies.data.model.Movie;
import dmtr.pimenov.android.popularmovies.data.network.NetworkApi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MoviesAdapterViewHolder> {

    // item type - regular movie, we have to show movie's poster
    public static final int ITEM_TYPE_MOVIE = 1;
    // item type - loading indicator, we have to show progress bar
    public static final int ITEM_TYPE_LOADING = 2;
    private static final String TAG = MovieListAdapter.class.getSimpleName();
    private final Context mContext;
    LayoutInflater mInflater;
    // reference to on click listener
    final private ListItemClickListener mOnClickListener;
    // movie collection
    private List<Movie> mMovieList = new ArrayList<>();

    /**
     * Constructor of MovieListAdapter
     *
     * @param context
     * @param clickListener reference to onClick listener
     */
    public MovieListAdapter(Context context, ListItemClickListener clickListener) {
        mContext = context;
        mOnClickListener = clickListener;
        mInflater = LayoutInflater.from(mContext);
    }

    /**
     * This method is called when creating new ViewHolder
     * ViewHolder can contain 2 types of layout, it depends on viewType
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layoutId;

        if (viewType == ITEM_TYPE_MOVIE) {
            layoutId = R.layout.movie_list_item;
        } else {
            layoutId = R.layout.progress_bar_list_item;
        }

        View view = mInflater.inflate(layoutId, parent, false);

        MoviesAdapterViewHolder viewHolder = new MoviesAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapterViewHolder holder, int position) {
        // if move is not null fill in controls
        // if movie is null so we are dealing with progress bar item. no action required
        Movie movie = mMovieList.get(position);
        if (movie != null) {
            holder.bind(movie);
        }
    }

    /**
     * Returns item type.
     *
     * @param position index of movie in collection
     * @return if movie is null them item type is ITEM_TYPE_LOADING, otherwise ITEM_TYPE_MOVIE
     */
    @Override
    public int getItemViewType(int position) {
        return mMovieList.get(position) == null ? ITEM_TYPE_LOADING : ITEM_TYPE_MOVIE;
    }

    @Override
    public int getItemCount() {
        return mMovieList == null ? 0 : mMovieList.size();
    }

    /**
     * Sets a new collection of movies to an existing one
     * This method is called when we continue to load movies.
     *
     * @param movies
     */
    public void setData(@NonNull List<Movie> movies) {
        // remove null item if exists
        mMovieList.clear();
        mMovieList.addAll(movies);
        notifyDataSetChanged();
    }

    public interface ListItemClickListener {
        void onListItemClick(Movie movie, ImageView image2Transition);
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        final ImageView moviePoster;

        MoviesAdapterViewHolder(View view) {
            super(view);
            moviePoster = view.findViewById(R.id.image_movie_poster);
            view.setOnClickListener(this);
        }

        void bind(Movie movie) {

            Drawable placeholder = ContextCompat.getDrawable(mContext, R.drawable.ic_file_download);
            Drawable error = ContextCompat.getDrawable(mContext, R.drawable.ic_error);

            String posterPath = NetworkApi.getPosterUrl(movie.getPosterPath(),
                    NetworkApi.PosterSize.W185);

            Picasso.with(mContext).load(posterPath)
                    .placeholder(placeholder)
                    .error(error)
                    .into(moviePoster);
        }

        @Override
        public void onClick(View v) {
            Movie movie = mMovieList.get(getAdapterPosition());
            if (movie != null) {
                ImageView moviePoster = v.findViewById(R.id.image_movie_poster);
                mOnClickListener.onListItemClick(mMovieList.get(getAdapterPosition()), moviePoster);
            }
        }
    }
}
