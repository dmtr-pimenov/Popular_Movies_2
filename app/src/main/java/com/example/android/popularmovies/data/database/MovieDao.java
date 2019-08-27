package com.example.android.popularmovies.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.ReviewMinimal;
import com.example.android.popularmovies.data.model.TrailerMinimal;

import java.util.List;

@Dao
public abstract class MovieDao {

    // Movie related methods
    @Query("select a.* from movie as a order by a.id")
    public abstract LiveData<List<Movie>> loadAllMovies();

    @Query("select a.* from movie as a order by a.popularity desc")
    public abstract LiveData<List<Movie>> loadAllMoviesByPopularity();

    @Query("select a.* from movie as a order by a.vote_average desc")
    public abstract LiveData<List<Movie>> loadAllMoviesByRating();

    @Query("select a.* from movie as a where a.id = :id")
    public abstract LiveData<Movie> loadMovieById(long id);

    @Query("select a.id from movie as a where a.id = :id")
    public abstract LiveData<Long> isFavoriteMovie(long id);

    @Insert
    public abstract void insertMovie(Movie entity);

    @Delete
    public abstract void deleteMovie(Movie entity);

    @Query("delete from movie where id = :id")
    public abstract void deleteMovieById(long id);

    // Trailer related methods
    @Query("select t.* from trailer_minimal as t where t.movie_id = :movieId order by t.id")
    public abstract LiveData<List<TrailerMinimal>> loadAllTrailers(Long movieId);

    @Insert
    public abstract void insertTrailers(List<TrailerMinimal> entities);

    // Review related methods
    @Query("select t.* from review_minimal as t where t.movie_id = :movieId order by t.id")
    public abstract LiveData<List<ReviewMinimal>> loadAllReviews(Long movieId);

    @Insert
    public abstract void insertReviews(List<ReviewMinimal> entities);

    //
    @Transaction
    public void insertMovieAndChildren(Movie movie,
                                       List<TrailerMinimal> trailers,
                                       List<ReviewMinimal> reviews) {
        insertMovie(movie);
        insertTrailers(trailers);
        insertReviews(reviews);
    }
}
