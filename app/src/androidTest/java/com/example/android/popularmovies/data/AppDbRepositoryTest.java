package com.example.android.popularmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.TestUtil;
import com.example.android.popularmovies.data.database.AppDatabase;
import com.example.android.popularmovies.data.model.Backdrop;
import com.example.android.popularmovies.data.model.Genre;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.MovieDetail;
import com.example.android.popularmovies.data.model.Review;
import com.example.android.popularmovies.data.model.Trailer;
import com.example.android.popularmovies.util.IAppExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * For testing Database related methods
 */

@RunWith(AndroidJUnit4.class)
public class AppDbRepositoryTest {

    AppRepository mRepository;
    AppDatabase mDatabase;
    IAppExecutors mExecutors;
    Context mContext;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
        mDatabase = Room.inMemoryDatabaseBuilder(mContext, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        mExecutors = TestUtil.getMockAppExecutors();
        mRepository = AppRepository.getInstance(mContext, mExecutors, mDatabase, null);
    }

    @After
    public void tearDown() throws Exception {
        mDatabase.close();
        // reset singleton
        Field sInstance = mRepository.getClass().getDeclaredField("sInstance");
        sInstance.setAccessible(true);
        sInstance.set(null, null);
    }

    @Test
    public void dbLoadAllMovies() throws InterruptedException {

        Long id1 = 1L;
        double popularity1 = 12;
        double voteAverage1 = 55;
        Long id2 = 2L;
        double popularity2 = 24;
        double voteAverage2 = 15;

        MovieDetail movieDetail1 = createMovie(id1, popularity1, voteAverage1);
        MovieDetail movieDetail2 = createMovie(id2, popularity2, voteAverage2);

        mRepository.dbInsertMovie(movieDetail1, Collections.<Trailer>emptyList(),
                Collections.<Review>emptyList(), Collections.<Genre>emptyList(),
                Collections.<Backdrop>emptyList());

        mRepository.dbInsertMovie(movieDetail2, Collections.<Trailer>emptyList(),
                Collections.<Review>emptyList(), Collections.<Genre>emptyList(),
                Collections.<Backdrop>emptyList());

        // sort mode by popular
        String sortMode = mContext.getResources().getString(R.string.pref_most_popular_value);
        LiveData<List<Movie>> result = mRepository.dbLoadAllMovies(sortMode);

        List<Movie> list = TestUtil.getOrAwaitValue(result);

        assertEquals(2, list.size());

        Movie m = list.get(0);
        assertEquals(id2, m.getId());

        m = list.get(1);
        assertEquals(id1, m.getId());

        // sort mode top rated
        sortMode = mContext.getResources().getString(R.string.pref_top_rated_value);
        result = mRepository.dbLoadAllMovies(sortMode);

        list = TestUtil.getOrAwaitValue(result);

        assertEquals(2, list.size());

        m = list.get(0);
        assertEquals(id1, m.getId());

        m = list.get(1);
        assertEquals(id2, m.getId());
    }

    @Test
    public void dbIsFavoriteMovie() throws InterruptedException {

        Long id = 1L;

        MovieDetail movieDetail = createMovie(id, 12, 55);

        mRepository.dbInsertMovie(movieDetail, Collections.<Trailer>emptyList(),
                Collections.<Review>emptyList(), Collections.<Genre>emptyList(),
                Collections.<Backdrop>emptyList());

        LiveData<Boolean> booleanLiveData = mRepository.dbIsFavoriteMovie(id);
        Boolean res = TestUtil.getOrAwaitValue(booleanLiveData);
        assertTrue(res);

        booleanLiveData = mRepository.dbIsFavoriteMovie(2);
        res = TestUtil.getOrAwaitValue(booleanLiveData);
        assertFalse(res);
    }

    @Test
    public void dbLoadAllTrailers() throws InterruptedException {

        Long id = 1L;

        Trailer t1 = new Trailer("id_1", "key_1", id, "trailer_1");
        Trailer t2 = new Trailer("id_2", "key_2", id, "trailer_2");
        Trailer t3 = new Trailer("id_3", "key_3", id, "trailer_3");

        List<Trailer> trailers = Arrays.asList(t1, t2, t3);

        MovieDetail movieDetail1 = createMovie(id, 1, 1);

        mRepository.dbInsertMovie(movieDetail1, trailers,
                Collections.<Review>emptyList(), Collections.<Genre>emptyList(),
                Collections.<Backdrop>emptyList());

        LiveData<List<Trailer>> result = mRepository.dbLoadAllTrailers(id);
        List<Trailer> list = TestUtil.getOrAwaitValue(result);

        assertEquals(3, list.size());

        List<String> ids = Arrays.asList(t1.getId(), t2.getId(), t3.getId());
        for (Trailer t : list) {
            assertTrue(ids.contains(t.getId()));
        }
    }

    @Test
    public void dbLoadAllReviews() throws InterruptedException {

        Long id = 1L;

        Review r1 = new Review("id_1", id, "author_1", "content_1", "url_1");
        Review r2 = new Review("id_2", id, "author_2", "content_2", "url_2");
        Review r3 = new Review("id_3", id, "author_3", "content_3", "url_3");

        List<Review> reviewList = Arrays.asList(r1, r2, r3);

        MovieDetail movieDetail1 = createMovie(id, 1, 1);

        mRepository.dbInsertMovie(movieDetail1, Collections.<Trailer>emptyList(),
                reviewList, Collections.<Genre>emptyList(),
                Collections.<Backdrop>emptyList());

        LiveData<List<Review>> result = mRepository.dbLoadAllReviews(id);
        List<Review> list = TestUtil.getOrAwaitValue(result);

        assertEquals(reviewList.size(), list.size());

        List<String> ids = Arrays.asList(r1.getId(), r2.getId(), r3.getId());
        for (Review r : list) {
            assertTrue(ids.contains(r.getId()));
        }
    }

    @Test
    public void dbLoadMovieDetailById() throws InterruptedException {

        Long id = 1L;
        double popularity = 12;
        double voteAverage = 12;
        int genreId1 = 1;
        int genreId2 = 2;

        MovieDetail m = createMovie(id, popularity, voteAverage);

        Genre g1 = new Genre(genreId1, id);
        Genre g2 = new Genre(genreId2, id);
        List<Genre> genres = Arrays.asList(g1, g2);

        mRepository.dbInsertMovie(m, Collections.<Trailer>emptyList(),
                Collections.<Review>emptyList(), genres,
                Collections.<Backdrop>emptyList());

        LiveData<MovieDetail> movieDetailLiveData = mRepository.dbLoadMovieDetailById(id);
        MovieDetail movieDetail = TestUtil.getOrAwaitValue(movieDetailLiveData);

        assertNotNull(movieDetail);
        assertEquals(id, movieDetail.getId());

        List<Genre> receivedGenres = movieDetail.getGenres();
        assertNotNull(receivedGenres);
        assertEquals(genres.size(), receivedGenres.size());

        List<Integer> ids = new ArrayList<>();
        for (Genre g : genres) {
            ids.add(g.getGenreId());
        }

        for (Genre g : receivedGenres) {
            assertTrue(ids.contains(g.getGenreId()));
        }
    }

    @Test
    public void dbDeleteMovie() throws InterruptedException {

        Long id = 1L;
        double popularity = 12;
        double voteAverage = 12;

        MovieDetail m = createMovie(id, popularity, voteAverage);
        mRepository.dbInsertMovie(m, Collections.<Trailer>emptyList(),
                Collections.<Review>emptyList(), Collections.<Genre>emptyList(),
                Collections.<Backdrop>emptyList());

        mRepository.dbDeleteMovieById(id);

        LiveData<MovieDetail> movieDetailLiveData = mRepository.dbLoadMovieDetailById(id);
        MovieDetail movieDetail = TestUtil.getOrAwaitValue(movieDetailLiveData);
        assertNull(movieDetail);
    }

    // helper method
    private MovieDetail createMovie(long id, double popularity, double voteAverage) {
        String homePage = "www.homepage.com";
        String originalLanguage = "en";
        String title = "title";
        String titleOriginal = "titleOriginal";
        String overview = "overview";
        String tagLine = "tagline";
        String posterPath = "\\path";
        String releaseDate = "2019-06-05";
        int budget = 100;
        int revenue = 200;
        int runtime = 144;
        int voteCount = 99;

        MovieDetail m = new MovieDetail(id, homePage, originalLanguage, title,
                titleOriginal, overview, tagLine, posterPath, releaseDate, budget, revenue,
                runtime, popularity, voteAverage, voteCount);

        return m;
    }
}