package com.example.android.popularmovies.data;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.MyApplication;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.database.AppDatabase;
import com.example.android.popularmovies.data.database.MovieDao;
import com.example.android.popularmovies.data.model.Backdrop;
import com.example.android.popularmovies.data.model.BackdropCollection;
import com.example.android.popularmovies.data.model.Genre;
import com.example.android.popularmovies.data.model.Language;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.MovieDetail;
import com.example.android.popularmovies.data.model.MoviesPage;
import com.example.android.popularmovies.data.model.Resource;
import com.example.android.popularmovies.data.model.Review;
import com.example.android.popularmovies.data.model.ReviewCollection;
import com.example.android.popularmovies.data.model.Trailer;
import com.example.android.popularmovies.data.model.TrailerCollection;
import com.example.android.popularmovies.data.network.NetworkApi;
import com.example.android.popularmovies.ui.CustomMediatorLiveData;
import com.example.android.popularmovies.util.IAppExecutors;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepository {

    private static final String TAG = AppRepository.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static volatile AppRepository sInstance = null;

    private final Context mContext;

    private final IAppExecutors mAppExecutors;
    private final AppDatabase mAppDatabase;
    private final NetworkApi mNetworkApi;

    private Call<MovieDetail> mMovieDetailQuery;
    private Call<MoviesPage> mMovieQuery;
    private Call<TrailerCollection> mTrailerQuery;
    private Call<ReviewCollection> mReviewQuery;
    private Call<BackdropCollection> mBackdropQuery;

    private MutableLiveData<Resource<MoviesPage>> mNetworkMoviesPage;

    private AppRepository(Context context, IAppExecutors appExecutors, AppDatabase appDatabase, NetworkApi networkApi) {
        mContext = context;
        mAppExecutors = appExecutors;
        mAppDatabase = appDatabase;
        mNetworkApi = networkApi;
    }

    public static AppRepository getInstance(Context mContext, IAppExecutors appExecutors,
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

    public LiveData<MovieDetail> dbLoadMovieDetailById(long id) {

        MovieDao movieDao = mAppDatabase.getMovieDao();
        LiveData<MovieDetail> movieDetail = movieDao.loadMovieDetailById(id);
        LiveData<List<Genre>> genresList = movieDao.loadAllGenres(id);

        LiveDataMerger res = new LiveDataMerger();
        res.addMovieDetail(movieDetail);
        res.addGenres(genresList);
        res.removeAllSources();

        return res;
    }

    private class LiveDataMerger extends CustomMediatorLiveData<MovieDetail> {

        boolean mMoveDetailIsNull = false;
        MovieDetail mMovieDetail = null;
        List<Genre> mGenres = null;

        public void update() {
            if ((mMovieDetail != null || mMoveDetailIsNull) && mGenres != null) {
                if (mMovieDetail != null) {
                    mMovieDetail.setGenres(mGenres);
                }
                setValue(mMovieDetail);
            }
        }

        public void addMovieDetail(LiveData<MovieDetail> movieDetailLiveData) {
            addSource(movieDetailLiveData, new Observer<MovieDetail>() {
                @Override
                public void onChanged(@Nullable MovieDetail o) {
                    mMovieDetail = o;
                    mMoveDetailIsNull = o == null;
                    update();
                }
            });
        }

        public void addGenres(LiveData<List<Genre>> listLiveData) {
            addSource(listLiveData, new Observer<List<Genre>>() {
                @Override
                public void onChanged(@Nullable List<Genre> o) {
                    mGenres = o;
                    update();
                }
            });
        }
    }

    public LiveData<List<Movie>> dbLoadAllMovies(String sortMode) {
        MovieDao movieDao = mAppDatabase.getMovieDao();
        LiveData<List<Movie>> res;
        if (getMostPopularStringArgValue().equals(sortMode)) {
            res = movieDao.loadAllMoviesByPopularity();
        } else {
            res = movieDao.loadAllMoviesByRating();
        }
        return res;
    }

    /**
     * Checks if a Movie exists in th Database
     *
     * @param id
     * @return
     */

    public LiveData<Boolean> dbIsFavoriteMovie(long id) {
        MovieDao movieDao = mAppDatabase.getMovieDao();
        LiveData<Long> favoriteMovie = movieDao.isFavoriteMovie(id);
        return Transformations.map(favoriteMovie, new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long input) {
                return input != null;
            }
        });
    }

    public LiveData<List<Trailer>> dbLoadAllTrailers(Long movieId) {
        MovieDao movieDao = mAppDatabase.getMovieDao();
        return movieDao.loadAllTrailers(movieId);
    }

    public LiveData<List<Review>> dbLoadAllReviews(Long movieId) {
        MovieDao movieDao = mAppDatabase.getMovieDao();
        return movieDao.loadAllReviews(movieId);
    }

    public LiveData<List<Backdrop>> dbLoadAllBackdrops(Long movieId) {
        MovieDao movieDao = mAppDatabase.getMovieDao();
        return movieDao.loadAllBackdrops(movieId);
    }

    public void dbDeleteMovieById(final long id) {
        final MovieDao movieDao = mAppDatabase.getMovieDao();
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.deleteMovieById(id);
            }
        });
    }

    /**
     * Inserts a Movie, Trailers and Reviews into Database
     *
     * @param movie
     * @param trailers
     * @param reviews
     */
    public void dbInsertMovie(final MovieDetail movie, final List<Trailer> trailers,
                              final List<Review> reviews, final List<Genre> genres,
                              final List<Backdrop> backdrops) {
        final MovieDao movieDao = mAppDatabase.getMovieDao();
        mAppExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.insertMovie(movie, trailers, reviews, genres, backdrops);
            }
        });
    }

    // **********************************************
    //
    // Network API related methods
    //
    // **********************************************

    public LiveData<Resource<MovieDetail>> retrieveMoveDetail(long movieId) {

        final MutableLiveData<Resource<MovieDetail>> observedData = new MutableLiveData<>();

        Callback<MovieDetail> callback = new Callback<MovieDetail>() {

            @MainThread
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                MovieDetail body = response.body();
                Resource<MovieDetail> result;
                if (body != null) {
                    result = Resource.success(body);
                } else {
                    result = Resource.error("Empty body", null);
                }
                observedData.setValue(result);
            }

            @MainThread
            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                if (!call.isCanceled()) {
                    Resource<MovieDetail> result = Resource.error("error", null);
                    observedData.setValue(result);
                    t.printStackTrace();
                }
            }
        };

        mMovieDetailQuery = mNetworkApi.getMovieDetail(movieId);
        mMovieDetailQuery.enqueue(callback);
        return observedData;
    }

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

        if (mMovieDetailQuery != null && mMovieDetailQuery.isExecuted()) {
            mMovieDetailQuery.cancel();
        }

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

    public boolean isFavoriteMode() {
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

    // **********************************************
    //
    // Reference data related methods
    //
    // **********************************************

    public String getGenreById(int id) {
        return ((MyApplication) mContext.getApplicationContext()).getGenresArray().get(id);
    }

    public Language getLanguageByCode(String code639_1) {
        return ((MyApplication) mContext.getApplicationContext()).getLanguageMap().get(code639_1);
    }
}
