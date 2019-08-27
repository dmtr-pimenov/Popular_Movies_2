package com.example.android.popularmovies.ui.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.data.AppRepository;
import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.ui.MovieDetailViewModel;

public class MovieDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final AppRepository mRepository;
    private final Movie mMovie;

    public MovieDetailViewModelFactory(@NonNull Movie movie,
                                       @NonNull AppRepository repository) {
        mRepository = repository;
        mMovie = movie;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieDetailViewModel(mMovie, mRepository);
    }
}
