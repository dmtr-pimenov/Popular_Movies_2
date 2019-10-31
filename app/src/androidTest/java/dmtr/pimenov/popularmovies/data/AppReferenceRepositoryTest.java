package dmtr.pimenov.popularmovies.data;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import dmtr.pimenov.popularmovies.data.model.Language;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * For testing References related methods
 * working with language table 639-1 and Genres
 */

@RunWith(AndroidJUnit4.class)
public class AppReferenceRepositoryTest {

    AppRepository mRepository;
    Context mContext;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mRepository = AppRepository.getInstance(mContext, null, null, null);
    }

    @After
    public void tearDown() throws Exception {
        // reset singleton
        Field sInstance = mRepository.getClass().getDeclaredField("sInstance");
        sInstance.setAccessible(true);
        sInstance.set(null, null);
    }

    @Test
    public void getGenreById() {
        assertEquals("Science Fiction", mRepository.getGenreById(878));
        assertEquals("Fantasy", mRepository.getGenreById(14));
        assertNull(mRepository.getGenreById(-1));
    }

    @Test
    public void getLanguageByCode() {
        Language en = mRepository.getLanguageByCode("en");
        assertNotNull(en);
        assertEquals("English", en.getName());
        Language ru = mRepository.getLanguageByCode("ru");
        assertNotNull(ru);
        assertEquals("Russian", ru.getName());
        assertNull(mRepository.getLanguageByCode("zzz"));
    }
}