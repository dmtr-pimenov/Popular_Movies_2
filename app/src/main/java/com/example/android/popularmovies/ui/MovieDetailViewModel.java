package com.example.android.popularmovies.ui;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies.data.AppRepository;
import com.example.android.popularmovies.data.model.Backdrop;
import com.example.android.popularmovies.data.model.BackdropCollection;
import com.example.android.popularmovies.data.model.Genre;
import com.example.android.popularmovies.data.model.Language;
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
    private LiveData<Resource<MovieDetail>> mMovieDetail;

    private LiveData<Boolean> mIsFavorite;


    private boolean mTrailerListCollapsed = true;
    private boolean mReviewListCollapsed = true;

    public MovieDetailViewModel(long movieId, AppRepository repository) {
        Log.d(TAG, "MovieDetailViewModel has been created");
        mMovieId = movieId;
        mRepository = repository;
        mIsFavorite = mRepository.dbIsFavoriteMovie(mMovieId);
        loadMovieDetail();
        setupBackdropRetrieving();
        setupTrailersRetrieving();
        setupReviewsRetrieving();
    }

    private void loadMovieDetail() {
        if (mRepository.isFavoriteSelection()) {
            mMovieDetail = Transformations.map(mRepository.dbLoadMovieDetailById(mMovieId),
                    new Function<MovieDetail, Resource<MovieDetail>>() {
                        @Override
                        public Resource<MovieDetail> apply(MovieDetail input) {
                            Resource<MovieDetail> res = Resource.success(input);
                            return res;
                        }
                    });
        } else {
            mMovieDetail = mRepository.retrieveMoveDetail(mMovieId);
        }
    }

    private void setupBackdropRetrieving() {
        if (mRepository.isFavoriteSelection()) {
            mBackdropCollection = Transformations.map(mRepository.dbLoadAllBackdrops(mMovieId),
                    new Function<List<Backdrop>, Resource<List<Backdrop>>>() {
                        @Override
                        public Resource<List<Backdrop>> apply(List<Backdrop> input) {
                            Resource<List<Backdrop>> res = Resource.success(input);
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

    // todo don't forget to disable checkbox Add to Favorite if MoveDetail retrieved with error
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

        List<Backdrop> backdrops = new ArrayList<>();
        if (mBackdropCollection.getValue().status == Resource.Status.SUCCESS) {
            backdrops.addAll(mBackdropCollection.getValue().data);
        }

        MovieDetail movie = mMovieDetail.getValue().data;
        mRepository.dbInsertMovie(movie, trailers, reviews, movie.getGenres(), backdrops);
    }

    public void removeMovieFromFavorites() {
        Log.d(TAG, "removeMovieFromFavorites");
        mRepository.dbDeleteMovieById(mMovieId);
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

    public LiveData<Resource<MovieDetail>> getMovieDetail() {
        return mMovieDetail;
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

    public String getGenresString(@NonNull List<Genre> genres) {
        StringBuilder sb = new StringBuilder();
        for (Genre g : genres) {
            String gName = mRepository.getGenreById(g.getGenreId());
            if (gName != null) {
                sb.append(" " + gName + ",");
            }
        }
        String res = "";
        int len = sb.length();
        // remove last comma
        if (sb.length() > 0) {
            res = sb.substring(0, len - 1).trim();
        }
        return res;
    }

    public Language getLanguageByCode(String code) {
        return mRepository.getLanguageByCode(code);
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
