package com.example.android.popularmovies.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.example.android.popularmovies.data.model.Backdrop;
import com.example.android.popularmovies.data.model.Genre;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.MovieDetail;
import com.example.android.popularmovies.data.model.Review;
import com.example.android.popularmovies.data.model.Trailer;

import java.util.List;

@Dao
public abstract class MovieDao {

    @Query("select a.id, a.title, a.poster_path from movie_detail as a order by a.popularity desc")
    public abstract LiveData<List<Movie>> loadAllMoviesByPopularity();

    @Query("select a.id, a.title, a.poster_path from movie_detail as a order by a.vote_average desc")
    public abstract LiveData<List<Movie>> loadAllMoviesByRating();

    @Transaction
    @Query("select a.* from movie_detail as a where a.id = :id")
    public abstract LiveData<MovieDetail> loadMovieDetailById(long id);

    @Query("select a.id from movie_detail as a where a.id = :id")
    public abstract LiveData<Long> isFavoriteMovie(long id);

    @Insert
    public abstract void insertMovie(MovieDetail entity);

    @Delete
    public abstract void deleteMovie(MovieDetail entity);

    @Transaction
    @Query("delete from movie_detail where id = :id")
    public abstract int deleteMovieById(long id);

    // Trailer related methods
    @Query("select t.* from trailer as t where t.movie_id = :movieId order by t.id")
    public abstract LiveData<List<Trailer>> loadAllTrailers(long movieId);

    @Insert
    public abstract void insertTrailers(List<Trailer> entities);

    // Review related methods
    @Query("select t.* from review as t where t.movie_id = :movieId order by t.id")
    public abstract LiveData<List<Review>> loadAllReviews(long movieId);

    @Insert
    public abstract void insertReviews(List<Review> entities);

    // Genre related methods
    @Query("select g.* from genre as g where g.movie_id = :movieId")
    public abstract LiveData<List<Genre>> loadAllGenres(long movieId);

    @Insert
    public abstract void insertGenres(List<Genre> entities);

    // Backdrop related methods
    @Query("select b.* from backdrop as b where b.movie_id = :movieId")
    public abstract LiveData<List<Backdrop>> loadAllBackdrops(long movieId);

    @Insert
    public abstract void insertBackdrops(List<Backdrop> entities);

    @Transaction
    public void insertMovie(MovieDetail movie,
                            List<Trailer> trailers,
                            List<Review> reviews,
                            List<Genre> genres,
                            List<Backdrop> backdrops
    ) {
        insertMovie(movie);
        insertTrailers(trailers);
        insertReviews(reviews);
        insertGenres(genres);
        insertBackdrops(backdrops);
    }
}
