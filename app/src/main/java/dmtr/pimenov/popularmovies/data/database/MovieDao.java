package dmtr.pimenov.popularmovies.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.annotation.NonNull;

import dmtr.pimenov.popularmovies.data.model.Backdrop;
import dmtr.pimenov.popularmovies.data.model.Genre;
import dmtr.pimenov.popularmovies.data.model.IIdSetter;
import dmtr.pimenov.popularmovies.data.model.Movie;
import dmtr.pimenov.popularmovies.data.model.MovieDetail;
import dmtr.pimenov.popularmovies.data.model.Review;
import dmtr.pimenov.popularmovies.data.model.Trailer;

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
    @Query("select g.* from genre as g where g.movie_id = :movieId order by g.genre_id")
    public abstract LiveData<List<Genre>> loadAllGenres(long movieId);

    @Insert
    public abstract void insertGenres(List<Genre> entities);

    // Backdrop related methods
    @Query("select b.* from backdrop as b where b.movie_id = :movieId")
    public abstract LiveData<List<Backdrop>> loadAllBackdrops(long movieId);

    @Insert
    public abstract void insertBackdrops(List<Backdrop> entities);

    @Transaction
    public void insertMovie(@NonNull MovieDetail movie,
                            @NonNull List<Trailer> trailers,
                            @NonNull List<Review> reviews,
                            @NonNull List<Genre> genres,
                            @NonNull List<Backdrop> backdrops
    ) {
        insertMovie(movie);
        setMovieId(movie.getId(), trailers, reviews, genres, backdrops);
        insertTrailers(trailers);
        insertReviews(reviews);
        insertGenres(genres);
        insertBackdrops(backdrops);
    }

    private void setMovieId(Long movieId, List<? extends IIdSetter>... collections) {
        for (List<? extends IIdSetter> c : collections) {
            for (IIdSetter entity : c) {
                entity.setMovieId(movieId);
            }
        }
    }
}
