package com.example.android.popularmovies.ui.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.data.AppRepository;
import com.example.android.popularmovies.ui.MovieDetailViewModel;

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
