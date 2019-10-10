package dmtr.pimenov.android.popularmovies.data.network;

import android.os.Build;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import dmtr.pimenov.android.popularmovies.data.model.BackdropCollection;
import dmtr.pimenov.android.popularmovies.data.model.MovieDetail;
import dmtr.pimenov.android.popularmovies.data.model.MoviesPage;
import dmtr.pimenov.android.popularmovies.data.model.ReviewCollection;
import dmtr.pimenov.android.popularmovies.data.model.TrailerCollection;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Wrapper for retrofit client
 * Singleton pattern is used
 */
public class NetworkApi {

    private static final String TAG = NetworkApi.class.getSimpleName();

    // Base URL for work with MovieDB API
    private static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org";
    // Base URL for loading images (posters, backdrops)
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    // Base URL for loading video thumbnail from youtube
    private static final String YOUTUBE_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
    private static final String YOUTUBE_THUMBNAIL_ENDING = "/mqdefault.jpg";


    // retrofit client that implements RawMovieDbApi interface
    private RawMovieDbApi mRawMovieDbApi;

    private static NetworkApi mNetworkApi;

    /**
     * this enum defines the available poster sizes
     */
    public enum PosterSize {
        W92, W154, W185, W342, W500, W780, ORIGINAL;

        private String getSizeStr() {
            return this.name().toLowerCase();
        }
    }

    /**
     * this enum defines the available backdrop sizes
     */
    public enum BackdropSize {
        W300, W780, W1280, ORIGINAL;

        private String getSizeStr() {
            return this.name().toLowerCase();
        }
    }

    /**
     * build OkHttpClient with custom SSL socket factory
     * for pre lollipop devices
     * @return
     */
    private OkHttpClient buildOkHttpClient() {
        OkHttpClient client = null;
        try {
            TLSSocketFactory tlsSocketFactory = new TLSSocketFactory();
            client = new OkHttpClient.Builder().
                    sslSocketFactory(tlsSocketFactory, tlsSocketFactory.getTrustManager()).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    /**
     * init retrofit
     * private constructor to avoid client applications to use constructor
     */
    private NetworkApi() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MOVIEDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.client(buildOkHttpClient());
        }
        Retrofit retrofit = builder.build();
        mRawMovieDbApi = retrofit.create(RawMovieDbApi.class);
    }

    /**
     * Creates and returns retrofit client that implements
     * RawMovieDbApi interface
     * @return NetworkApi
     */
    public static synchronized NetworkApi getMovieDbApi() {
        if (mNetworkApi == null) {
            Log.d(TAG, "Creating new MovieDB API");
            mNetworkApi = new NetworkApi();
            Log.d(TAG, "getMovieDbApi: " + "Network NetworkApi has been created");
        }
        return mNetworkApi;
    }

    /**
     * Executes REST request to MovieDB and
     * Returns Page with collection of Movies
     * @param sortMode - sort mode.
     * @param page - page number
     * @return Call<MoviesPage>
     */
    public Call<MoviesPage> getMoviesPage(String sortMode, int page) {
        return mRawMovieDbApi.getMoviesPage(sortMode,
                BuildConfig.MOVEDB_API_KEY,
                page);
    }

    /**
     * Executes REST request to MovieDB and
     * Returns MovieDetail
     * @param movieId
     * @return
     */
    public Call<MovieDetail> getMovieDetail(long movieId) {
        return mRawMovieDbApi.getMovieDetail(movieId, BuildConfig.MOVEDB_API_KEY);
    }

    /**
     * Executes REST request to MovieDB and
     * Returns collection of trailers
     * @param movieId - Movie ID
     * @return Call<TrailerCollection>
     */
    public Call<TrailerCollection> getTrailersCollection(long movieId) {
        return mRawMovieDbApi.getTrailers(movieId,
                BuildConfig.MOVEDB_API_KEY);
    }

    /**
     * Executes REST request to MovieDB and
     * Returns collection of reviews
     * @param movieId - Movie ID
     * @return Call<ReviewCollection>
     */
    public Call<ReviewCollection> getReviewCollection(long movieId) {
        return mRawMovieDbApi.getReviews(movieId,
                BuildConfig.MOVEDB_API_KEY);
    }

    /**
     * Executes REST request to MovieDB and
     * Returns collection of Backdrops
     * @param movieId - Movie ID
     * @return Call<BackdropCollection>
     */
    public Call<BackdropCollection> getBackdropCollection(long movieId) {
        return mRawMovieDbApi.getImages(movieId,
                BuildConfig.MOVEDB_API_KEY);
    }

    /**
     * Returns a String representation of URL for loading poster
     * @param relativePath See Movie.getPosterPath()
     * @param posterSize
     * @return String representation of URL for load image
     */
    public static String getPosterUrl(String relativePath, PosterSize posterSize) {
        return IMAGE_BASE_URL + posterSize.getSizeStr() + relativePath;
    }

    /**
     * Returns a String representation of URL for loading backdrop
     * @param relativePath See Movie.getBackdropPath()
     * @param backdropSize
     * @return String representation of URL for load image
     */
    public static String getBackdropUrl(String relativePath, BackdropSize backdropSize) {
        return IMAGE_BASE_URL + backdropSize.getSizeStr() + relativePath;
    }

    /**
     * Returns a String representation of URL for loading video thumbnail from youtube
     * @param videoKey
     * @return String representation of URL for load image
     */
    public static String getVideoThimbnailUrl(String videoKey) {
        return YOUTUBE_THUMBNAIL_BASE_URL + videoKey + YOUTUBE_THUMBNAIL_ENDING;
    }
}
