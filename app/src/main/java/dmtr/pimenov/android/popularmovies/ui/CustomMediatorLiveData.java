package dmtr.pimenov.android.popularmovies.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

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
    public <S> void addSource(@NonNull LiveData<S> source, @NonNull Observer<S> onChanged) {
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
