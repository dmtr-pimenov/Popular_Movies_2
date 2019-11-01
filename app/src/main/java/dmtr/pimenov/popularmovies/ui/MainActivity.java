package dmtr.pimenov.popularmovies.ui;

import android.annotation.SuppressLint;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import dmtr.pimenov.popularmovies.BuildConfig;
import dmtr.pimenov.popularmovies.R;
import dmtr.pimenov.popularmovies.data.model.Movie;
import dmtr.pimenov.popularmovies.data.model.Resource;
import dmtr.pimenov.popularmovies.ui.adapter.MovieListAdapter;
import dmtr.pimenov.popularmovies.ui.factory.MainViewModelFactory;
import dmtr.pimenov.popularmovies.util.InjectorUtil;
import dmtr.pimenov.popularmovies.util.MarginItemDecorator;
import dmtr.pimenov.popularmovies.util.NetworkUtils;
import dmtr.pimenov.popularmovies.util.UiUtils;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements MovieListAdapter.ListItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE_SETTINGS = 1;

    // Reference to RecyclerView and Adapter. Used to reload and update movie list
    private RecyclerView mMovieListRecyclerView;
    private MovieListAdapter mMovieListAdapter;

    // For displaying an error message
    private TextView mErrorMessageTextView;
    private ViewGroup mErrorLayout;

    // For the user's peace of mind
    private ProgressBar mLoadingIndicator;

    // We will arrange posters as a grid.
    private GridLayoutManager mGridLayoutManager;

    // To indicate that the loading in progress
    private boolean mLoadingInProgress;

    // number of columns with movie posters (in RecyclerView)
    // calculated value. depends on screen size and device orientation
    private int mSpanCount;
    // SpanSizeLookup() custom implementation
    // by default returns number of columns in RecyclerView
    // but in case of empty movie this function returns 1
    // that means the loading indicator will occupy all columns
    private final GridLayoutManager.SpanSizeLookup mSpanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            int itemType = mMovieListAdapter.getItemViewType(position);
            return itemType == MovieListAdapter.ITEM_TYPE_LOADING ? mSpanCount : 1;
        }
    };
    // AAC related members
    private MainViewModel mViewModel;

    // RecyclerView the scroll event handler
    // it is necessary to dynamically load new pages.
    private final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int totalCount = mGridLayoutManager.getItemCount();
            int lastVisible = mGridLayoutManager.findLastVisibleItemPosition();

            if (mLoadingInProgress || (lastVisible < totalCount - 1) || !mViewModel.hasNextPage()) {
                return;
            }
            // new page will be downloaded if:
            // no active downloading - mLoadingInProgress = false
            // current page isn't last page
            // last visible index of movie in RecyclerView more than total number of movies
            loadNextMoviePage();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieListRecyclerView = findViewById(R.id.rv_movies);
        mErrorMessageTextView = findViewById(R.id.tv_error_message);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mErrorLayout = findViewById(R.id.layout_error_message);

        // calculates the number of column for posters
        mSpanCount = calcSpanCount(this);
        setupRecyclerView();

        createViewModel();

        // check if device is online or if device is not online then favorite mode should be true
        // favorite mode == true means that we work with local database
        if (!NetworkUtils.isOnline(this) && !mViewModel.isFavoriteMode()) {
            showErrorMessage(R.string.no_network_message);
            return;
        } else {
            setupViewModel(savedInstanceState);
        }

        if (BuildConfig.DEBUG) {
            Picasso picasso = Picasso.with(this);
            picasso.setIndicatorsEnabled(true);
        }
    }

    /**
     * Creates a menu to show settings screen
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Callback function to handle the menu item selection
     *
     * @param item - Selected menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            showSettingsActivity();
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /*
     * SettingsActivity listen for changes in SharedPreferences
     * and if Settings that are responsible for data selection have been changed,
     * SettingsActivity will inform MainActivity about it
     */
    private void showSettingsActivity() {
        Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
        startActivityForResult(startSettingsActivity, REQUEST_CODE_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTINGS) {
            if (resultCode == SettingsActivity.RESULT_PREF_CHANGED) {
                Log.d(TAG, "onActivityResult: " + "Preferences has been changed");
                loadFirstPage();
            } else {
                Log.d(TAG, "onActivityResult: " + "Preferences are not changed");
            }
        }
    }

    /**
     * Creates GridLayoutManager, and makes some RecyclerView settings
     */
    private void setupRecyclerView() {
        mGridLayoutManager =
                new GridLayoutManager(this, mSpanCount, GridLayoutManager.VERTICAL, false);
        // set custom SpanSizeLookup implementation. see below in this file
        mGridLayoutManager.setSpanSizeLookup(mSpanSizeLookup);
        mMovieListRecyclerView.setLayoutManager(mGridLayoutManager);

        mMovieListRecyclerView.setHasFixedSize(true);
        int recycleViewMargin = (int) getResources().getDimension(R.dimen.grid_spacing);
        mMovieListRecyclerView.addItemDecoration(new MarginItemDecorator(recycleViewMargin, mSpanCount));

        // create and setup adapter
        mMovieListAdapter = new MovieListAdapter(this, this);
        mMovieListRecyclerView.setAdapter(mMovieListAdapter);

        // add scroll event handler
        mMovieListRecyclerView.addOnScrollListener(mScrollListener);
    }

    /**
     * Shows error message
     * Hides RecyclerView and displays layout with: error message text
     * and TRY AGAIN button
     *
     * @param resId - string id in resources
     */
    private void showErrorMessage(int resId) {
        mMovieListRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setText(resId);
        mErrorLayout.setVisibility(View.VISIBLE);
    }

    /**
     * "TRY AGAIN" button click handler
     *
     * @param view
     */
    public void onTryAgainButtonPressed(View view) {
        if (NetworkUtils.isOnline(this)) {
            // hide Error layout
            mErrorLayout.setVisibility(View.INVISIBLE);
            loadFirstPage();
        }
    }

    private void createViewModel() {
        MainViewModelFactory factory = InjectorUtil.provideMainViewModelFactory(getApplication());
        mViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
    }

    private void setupViewModel(Bundle savedInstance) {


        LiveData<Resource<List<Movie>>> movieCollection = mViewModel.getMovieCollection();

        movieCollection.observe(this, new Observer<Resource<List<Movie>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Movie>> movies) {

                if (mLoadingIndicator.getVisibility() == View.VISIBLE) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                }

                // clear download flag
                mLoadingInProgress = false;

                if (movies.status == Resource.Status.ERROR) {
                    showErrorMessage(R.string.error_loading_movies_list_message);
                    Log.e(TAG, movies.message);
                    return;
                }

                Log.d(TAG, "onChanged. Resource status: " + movies.status +
                        " items: " + movies.data.size());

                if (mLoadingIndicator.getVisibility() != View.VISIBLE) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                }

                if (mMovieListRecyclerView.getVisibility() != View.VISIBLE) {
                    mMovieListRecyclerView.setVisibility(View.VISIBLE);
                }

                List<Movie> movieList = movies.data;

                // add empty movie to the end of list.
                // see MovieListAdapter.getItemViewType,
                // MovieListAdapter.onCreateViewHolder
                // and GridLayoutManager.SpanSizeLookup implementation in this file
                // null instead of Movie object means we have to show loading indicator
                // instead of posters
                mMovieListAdapter.setData(movieList);
            }
        });

        // we need to determine whether to run the request
        // we will not run request if activity was recreated after device rotation
        // in this case savedInstance state isn't null and MainViewModel has previously loaded data
        // data can be null if app was unloaded from memory due to resource starvation
        // in short, here are the conditions to execute request:
        // savedInstance == null (and of course MainViewModel doesn't have previously loaded data)
        // or
        // savedInstance != null && MainViewModel doesn't have previously loaded data
        // ----
        // so we can eliminate savedInstanceState checking
        if (movieCollection.getValue() == null) {
            loadFirstPage();
        }
    }

    private void loadFirstPage() {
        if (mErrorLayout.getVisibility() == View.VISIBLE) {
            mErrorLayout.setVisibility(View.INVISIBLE);
        }
        // hide RecyclerView
        mMovieListRecyclerView.setVisibility(View.INVISIBLE);
        mMovieListRecyclerView.getLayoutManager().scrollToPosition(0);
        // show loading indicator
        mLoadingIndicator.setVisibility(View.VISIBLE);
        // clear data. it's useful for pimenov if we change sort order
        mViewModel.initNewDataRequest();
    }

    /**
     * Loads next movies page (2nd page, 3rd and so on...)
     */
    private void loadNextMoviePage() {

        // set download flag
        // if this flag is true we won't start downloading the next page
        // if onScroll event occurs. See RecyclerView.OnScrollListener implementation bellow
        mLoadingInProgress = true;
        mViewModel.retrieveNextPage();
    }

    /**
     * RecyclerView item click handler
     *
     * @param movie selected movie
     */
    @SuppressLint("NewApi")
    @Override
    public void onListItemClick(Movie movie, ImageView image2Transition) {
        // create an Intent to launch movie detail activity
        Intent startMovieDetailActivity = new Intent(this, MovieDetailActivity.class);
        // put serialized movie into extra bundle
        startMovieDetailActivity.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.getId());
        startMovieDetailActivity.putExtra(MovieDetailActivity.EXTRA_MOVIE_TITLE, movie.getTitle());
        startMovieDetailActivity.putExtra(MovieDetailActivity.EXTRA_POSTER_PATH, movie.getPosterPath());
        // start activity with transition
        ActivityOptionsCompat options;
        if (UiUtils.isTransitionAvailable(this)) {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    image2Transition, image2Transition.getTransitionName());
            startActivity(startMovieDetailActivity, options.toBundle());
        } else {
            startActivity(startMovieDetailActivity);
        }
    }

    /**
     * Helper method. Calculates the number of columns of movie posters
     * This method makes approximate calculation - We don't take into account gaps between columns
     *
     * @param context
     * @return
     */
    private int calcSpanCount(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float posterWidth = context.getResources().getDimension(R.dimen.poster_width);
        return Math.round(displayMetrics.widthPixels / posterWidth);
    }
}
