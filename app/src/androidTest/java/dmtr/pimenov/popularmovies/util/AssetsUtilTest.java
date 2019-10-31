package dmtr.pimenov.popularmovies.util;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.SparseArray;

import dmtr.pimenov.popularmovies.data.model.Language;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AssetsUtilTest {

    @Test
    public void getGenresFromAssets() {
        Context appContext = ApplicationProvider.getApplicationContext();

        SparseArray<String> genres = AssetsUtil.getGenresFromAssets(appContext);
        assertNotNull(genres.get(878));
        assertNotNull(genres.get(14));
        assertEquals("Science Fiction", genres.get(878));
        assertEquals("Fantasy", genres.get(14));
        assertNull(genres.get(-1));
    }

    @Test
    public void getLanguagesFromAssets() {
        Context appContext = ApplicationProvider.getApplicationContext();

        Map<String, Language> languages = AssetsUtil.getLanguagesFromAssets(appContext);
        assertNotNull(languages.get("en"));
        assertNotNull(languages.get("ru"));
        assertEquals("English", languages.get("en").getName());
        assertEquals("Russian", languages.get("ru").getName());
        assertNull(languages.get("zzz"));
    }

    @Test
    public void getBadMovieIdsFromAssets() {
        Context appContext = ApplicationProvider.getApplicationContext();
        Set<Long> badMovieIds = AssetsUtil.getBadMovieIdsFromAssets(appContext);
        assertFalse(badMovieIds.isEmpty());
        // bad assert
        assertEquals(145, badMovieIds.size());
        assertTrue(badMovieIds.contains(9179L));
        assertTrue(badMovieIds.contains(5725L));
        assertTrue(badMovieIds.contains(451156L));
    }

    @Test
    public void getBadBackdropsFromAssets() {
        Context appContext = ApplicationProvider.getApplicationContext();
        Map<Long, Set<String>> badBackdrops = AssetsUtil.getBadBackdropsFromAssets(appContext);
        assertFalse(badBackdrops.isEmpty());
        // bad assert
        assertEquals(226, badBackdrops.size());

        Set<String> s;

        s = badBackdrops.get(10261L);
        assertNotNull(s);
        assertTrue(s.contains("/jcXM32nUMoV6YAonMT3bAptuHoM.jpg"));

        s = badBackdrops.get(9923L);
        assertNotNull(s);
        assertTrue(s.contains("/dNYUrROJMb1NxSNUUrTThYQ4OL9.jpg"));

        s = badBackdrops.get(137182L);
        assertNotNull(s);
        assertTrue(s.contains("/2sSLbMcOvZvxzJcbHI46QFibAuc.jpg"));

        s = badBackdrops.get(1378L);
        assertNotNull(s);
        assertEquals(3, s.size());
        assertTrue(s.contains("/nJ4WTjDiLNeY1zaI4i0esIUEYRy.jpg"));
        assertTrue(s.contains("/NIoD1HZGAPQnJnEwIDmlEWYY1w.jpg"));
        assertTrue(s.contains("/uGgiiUKIaUlO0ArJPwpW9aIeaiy.jpg"));

    }
}