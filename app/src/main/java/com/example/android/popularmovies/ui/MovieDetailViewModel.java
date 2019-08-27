package com.example.android.popularmovies.ui;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.android.popularmovies.data.AppRepository;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.Resource;
import com.example.android.popularmovies.data.model.ReviewCollection;
import com.example.android.popularmovies.data.model.ReviewMinimal;
import com.example.android.popularmovies.data.model.TrailerCollection;
import com.example.android.popularmovies.data.model.TrailerMinimal;
import com.example.android.popularmovies.util.Converter;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailViewModel extends ViewModel {

    private static final String TAG = MovieDetailViewModel.class.getSimpleName();

    private final AppRepository mRepository;
    private final Movie mMovie;

    private LiveData<Resource<List<TrailerMinimal>>> mTrailerCollection;
    private LiveData<Resource<List<ReviewMinimal>>> mReviewCollection;

    private LiveData<Boolean> mFavorite;


    private boolean mTrailerListCollapsed = true;
    private boolean mReviewListCollapsed = true;

    public MovieDetailViewModel(Movie movie, AppRepository repository) {
        Log.d(TAG, "MovieDetailViewModel has been created");
        mMovie = movie;
        mRepository = repository;
        // run asynchronously trailers & reviews fetching
        // find out if the movie marked as favorite (stored in the database)
        // we have to transform Long value to Boolean value
        // if Long != null it means that user marked this movie as favorite
        // and movie is stored in database
        // if Long == null - movie is not favorite
        mFavorite = Transformations.map(mRepository.dbIsFavoriteMovie(movie.getId()),
                new Function<Long, Boolean>() {
                    @Override
                    public Boolean apply(Long movieId) {
                        return movieId != null;
                    }
                }
        );
        setupTrailersRetrieving();
        setupReviewsRetrieving();
    }

    private void setupTrailersRetrieving() {
        if (mRepository.isFavoriteSelection()) {
            mTrailerCollection = Transformations.map(mRepository.dbLoadAllTrailers(mMovie.getId()),
                    new Function<List<TrailerMinimal>, Resource<List<TrailerMinimal>>>() {
                        @Override
                        public Resource<List<TrailerMinimal>> apply(List<TrailerMinimal> input) {
                            Resource<List<TrailerMinimal>> res = Resource.success(input);
                            return res;
                        }
                    });
        } else {
            mTrailerCollection = Transformations.map(mRepository.retrieveTrailerList(mMovie.getId().toString()),
                    new Function<Resource<TrailerCollection>, Resource<List<TrailerMinimal>>>() {
                        @Override
                        public Resource<List<TrailerMinimal>> apply(Resource<TrailerCollection> input) {
                            Resource<List<TrailerMinimal>> res;
                            if (input.status == Resource.Status.SUCCESS) {
                                List<TrailerMinimal> l =
                                        Converter.convertTrailer2TrailerMinimal(mMovie, input.data.getResults());
                                res = Resource.success(l);
                            } else {
                                res = Resource.error(input.message, null);
                            }
                            return res;
                        }
                    });
        }
    }

    private void setupReviewsRetrieving() {
        if (mRepository.isFavoriteSelection()) {
            mReviewCollection = Transformations.map(mRepository.dbLoadAllReviews(mMovie.getId()),
                    new Function<List<ReviewMinimal>, Resource<List<ReviewMinimal>>>() {
                        @Override
                        public Resource<List<ReviewMinimal>> apply(List<ReviewMinimal> input) {
                            Resource<List<ReviewMinimal>> res = Resource.success(input);
                            return res;
                        }
                    });
        } else {
            mReviewCollection = Transformations.map(mRepository.retrieveReviewList(mMovie.getId().toString()),
                    new Function<Resource<ReviewCollection>, Resource<List<ReviewMinimal>>>() {
                        @Override
                        public Resource<List<ReviewMinimal>> apply(Resource<ReviewCollection> input) {
                            Resource<List<ReviewMinimal>> res;
                            if (input.status == Resource.Status.SUCCESS) {
                                List<ReviewMinimal> l =
                                        Converter.convertReview2ReviewMinimal(mMovie, input.data.getResults());
                                res = Resource.success(l);
                            } else {
                                res = Resource.error(input.message, null);
                            }
                            return res;
                        }
                    });
        }
    }

    public void addMovieToFavorites() {
        Log.d(TAG, "addMovieToFavorites");
        List<TrailerMinimal> trailers = new ArrayList<>();
        if (mTrailerCollection.getValue().status == Resource.Status.SUCCESS) {
            trailers.addAll(mTrailerCollection.getValue().data);
        }
        List<ReviewMinimal> reviews = new ArrayList<>();
        if (mReviewCollection.getValue().status == Resource.Status.SUCCESS) {
            reviews.addAll(mReviewCollection.getValue().data);
        }
        mRepository.dbInsertMovie(mMovie, trailers, reviews);
    }

    public void removeMovieFromFavorites() {
        Log.d(TAG, "removeMovieFromFavorites");
        mRepository.dbDeleteMovie(mMovie);
    }

    public boolean isTrailerListCollapsed() {
        return mTrailerListCollapsed;
    }

    public void setTrailerListCollapsed(boolean trailerListCollapsed) {
        mTrailerListCollapsed = trailerListCollapsed;
    }

    public boolean isReviewListCollapsed() {
        return mReviewListCollapsed;
    }

    public void setReviewListCollapsed(boolean reviewListCollapsed) {
        mReviewListCollapsed = reviewListCollapsed;
    }

    public Movie getMovie() {
        return mMovie;
    }

    public LiveData<Resource<List<TrailerMinimal>>> getTrailerCollection() {
        return mTrailerCollection;
    }

    public LiveData<Resource<List<ReviewMinimal>>> getReviewCollection() {
        return mReviewCollection;
    }

    public LiveData<Boolean> isFavorite() {
        return mFavorite;
    }

    public boolean isFavoriteMode() {
        return mRepository.isFavoriteSelection();
    }

    // finalization

    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared");
        super.onCleared();
    }
}
