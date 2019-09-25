package com.example.android.popularmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.database.AppDatabase;
import com.example.android.popularmovies.data.database.MovieDao;
import com.example.android.popularmovies.data.model.BackdropCollection;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.MoviesPage;
import com.example.android.popularmovies.data.model.Resource;
import com.example.android.popularmovies.data.model.ReviewCollection;
import com.example.android.popularmovies.data.model.ReviewMinimal;
import com.example.android.popularmovies.data.model.TrailerCollection;
import com.example.android.popularmovies.data.model.TrailerMinimal;
import com.example.android.popularmovies.data.network.NetworkApi;
import com.example.android.popularmovies.util.AppExecutors;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepository {

    private static final String TAG = AppRepository.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static AppRepository sInstance = null;

    private final Context mContext;

    private final AppExecutors mAppExecutors;
    private final AppDatabase mAppDatabase;
    private final NetworkApi mNetworkApi;

    private Call<MoviesPage> mMovieQuery;
    private Call<TrailerCollection> mTrailerQuery;
    private Call<ReviewCollection> mReviewQuery;
    private Call<BackdropCollection> mBackdropQuery;

    private MutableLiveData<Resource<MoviesPage>> mNetworkMoviesPage;

    private AppRepository(Context context, AppExecutors appExecutors, AppDatabase appDatabase, NetworkApi networkApi) {
        mContext = context;
        mAppExecutors = appExecutors;
        mAppDatabase = appDatabase;
        mNetworkApi = networkApi;
    }

    public static AppRepository getInstance(Context mContext, AppExecutors appExecutors,
                                            AppDatabase appDatabase, NetworkApi networkApi) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new AppRepository(mContext, appExecutors, appDatabase, networkApi);
                    Log.d(TAG, "getInstance: " + "Repository has been created");
                }
            }
        }
        return sInstance;
    }

    // **********************************************
    //
    // Database related methods
    //
    // **********************************************

    public LiveData<List<Movie>> dbLoadAllMovies(String sortMode) {
        MovieDao movieDao = mAppDatabase.getMovieDao();
        LiveData<List<Movie>> res;
        if (getMostPopularStringArgValue().equals(sortMode)) {
            // TODO: 19.09.2019 uncomment it
//            res = movieDao.loadAllMoviesByPopularity();
        } else {
            // TODO: 19.09.2019
//            res = movieDao.loadAllMoviesByRating();
        }
        // TODO: 19.09.2019 uncomment it
//        return res;
        return null;
    }

    /**
     * Checks if a Movie exists in th Database
     * @param id
     * @return
     */

    // // TODO: 19.09.2019 looks like you can return LiveData<Boolean> instead of LiveData<Long>
    public LiveData<Long> dbIsFavoriteMovie(long id) {
        MovieDao movieDao = mAppDatabase.getMovieDao();
        // TODO: 19.09.2019 uncomment it
//        return movieDao.isFavoriteMovie(id);
        return null;
    }

    public LiveData<List<TrailerMinimal>> dbLoadAllTrailers(Long movieId) {
        MovieDao movieDao = mAppDatabase.getMovieDao();
        return movieDao.loadAllTrailers(movieId);
    }

    public LiveData<List<ReviewMinimal>> dbLoadAllReviews(Long movieId) {
        MovieDao movieDao = mAppDatabase.getMovieDao();
        return movieDao.loadAllReviews(movieId);
    }

    public void dbDeleteMovie(final Movie movie) {
        final MovieDao movieDao = mAppDatabase.getMovieDao();
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // TODO: 19.09.2019 uncomment it
//                movieDao.deleteMovie(movie);
            }
        });
    }

    /**
     * Inserts a Movie, Trailers and Reviews into Database
     * @param movie
     * @param trailers
     * @param reviews
     */
    public void dbInsertMovie(final Movie movie, final List<TrailerMinimal> trailers,
                              final List<ReviewMinimal> reviews) {
        final MovieDao movieDao = mAppDatabase.getMovieDao();
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // TODO: 19.09.2019 uncomment it
//                movieDao.insertMovieAndChildren(movie, trailers, reviews);
            }
        });
    }

    // **********************************************
    //
    // Network API related methods
    //
    // **********************************************

    public LiveData<Resource<MoviesPage>> retrieveFirstMoviePage(String sortMode) {
        mNetworkMoviesPage = new MutableLiveData<>();
        retrieveMoviePage(sortMode, 1);
        return mNetworkMoviesPage;
    }

    public void retrieveMoviePage(String sortMode, int pageNum) {

        Log.d(TAG, "retrieveMoviePage: #" + pageNum);

        Callback<MoviesPage> moviesPageCallback = new Callback<MoviesPage>() {
            @MainThread
            @Override
            public void onResponse(Call<MoviesPage> call, Response<MoviesPage> response) {
                MoviesPage pages = response.body();
                if (pages != null) {
                    Resource<MoviesPage> result = Resource.success(pages);
                    mNetworkMoviesPage.setValue(result);
                } else {
                    Resource<MoviesPage> result = Resource.error("Empty Body", null);
                    mNetworkMoviesPage.setValue(result);
                }
            }

            @MainThread
            @Override
            public void onFailure(Call<MoviesPage> call, Throwable t) {
                if (!call.isCanceled()) {
                    Resource<MoviesPage> result = Resource.error("error", null);
                    mNetworkMoviesPage.setValue(result);
                    t.printStackTrace();
                }
            }
        };

        mMovieQuery = mNetworkApi.getMoviesPage(sortMode, pageNum);
        // enqueue query. The request will be executed asynchronously
        mMovieQuery.enqueue(moviesPageCallback);
    }

    public LiveData<Resource<BackdropCollection>> retrieveBackdropCollection(long movieId) {

        final MutableLiveData<Resource<BackdropCollection>> observedData = new MutableLiveData<>();

        Callback<BackdropCollection> backdropCallback = new Callback<BackdropCollection>() {
            @MainThread
            @Override
            public void onResponse(Call<BackdropCollection> call, Response<BackdropCollection> response) {
                BackdropCollection collection = response.body();
                Resource<BackdropCollection> result;
                if (collection != null) {
                    result = Resource.success(collection);
                } else {
                    result = Resource.error("Empty body", null);
                }
                observedData.setValue(result);
            }

            @MainThread
            @Override
            public void onFailure(Call<BackdropCollection> call, Throwable t) {
                if (!call.isCanceled()) {
                    Resource<BackdropCollection> result = Resource.error("error", null);
                    observedData.setValue(result);
                    t.printStackTrace();
                }
            }
        };

        mBackdropQuery = mNetworkApi.getBackdropCollection(movieId);
        mBackdropQuery.enqueue(backdropCallback);

        return observedData;
    }

    public LiveData<Resource<TrailerCollection>> retrieveTrailerCollection(long movieId) {

        final MutableLiveData<Resource<TrailerCollection>> observedData = new MutableLiveData<>();

        Log.d(TAG, "retrieveTrailersList. Movie ID: " + movieId);

        Callback<TrailerCollection> trailersCallback = new Callback<TrailerCollection>() {
            @MainThread
            @Override
            public void onResponse(Call<TrailerCollection> call, Response<TrailerCollection> response) {
                TrailerCollection trailers = response.body();
                if (trailers != null) {
                    Resource<TrailerCollection> result = Resource.success(trailers);
                    observedData.setValue(result);
                } else {
                    Resource<TrailerCollection> result = Resource.error("Empty Body", null);
                    observedData.setValue(result);
                }
            }

            @MainThread
            @Override
            public void onFailure(Call<TrailerCollection> call, Throwable t) {
                if (!call.isCanceled()) {
                    Resource<TrailerCollection> result = Resource.error("error", null);
                    observedData.setValue(result);
                    t.printStackTrace();
                }
            }
        };

        mTrailerQuery = mNetworkApi.getTrailersCollection(movieId);
        // enqueue query. The request will be executed asynchronously
        mTrailerQuery.enqueue(trailersCallback);

        return observedData;
    }

    public LiveData<Resource<ReviewCollection>> retrieveReviewCollection(long movieId) {

        final MutableLiveData<Resource<ReviewCollection>> observedData = new MutableLiveData<>();

        Log.d(TAG, "retrieveReviewCollection. Movie ID: " + movieId);

        Callback<ReviewCollection> reviewsCallback = new Callback<ReviewCollection>() {
            @MainThread
            @Override
            public void onResponse(Call<ReviewCollection> call, Response<ReviewCollection> response) {
                ReviewCollection reviews = response.body();
                if (reviews != null) {
                    Resource<ReviewCollection> result = Resource.success(reviews);
                    observedData.setValue(result);
                } else {
                    Resource<ReviewCollection> result = Resource.error("Empty Body", null);
                    observedData.setValue(result);
                }
            }

            @MainThread
            @Override
            public void onFailure(Call<ReviewCollection> call, Throwable t) {
                if (!call.isCanceled()) {
                    Resource<ReviewCollection> result = Resource.error("error", null);
                    observedData.setValue(result);
                    t.printStackTrace();
                }
            }
        };

        mReviewQuery = mNetworkApi.getReviewCollection(movieId);
        // enqueue query. The request will be executed asynchronously
        mReviewQuery.enqueue(reviewsCallback);

        return observedData;
    }

    /**
     * Cancels the Retrofit requests if active
     */
    public void cancelRetrofitRequest() {
        if (mMovieQuery != null && mMovieQuery.isExecuted()) {
            mMovieQuery.cancel();
        }
        if (mTrailerQuery != null && mTrailerQuery.isExecuted()) {
            mTrailerQuery.cancel();
        }
        if (mReviewQuery != null && mReviewQuery.isExecuted()) {
            mReviewQuery.cancel();
        }
        if (mBackdropQuery != null && mBackdropQuery.isExecuted()) {
            mBackdropQuery.cancel();
        }
    }

    // **********************************************
    //
    // Preferences related methods
    //
    // All settings are saved in the Preferences.
    // (see SettingsActivity.java and SettingsFragment)
    // To access these settings intended next methods
    //
    // **********************************************

    public String getSortMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getString(mContext.getString(R.string.pref_sort_mode_key),
                mContext.getString(R.string.default_sort_mode));
    }

    public boolean isFavoriteSelection() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getBoolean(mContext.getString(R.string.pref_favorite_key),
                mContext.getResources().getBoolean(R.bool.pref_favorite_default));
    }

    public boolean isTransitionEnabled() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getBoolean(mContext.getString(R.string.pref_transition_key),
                mContext.getResources().getBoolean(R.bool.pref_transition_default));
    }

    private String getMostPopularStringArgValue() {
        return mContext.getString(R.string.pref_most_popular_value);
    }
}
