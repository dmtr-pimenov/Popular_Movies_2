package com.example.android.popularmovies.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.model.Backdrop;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.Resource;
import com.example.android.popularmovies.data.model.ReviewMinimal;
import com.example.android.popularmovies.data.model.TrailerMinimal;
import com.example.android.popularmovies.data.network.NetworkApi;
import com.example.android.popularmovies.databinding.ActivityMovieDetailBinding;
import com.example.android.popularmovies.ui.adapter.BackdropAdapter;
import com.example.android.popularmovies.ui.adapter.MovieFragmentAdapter;
import com.example.android.popularmovies.ui.adapter.ReviewListAdapter;
import com.example.android.popularmovies.ui.adapter.TrailerListAdapter;
import com.example.android.popularmovies.ui.factory.MovieDetailViewModelFactory;
import com.example.android.popularmovies.util.InjectorUtil;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements ReviewListAdapter.OnLinkClickListener {

    public static final String EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID";
    public static final String EXTRA_MOVIE_TITLE = "EXTRA_MOVIE_TITLE";
    public static final String YOUTUBE_BASE_URI = "https://youtube.com/watch?v=";
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
//        setupImageViews(movie);
//        setupRatingViews(movie);
        setupBackdropViewPager();
        setupViewPager();

        mBinding.detailedInfo.textTitle.setText(movieTitle);
//        mBinding.detailedInfo.tvReleaseDate.setText(formatDate(movie.getReleaseDate()));

        mBinding.detailedInfo.checkFavoriteMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processCheckBoxOnClick();
            }
        });

        isFavoriteMode = mViewModel.isFavoriteMode();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isMovieDeleted && isFavoriteMode) {
                finish();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isMovieDeleted && isFavoriteMode) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void setupViewModel(long movieId) {

        MovieDetailViewModelFactory factory = InjectorUtil.provideMovieDetailViewModelFactory(this, movieId);
        mViewModel = ViewModelProviders.of(this, factory).get(MovieDetailViewModel.class);

        mViewModel.getBackdropCollection().observe(this, new Observer<Resource<List<Backdrop>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Backdrop>> backdropCollectionResource) {
                mViewModel.getBackdropCollection().removeObserver(this);
                if (backdropCollectionResource == null ||
                        backdropCollectionResource.status == Resource.Status.ERROR) {
                    showToastMessage(R.string.error_loading_backdrop_collection);
                } else {
                    mViewModel.getBackdropCollection().removeObserver(this);
                    BackdropAdapter adapter = new BackdropAdapter(MovieDetailActivity.this,
                            backdropCollectionResource.data);
                    mBinding.detailedInfo.viewPagerBackdrops.setAdapter(adapter);
                }
            }
        });

/*
        mViewModel.getTrailerCollection().observe(this, new Observer<Resource<List<TrailerMinimal>>>() {
        // todo remove observer
            @Override
            public void onChanged(@Nullable Resource<List<TrailerMinimal>> trailerCollectionResource) {
                mViewModel.getTrailerCollection().removeObserver(this);
                if (trailerCollectionResource == null ||
                        trailerCollectionResource.status == Resource.Status.ERROR) {
                    showToastMessage(R.string.error_loading_trailer_collection);
                } else {
                    setupTrailerList(trailerCollectionResource.data);
                }
            }
        });

        mViewModel.getReviewCollection().observe(this, new Observer<Resource<List<ReviewMinimal>>>() {
        // todo remove observer
            @Override
            public void onChanged(@Nullable Resource<List<ReviewMinimal>> reviewCollectionResource) {
                mViewModel.getReviewCollection().removeObserver(this);
                if (reviewCollectionResource == null ||
                        reviewCollectionResource.status == Resource.Status.ERROR) {
                    showToastMessage(R.string.error_loading_review_collection);
                } else {
                    setupReviewList(reviewCollectionResource.data);
                }
            }
        });
*/

        /*
         * If the App gets the Data from network we have to inform
         * user whether the Movie already added into the local Database or not
         * For this we ask ViewModel to check if the Movie exists in the local Db
         */

        mViewModel.isFavorite().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isFavorite) {
                mViewModel.isFavorite().removeObserver(this);
                Log.d(TAG, "Movie is favorite: " + isFavorite);
                mBinding.detailedInfo.checkFavoriteMovie.setChecked(isFavorite);
            }
        });
    }

    /**
     * If user clicks on the favorite checkbox and Movie is not local database
     * the Movie will be added into DB
     * otherwise The Movie will be removed form DB
     */
    private void processCheckBoxOnClick() {
        boolean isFavorite = mBinding.detailedInfo.checkFavoriteMovie.isChecked();
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

    private void setupViewPager() {
        MovieFragmentAdapter adapter = new MovieFragmentAdapter(this, getSupportFragmentManager());
        mBinding.viewPagerFragment.setAdapter(adapter);
        mBinding.tabFragment.setupWithViewPager(mBinding.viewPagerFragment);
    }

    private void setupBackdropViewPager() {

        TabLayout tabLayout = mBinding.detailedInfo.tablayoutBackdropIndicator;
        ViewPager viewPager = mBinding.detailedInfo.viewPagerBackdrops;
        tabLayout.setupWithViewPager(viewPager);

        LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(false);
        }
        mViewPagerChanger = new ViewPagerChanger(this,
                mBinding.detailedInfo.viewPagerBackdrops, 3000);
        mViewPagerChanger.start();
    }

    private void setupImageViews(Movie movie) {

        Drawable placeholder = ContextCompat.getDrawable(this, R.drawable.ic_file_download);
        Drawable error = ContextCompat.getDrawable(this, R.drawable.ic_error);

        // poster image
        String posterUrl = NetworkApi.getPosterUrl(movie.getPosterPath(), NetworkApi.PosterSize.W185);
        Picasso.with(this).load(posterUrl)
                .error(error)
                .into(mBinding.detailedInfo.imagePoster);
    }

/*
    private void setupRatingViews(Movie movie) {
        Float voteAverage = movie.getVoteAverage();
        if (voteAverage == null) {
            voteAverage = 0f;
        }
        float numStars = mBinding.ratingMovieRating.getNumStars();
        float maxRating = (float) getResources().getInteger(R.integer.max_rating_value);
        float rating = voteAverage * numStars / maxRating;
        mBinding.ratingMovieRating.setRating(rating);

        String ratingString = DecimalFormat.getNumberInstance().format(voteAverage) + "/10";
        mBinding.textRatingStr.setText(ratingString);
    }
*/

    // todo move this method to Util class

    /**
     * Convert String representation of Release date to
     * short form representation
     *
     * @param releaseDate String representation of the Release Date. Format yyyy-MM-dd. Example: 2018-10-19
     * @return String representation of the Release Date. Format MMMM yyyy. Example: march 2018
     */
    private String formatDate(String releaseDate) {
        String result;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat();
            int jsonDateFormat = R.string.json_date_format;
            sdf.applyPattern(getString(jsonDateFormat));
            Date date = sdf.parse(releaseDate);

            int displayData = R.string.display_date_format;
            sdf.applyPattern(getString(displayData));
            result = sdf.format(date);

        } catch (ParseException ex) {
            result = "parse error";
        }
        return result;
    }

    private void setupAppBarListener() {
        mBinding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
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

/*
    private void setupTrailerList(final List<TrailerMinimal> trailerList) {

        final ListView trailers = mBinding.lvTrailers;
        TrailerListAdapter adapter =
                new TrailerListAdapter(this, trailerList);
        trailers.setAdapter(adapter);

        trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrailerMinimal item = (TrailerMinimal) trailers.getAdapter().getItem(position);
                showTrailer(item);
            }
        });

        if (mViewModel.isTrailerListCollapsed()) {
            mBinding.lvTrailers.setVisibility(View.GONE);
        } else {
            mBinding.lvTrailers.setVisibility(View.VISIBLE);
        }

        mBinding.cardview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (trailerList.size() == 0) {
                    showToastMessage(R.string.empty_trailer_list);
                    return;
                }

                boolean listState = mViewModel.isTrailerListCollapsed();
                // as result of the imageView click collapse button should
                // change state of button itself
                // and ListView visibility
                if (listState) {
                    // list is in collapsed state, will have state - expanded
                    mBinding.lvTrailers.setVisibility(View.VISIBLE);
                    mBinding.ivTrailerCollapseButton.setImageResource(R.drawable.ic_keyboard_arrow_up);
                    mViewModel.setTrailerListCollapsed(false);

                } else {
                    // list is in expanded state, will have state - collapsed
                    mBinding.lvTrailers.setVisibility(View.GONE);
                    mBinding.ivTrailerCollapseButton.setImageResource(R.drawable.ic_keyboard_arrow_down);
                    mViewModel.setTrailerListCollapsed(true);
                }
            }
        });
    }
*/

/*
    private void setupReviewList(final List<ReviewMinimal> reviewList) {

        final ListView reviews = mBinding.lvReviews;
        ReviewListAdapter adapter =
                new ReviewListAdapter(this, reviewList, this);
        reviews.setAdapter(adapter);

        if (mViewModel.isReviewListCollapsed()) {
            mBinding.lvReviews.setVisibility(View.GONE);
        } else {
            mBinding.lvReviews.setVisibility(View.VISIBLE);
        }

        mBinding.cardview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reviewList.size() == 0) {
                    showToastMessage(R.string.empty_review_list);
                    return;
                }

                boolean listState = mViewModel.isReviewListCollapsed();
                // as result of the imageView click collapse button should
                // change state of button itself
                // and ListView visibility
                if (listState) {
                    // list is in collapsed state, will have state - expanded
                    mBinding.lvReviews.setVisibility(View.VISIBLE);
                    mBinding.ivReviewCollapseButton.setImageResource(R.drawable.ic_keyboard_arrow_up);
                    mViewModel.setReviewListCollapsed((false));

                } else {
                    // list is in expanded state, will have state - collapsed
                    mBinding.lvReviews.setVisibility(View.GONE);
                    mBinding.ivReviewCollapseButton.setImageResource(R.drawable.ic_keyboard_arrow_down);
                    mViewModel.setReviewListCollapsed(true);
                }
            }
        });
    }
*/

/*
    private void showTrailer(@Nullable TrailerMinimal trailer) {

        if (trailer != null) {
            //initialize a new intent with action
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(YOUTUBE_BASE_URI + trailer.getKey()));
            //check if intent is supported
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                showToastMessage(R.string.error_no_player);
            }
        }
    }
*/

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

    /**
     * Click listener for TextView that contains Http link to Review on web site
     * If user clicks on link then external web browser will open page with Review
     *
     * @param link
     */
    @Override
    public void onLinkClickListener(@Nullable String link) {
        if (link != null && (link.startsWith("http://") || link.startsWith("https://"))) {
            Log.d(TAG, "onLinkClickListener: " + link);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        }
    }
}
