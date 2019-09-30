package com.example.android.popularmovies.ui;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.android.popularmovies.data.AppRepository;
import com.example.android.popularmovies.data.model.Backdrop;
import com.example.android.popularmovies.data.model.BackdropCollection;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.MovieDetail;
import com.example.android.popularmovies.data.model.Resource;
import com.example.android.popularmovies.data.model.Review;
import com.example.android.popularmovies.data.model.ReviewCollection;
import com.example.android.popularmovies.data.model.Trailer;
import com.example.android.popularmovies.data.model.TrailerCollection;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailViewModel extends ViewModel {

    private static final String TAG = MovieDetailViewModel.class.getSimpleName();

    private final AppRepository mRepository;
    private final long mMovieId;

    private LiveData<Resource<List<Trailer>>> mTrailerCollection;
    private LiveData<Resource<List<Review>>> mReviewCollection;
    private LiveData<Resource<List<Backdrop>>> mBackdropCollection;
    private LiveData<MovieDetail> mMovieDetail;

    private LiveData<Boolean> mIsFavorite;


    private boolean mTrailerListCollapsed = true;
    private boolean mReviewListCollapsed = true;

    public MovieDetailViewModel(long movieId, AppRepository repository) {
        Log.d(TAG, "MovieDetailViewModel has been created");
        mMovieId = movieId;
        mRepository = repository;
        // run asynchronously trailers & reviews fetching
        // find out if the movie marked as favorite (stored in the database)
        // we have to transform Long value to Boolean value
        // if Long != null it means that user marked this movie as favorite
        // and movie is stored in database
        // if Long == null - movie is not favorite
        mIsFavorite = mRepository.dbIsFavoriteMovie(mMovieId);
        setupBackdropRetrieving();
        setupTrailersRetrieving();
        setupReviewsRetrieving();
    }

    private void setupBackdropRetrieving() {
        if (mRepository.isFavoriteSelection()) {

            mBackdropCollection = new MutableLiveData<>();
            ((MutableLiveData) mBackdropCollection).setValue(Resource.error("Error", null));

        } else {
            mBackdropCollection = Transformations.map(mRepository.retrieveBackdropCollection(mMovieId),
                    new Function<Resource<BackdropCollection>, Resource<List<Backdrop>>>() {
                        @Override
                        public Resource<List<Backdrop>> apply(Resource<BackdropCollection> input) {
                            Resource<List<Backdrop>> res;
                            if (input.status == Resource.Status.SUCCESS) {
                                List<Backdrop> l = input.data.getBackdrops();
                                res = Resource.success(l);
                            } else {
                                res = Resource.error(input.message, null);
                            }
                            return res;
                        }
                    });
        }
    }

    private void setupTrailersRetrieving() {
        if (mRepository.isFavoriteSelection()) {
            mTrailerCollection = Transformations.map(mRepository.dbLoadAllTrailers(mMovieId),
                    new Function<List<Trailer>, Resource<List<Trailer>>>() {
                        @Override
                        public Resource<List<Trailer>> apply(List<Trailer> input) {
                            Resource<List<Trailer>> res = Resource.success(input);
                            return res;
                        }
                    });
        } else {
            mTrailerCollection = Transformations.map(mRepository.retrieveTrailerCollection(mMovieId),
                    new Function<Resource<TrailerCollection>, Resource<List<Trailer>>>() {
                        @Override
                        public Resource<List<Trailer>> apply(Resource<TrailerCollection> input) {
                            Resource<List<Trailer>> res;
                            if (input.status == Resource.Status.SUCCESS) {
                                List<Trailer> l = input.data.getResults();
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
            mReviewCollection = Transformations.map(mRepository.dbLoadAllReviews(mMovieId),
                    new Function<List<Review>, Resource<List<Review>>>() {
                        @Override
                        public Resource<List<Review>> apply(List<Review> input) {
                            Resource<List<Review>> res = Resource.success(input);
                            return res;
                        }
                    });
        } else {
            mReviewCollection = Transformations.map(mRepository.retrieveReviewCollection(mMovieId),
                    new Function<Resource<ReviewCollection>, Resource<List<Review>>>() {
                        @Override
                        public Resource<List<Review>> apply(Resource<ReviewCollection> input) {
                            Resource<List<Review>> res;
                            if (input.status == Resource.Status.SUCCESS) {
                                List<Review> l = input.data.getResults();
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
        List<Trailer> trailers = new ArrayList<>();
        if (mTrailerCollection.getValue().status == Resource.Status.SUCCESS) {
            trailers.addAll(mTrailerCollection.getValue().data);
        }
        List<Review> reviews = new ArrayList<>();
        if (mReviewCollection.getValue().status == Resource.Status.SUCCESS) {
            reviews.addAll(mReviewCollection.getValue().data);
        }
//        mRepository.dbInsertMovie(mMovie, trailers, reviews);
    }

    public void removeMovieFromFavorites() {
        Log.d(TAG, "removeMovieFromFavorites");
//        mRepository.dbDeleteMovie(mMovie);
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
//        return mMovie;
        return null;
    }

    public LiveData<Resource<List<Backdrop>>> getBackdropCollection() {
        return mBackdropCollection;
    }

    public LiveData<Resource<List<Trailer>>> getTrailerCollection() {
        return mTrailerCollection;
    }

    public LiveData<Resource<List<Review>>> getReviewCollection() {
        return mReviewCollection;
    }

    public LiveData<Boolean> isFavorite() {
        return mIsFavorite;
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
