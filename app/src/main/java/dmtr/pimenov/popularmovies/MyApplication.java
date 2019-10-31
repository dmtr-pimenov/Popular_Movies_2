package dmtr.pimenov.popularmovies;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.SparseArray;

import dmtr.pimenov.popularmovies.data.model.Language;
import dmtr.pimenov.popularmovies.util.AssetsUtil;

import java.util.Map;
import java.util.Set;

public class MyApplication extends Application {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Map<String, Language> mLanguageMap;
    private SparseArray<String> mGenresArray;
    private Map<Long, Set<String>> badBackdrops;
    private Set<Long> badMovieIds;

    @Override
    public void onCreate() {
        super.onCreate();
        preloadDataFromAssets();
    }

    private void preloadDataFromAssets() {
        mLanguageMap = AssetsUtil.getLanguagesFromAssets(this);
        mGenresArray = AssetsUtil.getGenresFromAssets(this);
        badBackdrops = AssetsUtil.getBadBackdropsFromAssets(this);
        badMovieIds = AssetsUtil.getBadMovieIdsFromAssets(this);

    }


    public Map<String, Language> getLanguageMap() {
        return mLanguageMap;
    }

    public SparseArray<String> getGenresArray() {
        return mGenresArray;
    }

    public Map<Long, Set<String>> getBadBackdrops() {
        return badBackdrops;
    }

    public Set<Long> getBadMovieIds() {
        return badMovieIds;
    }
}
