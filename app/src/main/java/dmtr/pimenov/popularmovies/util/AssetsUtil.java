package dmtr.pimenov.popularmovies.util;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.SparseArray;

import dmtr.pimenov.popularmovies.data.model.Language;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssetsUtil {

    private static final String GENRES_FILE = "genres.json";
    private static final String LANGUAGES_FILE = "iso_639-1.json";
    private static final String BAD_MOVIE_FILE = "bad_movies.txt";
    private static final String BACKDROPS_FILE = "movies_with_bad_backdrops.json";

    /**
     * Loads SparseArray of Movie Genres from JSON file into SparseArray
     *
     * @param context
     * @return
     */
    public static SparseArray<String> getGenresFromAssets(@NonNull Context context) {

        InputStream is = null;
        JsonReader reader = null;

        SparseArray<String> result = new SparseArray<>();

        try {
            is = context.getAssets().open(GENRES_FILE);
            reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            Type GENRE_LIST_TYPE = new TypeToken<List<GenreName>>() {
            }.getType();

            List<GenreName> genres = new Gson().fromJson(reader, GENRE_LIST_TYPE);
            for (GenreName g : genres) {
                result.put(g.getId(), g.getName());
            }
            return result;

        } catch (JsonSyntaxException | IOException ex) {
            ex.printStackTrace();
            return result;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private class GenreName {

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Loads Map of Language codes ISO 639-1 from JSON file into unmodifiable Map
     *
     * @param context
     * @return
     */
    public static Map<String, Language> getLanguagesFromAssets(@NonNull Context context) {

        InputStream is = null;
        JsonReader reader = null;

        try {
            is = context.getAssets().open(LANGUAGES_FILE);
            reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            Type LANGUAGE_LIST_TYPE = new TypeToken<Map<String, Language>>() {
            }.getType();

            Map<String, Language> languageMap = new Gson().fromJson(reader, LANGUAGE_LIST_TYPE);
            return Collections.unmodifiableMap(languageMap);

        } catch (JsonSyntaxException | IOException ex) {

            ex.printStackTrace();
            return Collections.EMPTY_MAP;

        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Loads list of Movie that possible violate Google Sexually Explicit Content policy
     * Further these movies will be hidden from the user
     *
     * @param context
     * @return Set of movie Id's
     */
    public static Set<Long> getBadMovieIdsFromAssets(@NonNull Context context) {

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try {

            is = context.getAssets().open(BAD_MOVIE_FILE);
            isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);

            String line;
            ArrayList<String> lines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            Set<Long> result = new HashSet<>(lines.size());
            for (String l : lines) {
                result.add(Long.parseLong(l));
            }
            return Collections.unmodifiableSet(result);

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error reading " + BAD_MOVIE_FILE, ex);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (Exception e) {
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Loads Map of Movie Id with backdrops that possible violate Google Sexually Explicit Content policy
     *
     * @param context
     * @return
     */
    public static Map<Long, Set<String>> getBadBackdropsFromAssets(@NonNull Context context) {

        InputStream is = null;
        JsonReader reader = null;

        try {
            is = context.getAssets().open(BACKDROPS_FILE);
            reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            Type BACKDROP_LIST_TYPE = new TypeToken<List<BadMovie>>() {
            }.getType();

            List<BadMovie> movieList = new Gson().fromJson(reader, BACKDROP_LIST_TYPE);

            //noinspection unchecked
            Map<Long, Set<String>> result = new HashMap(movieList.size());
            for (BadMovie m : movieList) {
                //noinspection unchecked
                result.put(m.getId(), Collections.unmodifiableSet(new HashSet(m.getBackdrops())));
            }
            return Collections.unmodifiableMap(result);

        } catch (JsonSyntaxException | IOException ex) {

            ex.printStackTrace();
            return Collections.EMPTY_MAP;

        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private class BadMovie {
        long id;
        List<String> backdrops;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public List<String> getBackdrops() {
            return backdrops;
        }

        public void setBackdrops(List<String> backdrops) {
            this.backdrops = backdrops;
        }
    }
}
