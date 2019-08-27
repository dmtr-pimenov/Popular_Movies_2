package com.example.android.popularmovies.data.network;

import com.example.android.popularmovies.data.model.MoviesPage;
import com.example.android.popularmovies.data.model.ReviewCollection;
import com.example.android.popularmovies.data.model.TrailerCollection;

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

    @GET("3/movie/{id}/videos")
    Call<TrailerCollection> getTrailers(@Path("id") String movieId,
                                        @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<ReviewCollection> getReviews(@Path("id") String movieId,
                                      @Query("api_key") String apiKey);
}
