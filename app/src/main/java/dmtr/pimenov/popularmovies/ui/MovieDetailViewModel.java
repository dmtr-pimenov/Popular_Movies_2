package dmtr.pimenov.popularmovies.ui;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dmtr.pimenov.popularmovies.data.AppRepository;
import dmtr.pimenov.popularmovies.data.model.Backdrop;
import dmtr.pimenov.popularmovies.data.model.BackdropCollection;
import dmtr.pimenov.popularmovies.data.model.MovieDetail;
import dmtr.pimenov.popularmovies.data.model.Resource;
import dmtr.pimenov.popularmovies.data.model.Review;
import dmtr.pimenov.popularmovies.data.model.ReviewCollection;
import dmtr.pimenov.popularmovies.data.model.Trailer;
import dmtr.pimenov.popularmovies.data.model.TrailerCollection;
import dmtr.pimenov.popularmovies.util.MovieLoadingWatcher;

public class MovieDetailViewModel extends ViewModel {

    private static final String TAG = MovieDetailViewModel.class.getSimpleName();

    private final AppRepository mRepository;
    private final long mMovieId;

    private LiveData<Resource<List<Trailer>>> mTrailerCollection;
    private LiveData<Resource<List<Review>>> mReviewCollection;
    private LiveData<Resource<List<Backdrop>>> mBackdropCollection;
    private LiveData<Resource<MovieDetail>> mMovieDetail;
    private MovieLoadingWatcher mMovieLoadingWatcher = new MovieLoadingWatcher();

    private LiveData<Boolean> mIsFavorite;

    public MovieDetailViewModel(long movieId, AppRepository repository) {
        Log.d(TAG, "MovieDetailViewModel has been created");
        mMovieId = movieId;
        mRepository = repository;
        mIsFavorite = mRepository.dbIsFavoriteMovie(mMovieId);

    }

    public LiveData<Resource<MovieDetail>> getMovieDetail() {
        if (mMovieDetail == null) {
            if (mRepository.isFavoriteMode()) {
                mMovieDetail = Transformations.map(mRepository.dbLoadMovieDetailById(mMovieId),
                        new Function<MovieDetail, Resource<MovieDetail>>() {
                            @Override
                            public Resource<MovieDetail> apply(MovieDetail input) {
                                Resource<MovieDetail> res = Resource.success(input);
                                mMovieLoadingWatcher.movieLoaded();
                                return res;
                            }
                        });
            } else {
                mMovieDetail = Transformations.map(mRepository.retrieveMoveDetail(mMovieId),
                        new Function<Resource<MovieDetail>, Resource<MovieDetail>>() {
                            @Override
                            public Resource<MovieDetail> apply(Resource<MovieDetail> input) {
                                if (input.status == Resource.Status.SUCCESS) {
                                    mMovieLoadingWatcher.movieLoaded();
                                }
                                return input;
                            }
                        });
            }
        }
        return mMovieDetail;
    }

    public LiveData<Resource<List<Backdrop>>> getBackdropCollection() {
        if (mBackdropCollection == null) {
            if (mRepository.isFavoriteMode()) {
                mBackdropCollection = Transformations.map(mRepository.dbLoadAllBackdrops(mMovieId),
                        new Function<List<Backdrop>, Resource<List<Backdrop>>>() {
                            @Override
                            public Resource<List<Backdrop>> apply(List<Backdrop> input) {
                                Resource<List<Backdrop>> res = Resource.success(input);
                                mMovieLoadingWatcher.backdropsLoaded();
                                return res;
                            }
                        });
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
                                mMovieLoadingWatcher.backdropsLoaded();
                                return res;
                            }
                        });
            }
        }
        return mBackdropCollection;
    }

    public LiveData<Resource<List<Trailer>>> getTrailerCollection() {
        if (mTrailerCollection == null) {
            if (mRepository.isFavoriteMode()) {
                mTrailerCollection = Transformations.map(mRepository.dbLoadAllTrailers(mMovieId),
                        new Function<List<Trailer>, Resource<List<Trailer>>>() {
                            @Override
                            public Resource<List<Trailer>> apply(List<Trailer> input) {
                                Resource<List<Trailer>> res = Resource.success(input);
                                mMovieLoadingWatcher.trailersLoaded();
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
                                mMovieLoadingWatcher.trailersLoaded();
                                return res;
                            }
                        });
            }
        }
        return mTrailerCollection;
    }

    public LiveData<Resource<List<Review>>> getReviewCollection() {
        if (mReviewCollection == null) {
            if (mRepository.isFavoriteMode()) {
                mReviewCollection = Transformations.map(mRepository.dbLoadAllReviews(mMovieId),
                        new Function<List<Review>, Resource<List<Review>>>() {
                            @Override
                            public Resource<List<Review>> apply(List<Review> input) {
                                Resource<List<Review>> res = Resource.success(input);
                                mMovieLoadingWatcher.reviewsLoaded();
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
                                mMovieLoadingWatcher.reviewsLoaded();
                                return res;
                            }
                        });
            }
        }
        return mReviewCollection;
    }

    @SuppressWarnings("ConstantConditions")
    public void addMovieToFavorites() {
        Log.d(TAG, "addMovieToFavorites");

        List<Trailer> trailers = new ArrayList<>();
        if (isDataReadyToDb(mTrailerCollection)) {
            trailers.addAll(mTrailerCollection.getValue().data);
        }
        List<Review> reviews = new ArrayList<>();
        if (isDataReadyToDb(mReviewCollection)) {
            reviews.addAll(mReviewCollection.getValue().data);
        }

        List<Backdrop> backdrops = new ArrayList<>();
        if (isDataReadyToDb(mBackdropCollection)) {
            backdrops.addAll(mBackdropCollection.getValue().data);
        }

        MovieDetail movie = mMovieDetail.getValue().data;
        mRepository.dbInsertMovie(movie, trailers, reviews, movie.getGenres(), backdrops);
    }

    private boolean isDataReadyToDb(LiveData<? extends Resource<?>> anyData) {
        return anyData != null && anyData.getValue() != null
                && anyData.getValue().status == Resource.Status.SUCCESS;
    }

    public void removeMovieFromFavorites() {
        Log.d(TAG, "removeMovieFromFavorites");
        mRepository.dbDeleteMovieById(mMovieId);
    }

    public LiveData<Boolean> isFavorite() {
        return mIsFavorite;
    }

    public LiveData<Boolean> isMovieInformationLoaded() {
        return mMovieLoadingWatcher.isMovieInformationLoaded();
    }

    public boolean isFavoriteMode() {
        return mRepository.isFavoriteMode();
    }

    // finalization
    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared");
        super.onCleared();
    }
}
