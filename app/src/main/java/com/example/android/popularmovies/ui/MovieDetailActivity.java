package com.example.android.popularmovies.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.model.Backdrop;
import com.example.android.popularmovies.data.model.MovieDetail;
import com.example.android.popularmovies.data.model.Resource;
import com.example.android.popularmovies.data.model.Review;
import com.example.android.popularmovies.data.model.Trailer;
import com.example.android.popularmovies.data.network.NetworkApi;
import com.example.android.popularmovies.databinding.ActivityMovieDetailBinding;
import com.example.android.popularmovies.ui.adapter.BackdropAdapter;
import com.example.android.popularmovies.ui.adapter.MovieFragmentAdapter;
import com.example.android.popularmovies.ui.factory.MovieDetailViewModelFactory;
import com.example.android.popularmovies.util.InjectorUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID";
    public static final String EXTRA_MOVIE_TITLE = "EXTRA_MOVIE_TITLE";
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private ActivityMovieDetailBinding mBinding;
    private MovieDetailViewModel mViewModel;
    private Toast mToast;
    private ViewPagerChanger mViewPagerChanger;

    private boolean isMovieDeleted = false;
    private boolean isFavoriteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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
        setTitle(movieTitle);

        setupViewModel(movieId);
        setupAppBarListener();

        isFavoriteMode = mViewModel.isFavoriteMode();
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

    private void setupViewModel(long movieId) {

        MovieDetailViewModelFactory factory = InjectorUtil.provideMovieDetailViewModelFactory(this, movieId);
        mViewModel = ViewModelProviders.of(this, factory).get(MovieDetailViewModel.class);

        mViewModel.getMovieDetail().observe(this, new Observer<Resource<MovieDetail>>() {
            @Override
            public void onChanged(@Nullable Resource<MovieDetail> movieDetailResource) {
                mViewModel.getMovieDetail().removeObserver(this);
                MovieDetail movieDetail = movieDetailResource.data;
                if (movieDetailResource.status == Resource.Status.SUCCESS && movieDetail != null) {
                    populateUi(movieDetailResource.data, mViewModel);
                    observeRestCollections();
                    setupFragmentViewPager();
                } else {
                    showToastMessage(R.string.error_loading_movie_info);
                }
            }
        });
    }

    private void observeRestCollections() {
        mViewModel.getBackdropCollection().observe(this, new Observer<Resource<List<Backdrop>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Backdrop>> backdropCollectionResource) {
                mViewModel.getBackdropCollection().removeObserver(this);
                if (backdropCollectionResource == null ||
                        backdropCollectionResource.status == Resource.Status.ERROR) {
                    showToastMessage(R.string.error_loading_backdrop_collection);
                } else {
                    setupBackdropViewPager(backdropCollectionResource.data);
                }
            }
        });

        mViewModel.getTrailerCollection().observe(this, new Observer<Resource<List<Trailer>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Trailer>> trailerCollectionResource) {
                mViewModel.getTrailerCollection().removeObserver(this);
                if (trailerCollectionResource == null ||
                        trailerCollectionResource.status == Resource.Status.ERROR) {
                    showToastMessage(R.string.error_loading_trailer_collection);
                }
                Log.d(TAG, "Trailer list received");
            }
        });

        mViewModel.getReviewCollection().observe(this, new Observer<Resource<List<Review>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Review>> reviewCollectionResource) {
                mViewModel.getReviewCollection().removeObserver(this);
                if (reviewCollectionResource == null ||
                        reviewCollectionResource.status == Resource.Status.ERROR) {
                    showToastMessage(R.string.error_loading_review_collection);
                }
                Log.d(TAG, "Review list received");
            }
        });
    }

    private void populateUi(@NonNull MovieDetail movieDetail, MovieDetailViewModel viewModel) {
        mBinding.setMovieDetail(movieDetail);
        mBinding.setViewModel(viewModel);
        setupPosterImage(movieDetail);
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
        MovieFragmentAdapter adapter = new MovieFragmentAdapter(this, getSupportFragmentManager());
        mBinding.viewPagerFragment.setAdapter(adapter);
        mBinding.tabFragment.setupWithViewPager(mBinding.viewPagerFragment);
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

    private void setupPosterImage(@NonNull  MovieDetail movieDetail) {

        Drawable error = ContextCompat.getDrawable(this, R.drawable.ic_error);

        // poster image
        String posterUrl = NetworkApi.getPosterUrl(movieDetail.getPosterPath(), NetworkApi.PosterSize.W185);
        Picasso.with(this).load(posterUrl)
                .error(error)
                .into(mBinding.detailInfo.imagePoster);
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
