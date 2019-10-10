package dmtr.pimenov.popularmovies.util;

import android.app.Application;
import android.content.Context;

import dmtr.pimenov.popularmovies.data.AppRepository;
import dmtr.pimenov.popularmovies.data.database.AppDatabase;
import dmtr.pimenov.popularmovies.data.network.NetworkApi;
import dmtr.pimenov.popularmovies.ui.factory.MainViewModelFactory;
import dmtr.pimenov.popularmovies.ui.factory.MovieDetailViewModelFactory;

public class InjectorUtil {

    private static final String TAG = InjectorUtil.class.getSimpleName();

    public static AppRepository provideRepository(Context context) {
        // Repository should be created in Application Context, not in Activity
        Context appContext = context.getApplicationContext();

        IAppExecutors appExecutors = AppExecutors.getInstance();
        AppDatabase appDatabase = AppDatabase.getInstance(appContext);
        NetworkApi networkApi = NetworkApi.getMovieDbApi();

        return AppRepository.getInstance(appContext, appExecutors, appDatabase, networkApi);
    }

    public static MainViewModelFactory provideMainViewModelFactory(Application application) {
        AppRepository repository = provideRepository(application);
        return new MainViewModelFactory(application, repository);
    }

    public static MovieDetailViewModelFactory provideMovieDetailViewModelFactory(
            Context context, long movieId) {
        AppRepository repository = provideRepository(context);
        return new MovieDetailViewModelFactory(movieId, repository);
    }
}
