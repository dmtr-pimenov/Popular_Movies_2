package dmtr.example.android.popularmovies.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.SparseArray;

import dmtr.example.android.popularmovies.data.model.Language;
import dmtr.example.android.popularmovies.util.AssetsUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AssetsUtilTest {

    @Test
    public void getGenresFromAssets() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        SparseArray<String> genres = AssetsUtil.getGenresFromAssets(appContext);
        assertNotNull(genres.get(878));
        assertNotNull(genres.get(14));
        assertEquals("Science Fiction", genres.get(878));
        assertEquals("Fantasy", genres.get(14));
        assertNull(genres.get(-1));
    }

    @Test
    public void getLanguagesFromAssets() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        Map<String, Language> languages = AssetsUtil.getLanguagesFromAssets(appContext);
        assertNotNull(languages.get("en"));
        assertNotNull(languages.get("ru"));
        assertEquals("English", languages.get("en").getName());
        assertEquals("Russian", languages.get("ru").getName());
        assertNull(languages.get("zzz"));
    }
}