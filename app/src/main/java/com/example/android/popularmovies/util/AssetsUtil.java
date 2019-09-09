package com.example.android.popularmovies.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.example.android.popularmovies.data.model.Genre;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class AssetsUtil {

    private static final String GENRES_FILE = "genres.json";

    /**
     * Loads list of Movie Genres from JSON file into SparseArray
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
            Type GENRE_LIST_TYPE = new TypeToken<List<Genre>>() {
            }.getType();

            List<Genre> genres = new Gson().fromJson(reader, GENRE_LIST_TYPE);
            for (Genre g : genres) {
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
            } catch (Exception e) {}

            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {}
        }
    }

}
