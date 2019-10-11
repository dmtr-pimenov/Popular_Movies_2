package dmtr.pimenov.popularmovies.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.MainThread;

public class MovieLoadingWatcher {
    private boolean mMovieLoaded = false;
    private boolean mBackdropsLoaded = false;
    private boolean mTrailersLoaded = false;
    private boolean mReviewsLoaded = false;

    private MutableLiveData<Boolean> mMovieInformationLoaded = new MutableLiveData<>();

    public LiveData<Boolean> isMovieInformationLoaded() {
        return mMovieInformationLoaded;
    }

    @MainThread
    private void update() {
        if (mMovieLoaded && mBackdropsLoaded
                && mTrailersLoaded && mReviewsLoaded) {
            mMovieInformationLoaded.setValue(true);
        }
    }

    @MainThread
    public void movieLoaded() {
        mMovieLoaded = true;
        update();
    }

    @MainThread
    public void backdropsLoaded() {
        mBackdropsLoaded = true;
        update();
    }

    @MainThread
    public void trailersLoaded() {
        mTrailersLoaded = true;
        update();
    }

    @MainThread
    public void reviewsLoaded() {
        mReviewsLoaded = true;
        update();
    }
}
