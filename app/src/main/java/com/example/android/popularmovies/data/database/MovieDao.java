package com.example.android.popularmovies.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.example.android.popularmovies.data.model.Genre;
import com.example.android.popularmovies.data.model.MovieDetail;
import com.example.android.popularmovies.data.model.Review;
import com.example.android.popularmovies.data.model.Trailer;

import java.util.List;

@Dao
public abstract class MovieDao {

    // Movie related methods
    @Query("select a.* from movie_detail as a order by a.id")
    public abstract LiveData<List<MovieDetail>> loadAllMovies();

    @Query("select a.* from movie_detail as a order by a.popularity desc")
    public abstract LiveData<List<MovieDetail>> loadAllMoviesByPopularity();

    @Query("select a.* from movie_detail as a order by a.vote_average desc")
    public abstract LiveData<List<MovieDetail>> loadAllMoviesByRating();

    @Query("select a.* from movie_detail as a where a.id = :id")
    public abstract LiveData<MovieDetail> loadMovieById(long id);

    @Query("select a.id from movie_detail as a where a.id = :id")
    public abstract LiveData<Long> isFavoriteMovie(long id);

    @Insert
    public abstract void insertMovie(MovieDetail entity);

    @Delete
    public abstract void deleteMovie(MovieDetail entity);

    @Query("delete from movie_detail where id = :id")
    public abstract void deleteMovieById(long id);

    // Trailer related methods
    @Query("select t.* from trailer as t where t.movie_id = :movieId order by t.id")
    public abstract LiveData<List<Trailer>> loadAllTrailers(Long movieId);

    @Insert
    public abstract void insertTrailers(List<Trailer> entities);

    // Review related methods
    @Query("select t.* from review as t where t.movie_id = :movieId order by t.id")
    public abstract LiveData<List<Review>> loadAllReviews(Long movieId);

    @Insert
    public abstract void insertReviews(List<Review> entities);

    // Genre related methods
    @Query("select g.* from genre as g where g.movie_id = :movieId")
    public abstract LiveData<List<Genre>> loadAllGenres(Long movieId);

    @Insert
    public abstract void insertGenres(List<Genre> entities);

    @Transaction
    public void insertMovieAndChildren(MovieDetail movie,
                                       List<Trailer> trailers,
                                       List<Review> reviews,
                                       List<Genre> genres) {
        insertMovie(movie);
        insertTrailers(trailers);
        insertReviews(reviews);
        insertGenres(genres);
    }
}
