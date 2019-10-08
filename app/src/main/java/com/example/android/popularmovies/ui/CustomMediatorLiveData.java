package com.example.android.popularmovies.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This simple wrapper for MediatorLiveData
 * that keeps all active sources and provides method
 * to remove all sources
 * @param <T>
 */

public class CustomMediatorLiveData<T> extends MediatorLiveData<T> {

    private List<LiveData> mSources = new ArrayList<>();

    @Override
    public <S> void addSource(@NonNull LiveData<S> source, @NonNull Observer<? super S> onChanged) {
        mSources.add(source);
        super.addSource(source, onChanged);
    }

    @Override
    public <S> void removeSource(@NonNull LiveData<S> toRemote) {
        mSources.remove(toRemote);
        super.removeSource(toRemote);
    }

    public void removeAllSources() {
        for (LiveData ld : mSources) {
            removeSource(ld);
        }
        mSources.clear();
    }
}
