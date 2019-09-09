package com.example.android.popularmovies.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.data.AppRepository;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.MoviesPage;
import com.example.android.popularmovies.data.model.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Application operates in 2 global modes
 * 1. Retrieving data from the network from The Movie DB rest service
 * 2. Retrieving data from the local Database - Favorite Mode
 * By default application retrieves data from the Network
 * To switch to Favorite Mode open Settings and click on checkbox Favorite Movies
 * To put Movie into local Database click on the heart shaped checkbox in MovieDetailActivity
 * A second click on this checkbox will delete the information from the local Database.
 */

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private final AppRepository mRepository;

    private String mSortMode;
    private boolean mLoadFavoriteMovie;

    private int mCurrentPage;
    private int mTotalPages;

    private List<Movie> mMovieList;
    private CustomMediatorLiveData<Resource<List<Movie>>> mMovieCollection;

    public MainViewModel(Application application, AppRepository repository) {
        super(application);
        mRepository = repository;
        mMovieCollection = new CustomMediatorLiveData<>();

        mSortMode = mRepository.getSortMode();
        mLoadFavoriteMovie = mRepository.isFavoriteSelection();

        Log.d(TAG, "MainViewModel has been created");
    }

    private void setupRetrievingDataFromNet() {

        mMovieCollection.addSource(mRepository.retrieveFirstMoviePage(mSortMode),
                new Observer<Resource<MoviesPage>>() {
                    @Override
                    public void onChanged(@Nullable Resource<MoviesPage> moviesPageResource) {

                        if (moviesPageResource == null) return;

                        Resource<List<Movie>> res;

                        if (moviesPageResource.status == Resource.Status.ERROR) {

                            mMovieList.clear();
                            res = Resource.error(moviesPageResource.message, mMovieList);
                            mMovieCollection.setValue(res);

                        } else {

                            MoviesPage page = moviesPageResource.data;

                            Log.d(TAG, String.format("Current page #%d, Received page #%d, Total pages: %d",
                                    mCurrentPage, page.getPage(), mTotalPages));

                            if (mCurrentPage == -1) {
                                mTotalPages = page.getTotalPages();
                            }
                            mCurrentPage = page.getPage();

                            // remove null item if exists. see below and MovieListAdapter
                            int sz = mMovieList.size();
                            if (sz > 0 && mMovieList.get(sz - 1) == null) {
                                mMovieList.remove(sz - 1);
                            }

                            mMovieList.addAll(moviesPageResource.data.getResults());
                            // add empty movie to the end of list.
                            // An empty movie means it isn't the last movie.
                            // and if RecyclerView adapter meets this empty movie
                            // it will show loading progress indicator instead of posters
                            // see MovieListAdapter
                            if (mCurrentPage < mTotalPages) {
                                mMovieList.add(null);
                            }

                            res = Resource.success(mMovieList);
                            mMovieCollection.setValue(res);
                        }
                    }
                });
    }

    private void setupRetrievingDataFromDb() {

        mMovieCollection.addSource(mRepository.dbLoadAllMovies(mSortMode),
                new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(@Nullable List<Movie> movies) {
                        mCurrentPage = 1;
                        mTotalPages = 1;
                        mMovieList.clear();
                        mMovieList.addAll(movies);

                        Resource<List<Movie>> res;
                        res = Resource.success(mMovieList);
                        mMovieCollection.setValue(res);
                    }
                }
        );
    }

    public LiveData<Resource<List<Movie>>> getMovieCollection() {
        return mMovieCollection;
    }

    public void initNewDataRequest() {

        Log.d(TAG, "initNewDataRequest");

        mCurrentPage = -1;
        mTotalPages = 0;

        mRepository.cancelRetrofitRequest();

        mMovieList = new ArrayList<>();
        mSortMode = mRepository.getSortMode();

        mLoadFavoriteMovie = mRepository.isFavoriteSelection();

        mMovieCollection.removeAllSources();
        if (!mLoadFavoriteMovie) {
            setupRetrievingDataFromNet();
        } else {
            setupRetrievingDataFromDb();
        }
    }

    public void retrieveNextPage() {
        if (!mLoadFavoriteMovie) {
            mRepository.retrieveMoviePage(mSortMode, mCurrentPage + 1);
        }
    }

    public boolean hasNextPage() {
        return (mCurrentPage < mTotalPages) && !mLoadFavoriteMovie;
    }

    public boolean isTransitionEnabled() {
        return mRepository.isTransitionEnabled();
    }

    public boolean isFavoriteMode() {
        return mRepository.isFavoriteSelection();
    }

    @Override
    protected void onCleared() {
        mMovieCollection.removeAllSources();
        super.onCleared();
        Log.d(TAG, "onCleared: ");
    }
}
