package dmtr.pimenov.popularmovies.data.network;

import dmtr.pimenov.popularmovies.data.model.BackdropCollection;
import dmtr.pimenov.popularmovies.data.model.MovieDetail;
import dmtr.pimenov.popularmovies.data.model.MoviesPage;
import dmtr.pimenov.popularmovies.data.model.ReviewCollection;
import dmtr.pimenov.popularmovies.data.model.TrailerCollection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * This interface defines REST request to get a Page with collection of Movies
 */

public interface RawMovieDbApi {

    @GET("3/movie/{sort_kind}")
    Call<MoviesPage> getMoviesPage(@Path("sort_kind") String sortKind,
                                   @Query("api_key") String apiKey,
                                   @Query("page") int page);

    @GET("3/movie/{id}")
    Call<MovieDetail> getMovieDetail(@Path("id") long movieId,
                                     @Query("api_key") String apiKey);

    @GET("3/movie/{id}/videos")
    Call<TrailerCollection> getTrailers(@Path("id") long movieId,
                                        @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<ReviewCollection> getReviews(@Path("id") long movieId,
                                      @Query("api_key") String apiKey);

    @GET("3/movie/{id}/images")
    Call<BackdropCollection> getImages(@Path("id") long movieId,
                                       @Query("api_key") String apiKey);
}
