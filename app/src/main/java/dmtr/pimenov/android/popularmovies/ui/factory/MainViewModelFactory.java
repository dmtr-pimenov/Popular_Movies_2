package dmtr.pimenov.android.popularmovies.ui.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import dmtr.pimenov.android.popularmovies.data.AppRepository;
import dmtr.pimenov.android.popularmovies.ui.MainViewModel;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application mApplication;
    @NonNull
    private final AppRepository mRepository;

    public MainViewModelFactory(@NonNull Application application, @NonNull AppRepository repository) {
        mApplication = application;
        mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MainViewModel(mApplication, mRepository);
    }
}
