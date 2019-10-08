package com.example.android.popularmovies.util;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.SparseArray;

import com.example.android.popularmovies.data.model.Language;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AssetsUtil {

    private static final String GENRES_FILE = "genres.json";
    private static final String LANGUAGES_FILE = "iso_639-1.json";

    /**
     * Loads list of Movie Genres from JSON file into SparseArray
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
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
            }

            try {
                if (reader != null) {
                    reader.close();
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
     * Loads list of Language codes ISO 639-1 from JSON file into unmodifiable Map
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
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
            }

            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }
        }
    }
}
