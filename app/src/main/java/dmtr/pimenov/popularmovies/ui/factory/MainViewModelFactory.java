package dmtr.pimenov.popularmovies.ui.factory;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import dmtr.pimenov.popularmovies.data.AppRepository;
import dmtr.pimenov.popularmovies.ui.MainViewModel;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final AppRepository mRepository;

    public MainViewModelFactory(@NonNull Application application, @NonNull AppRepository repository) {
        mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainViewModel(mRepository);
    }
}
