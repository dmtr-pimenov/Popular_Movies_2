package dmtr.pimenov.popularmovies.ui.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import dmtr.pimenov.popularmovies.data.AppRepository;
import dmtr.pimenov.popularmovies.ui.MovieDetailViewModel;

public class MovieDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final AppRepository mRepository;
    private final long mMovieId;

    public MovieDetailViewModelFactory(long movieId,
                                       @NonNull AppRepository repository) {
        mRepository = repository;
        mMovieId = movieId;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieDetailViewModel(mMovieId, mRepository);
    }
}
