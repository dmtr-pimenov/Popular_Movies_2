package dmtr.example.android.popularmovies;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;
import android.util.SparseArray;

import dmtr.example.android.popularmovies.data.model.Language;
import dmtr.example.android.popularmovies.util.AssetsUtil;

import java.util.Map;

public class MyApplication extends Application {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Map<String, Language> mLanguageMap;
    private SparseArray<String> mGenresArray;

    @Override
    public void onCreate() {
        super.onCreate();
        preloadDataFromAssets();
    }

    private void preloadDataFromAssets() {
        mLanguageMap = AssetsUtil.getLanguagesFromAssets(this);
        mGenresArray = AssetsUtil.getGenresFromAssets(this);
    }


    public Map<String, Language> getLanguageMap() {
        return mLanguageMap;
    }

    public SparseArray<String> getGenresArray() {
        return mGenresArray;
    }
}
