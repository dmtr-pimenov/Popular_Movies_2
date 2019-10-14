package dmtr.pimenov.popularmovies.ui;

import android.annotation.TargetApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import dmtr.pimenov.popularmovies.R;
import dmtr.pimenov.popularmovies.data.model.Backdrop;
import dmtr.pimenov.popularmovies.data.model.MovieDetail;
import dmtr.pimenov.popularmovies.data.model.Resource;
import dmtr.pimenov.popularmovies.data.model.Review;
import dmtr.pimenov.popularmovies.data.model.Trailer;
import dmtr.pimenov.popularmovies.data.network.NetworkApi;
import dmtr.pimenov.popularmovies.databinding.ActivityMovieDetailBinding;
import dmtr.pimenov.popularmovies.ui.adapter.BackdropAdapter;
import dmtr.pimenov.popularmovies.ui.adapter.MovieFragmentAdapter;
import dmtr.pimenov.popularmovies.ui.factory.MovieDetailViewModelFactory;
import dmtr.pimenov.popularmovies.util.InjectorUtil;
import dmtr.pimenov.popularmovies.util.UiUtils;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID";
    public static final String EXTRA_MOVIE_TITLE = "EXTRA_MOVIE_TITLE";
    public static final String EXTRA_POSTER_PATH = "EXTRA_POSTER_PATH";
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private ActivityMovieDetailBinding mBinding;
    private MovieDetailViewModel mViewModel;
    private Toast mToast;
    private ViewPagerChanger mViewPagerChanger;

    private boolean isMovieDeleted = false;
    private boolean isFavoriteMode = false;

    private boolean isTrailersLoaded = false;
    private boolean isReviewsLoaded = false;

    private boolean disableTransition = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        disableTransition = savedInstanceState != null;

        if (UiUtils.isTransitionAvailable(this) && !disableTransition) {
            supportPostponeEnterTransition();
        }

        super.onCreate(savedInstanceState);

        Intent startIntent = getIntent();
        if (!startIntent.hasExtra(EXTRA_MOVIE_ID)) {
            Log.e(TAG, "Incredible! No movie information");
            finish();
            return;
        }

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        mBinding.setLifecycleOwner(this);

        setSupportActionBar(mBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        long movieId = startIntent.getLongExtra(EXTRA_MOVIE_ID, -1);
        String movieTitle = startIntent.getStringExtra(EXTRA_MOVIE_TITLE);
        if (movieTitle != null) {
            setTitle(movieTitle);
        }
        String posterPath = startIntent.getStringExtra(EXTRA_POSTER_PATH);
        setupPosterImage(posterPath);

        setupViewModel(movieId);
        getMovieDetail();
        setupAppBarListener();
        if (!UiUtils.isTransitionAvailable(this) || disableTransition) {
            getBackdropCollection();
            getRestCollections();
        }
    }

    private void setupViewModel(long movieId) {
        MovieDetailViewModelFactory factory = InjectorUtil.provideMovieDetailViewModelFactory(
                this.getApplicationContext(), movieId);
        mViewModel = ViewModelProviders.of(this, factory).get(MovieDetailViewModel.class);
        isFavoriteMode = mViewModel.isFavoriteMode();
        setVisibilityFavoriteButton();
    }

    private void getMovieDetail() {
        mViewModel.getMovieDetail().observe(this, new Observer<Resource<MovieDetail>>() {
            @Override
            public void onChanged(@Nullable Resource<MovieDetail> movieDetailResource) {
                mViewModel.getMovieDetail().removeObserver(this);
                if (movieDetailResource != null) {
                    MovieDetail movieDetail = movieDetailResource.data;
                    if (movieDetailResource.status == Resource.Status.SUCCESS && movieDetail != null) {
                        populateUi(movieDetailResource.data, mViewModel);
                    } else {
                        showToastMessage(R.string.error_loading_movie_info);
                    }
                } else {
                    showToastMessage(R.string.error_loading_movie_info);
                }
            }
        });
    }

    private void getBackdropCollection() {
        mViewModel.getBackdropCollection().observe(this, new Observer<Resource<List<Backdrop>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Backdrop>> backdropCollectionResource) {
                mViewModel.getBackdropCollection().removeObserver(this);
                if (backdropCollectionResource != null &&
                        backdropCollectionResource.status == Resource.Status.SUCCESS) {
                    setupBackdropViewPager(backdropCollectionResource.data);
                }
                Log.d(TAG, "Backdrop list received");
            }
        });
    }

    private void getRestCollections() {

        mViewModel.getTrailerCollection().observe(this, new Observer<Resource<List<Trailer>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Trailer>> trailerCollectionResource) {
                mViewModel.getTrailerCollection().removeObserver(this);
                isTrailersLoaded = true;
                setupFragmentViewPager();
                Log.d(TAG, "Trailer list received");
            }
        });

        mViewModel.getReviewCollection().observe(this, new Observer<Resource<List<Review>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Review>> reviewCollectionResource) {
                mViewModel.getReviewCollection().removeObserver(this);
                isReviewsLoaded = true;
                setupFragmentViewPager();
                Log.d(TAG, "Review list received");
            }
        });
    }

    private void populateUi(MovieDetail movieDetail, MovieDetailViewModel viewModel) {
        mBinding.setMovieDetail(movieDetail);
        mBinding.setViewModel(viewModel);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startDelayedTransition() {

        final Transition enterTransition = getWindow().getEnterTransition();
        if (enterTransition != null) {
            enterTransition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {}

                @Override
                public void onTransitionEnd(Transition transition) {
                    getBackdropCollection();
                    getRestCollections();
                    enterTransition.removeListener(this);
                }

                @Override
                public void onTransitionCancel(Transition transition) {}

                @Override
                public void onTransitionPause(Transition transition) {}

                @Override
                public void onTransitionResume(Transition transition) {}
            });
        }
        supportStartPostponedEnterTransition();
    }

    /**
     * If user clicks on the favorite checkbox and Movie is not in local database
     * then Movie will be added into DB
     * otherwise The Movie will be removed form DB
     */
    public void processCheckBoxOnClick(View v) {
        boolean isFavorite = mBinding.detailInfo.checkFavoriteMovie.isChecked();
        Log.d(TAG, "processCheckBoxOnClick: " + isFavorite);
        int messageId;

        if (isFavorite) {
            mViewModel.addMovieToFavorites();
            messageId = R.string.added_to_favorites;
        } else {
            mViewModel.removeMovieFromFavorites();
            isMovieDeleted = true;
            messageId = R.string.removed_from_favorites;
        }

        showToastMessage(messageId);
    }

    private void setupFragmentViewPager() {
        if (isTrailersLoaded && isReviewsLoaded) {
            MovieFragmentAdapter adapter = new MovieFragmentAdapter(this, getSupportFragmentManager());
            mBinding.viewPagerFragment.setAdapter(adapter);
            mBinding.tabFragment.setupWithViewPager(mBinding.viewPagerFragment);
        }
    }

    private void setupBackdropViewPager(List<Backdrop> backdrops) {

        BackdropAdapter adapter = new BackdropAdapter(MovieDetailActivity.this, backdrops);
        mBinding.detailInfo.viewPagerBackdrops.setAdapter(adapter);

        TabLayout tabLayout = mBinding.detailInfo.tablayoutBackdropIndicator;
        ViewPager viewPager = mBinding.detailInfo.viewPagerBackdrops;
        tabLayout.setupWithViewPager(viewPager);

        LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(false);
        }
        mViewPagerChanger = new ViewPagerChanger(this,
                mBinding.detailInfo.viewPagerBackdrops, 3000);
        mViewPagerChanger.start();
    }

    private void setupPosterImage(@Nullable String posterPath) {

        if (posterPath != null) {
            Drawable error = ContextCompat.getDrawable(this, R.drawable.ic_error);
            // poster image
            String posterUrl = NetworkApi.getPosterUrl(posterPath, NetworkApi.PosterSize.W185);
            if (UiUtils.isTransitionAvailable(this) && !disableTransition) {
                Picasso.with(this).load(posterUrl)
                        .error(error)
                        .into(mBinding.detailInfo.imagePoster, new Callback() {
                            @Override
                            public void onSuccess() {
                                startDelayedTransition();
                            }

                            @Override
                            public void onError() {
                                startDelayedTransition();
                            }
                        });
            } else {
                Picasso.with(this).load(posterUrl)
                        .error(error)
                        .into(mBinding.detailInfo.imagePoster);
            }
        } else {
            if (UiUtils.isTransitionAvailable(this) && !disableTransition) {
                startDelayedTransition();
            }
        }
    }

    private void setupAppBarListener() {
        mBinding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // yes, this is possible, especially on slow network connection
                if (mViewPagerChanger == null) {
                    return;
                }
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // Collapsed
                    if (mViewPagerChanger.isStarted()) {
                        mViewPagerChanger.stop();
                    }
                } else if (verticalOffset == 0) {
                    // Expanded
                    if (!mViewPagerChanger.isStarted()) {
                        mViewPagerChanger.start();
                    }
                } else {
                    // Somewhere in between
                    if (!mViewPagerChanger.isStarted()) {
                        mViewPagerChanger.start();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if ((isMovieDeleted && isFavoriteMode) || isPosterPartiallyInvisible()) {
                finish();
            } else {
                super.onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if ((isMovieDeleted && isFavoriteMode) || isPosterPartiallyInvisible()) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private boolean isPosterPartiallyInvisible() {
        int[] location = new int[2];
        int height = mBinding.detailInfo.imagePoster.getHeight();
        mBinding.detailInfo.imagePoster.getLocationInWindow(location);
        int top = location[1];
        boolean partiallyInvisible;
        if (top < 0 && Math.abs(top) >= height / 4) {
            partiallyInvisible = true;
        } else {
            partiallyInvisible = false;
        }
        return partiallyInvisible;
    }

    private void setVisibilityFavoriteButton() {
        mViewModel.isMovieInformationLoaded().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                ViewGroup root = findViewById(android.R.id.content);
                Fade f = new Fade();
                f.setDuration(500);
                TransitionManager.beginDelayedTransition(root, f);
                TransitionManager.beginDelayedTransition(root, f);
                mBinding.detailInfo.checkFavoriteMovie.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Helper method
     *
     * @param messageId
     */
    private void showToastMessage(@StringRes int messageId) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this,
                messageId,
                Toast.LENGTH_SHORT);
        mToast.show();
    }
}
