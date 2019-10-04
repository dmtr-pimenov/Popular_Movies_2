package com.example.android.popularmovies.ui.adapter;

import android.app.Application;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.widget.TextView;

import com.example.android.popularmovies.MyApplication;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.AppRepository;
import com.example.android.popularmovies.data.model.Genre;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BindingAdapters {

    @BindingAdapter("releaseDate")
    public static void setReleaseDate(TextView textView, String releaseDate) {
        if (releaseDate != null) {
            String result;
            Context context = textView.getContext().getApplicationContext();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat();
                int jsonDateFormat = R.string.json_date_format;
                sdf.applyPattern(context.getString(jsonDateFormat));
                Date date = sdf.parse(releaseDate);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int style;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    style = Calendar.LONG_STANDALONE;
                } else {
                    style = Calendar.LONG;
                }
                String monthName = calendar.getDisplayName(Calendar.MONTH, style, Locale.getDefault());
                result = monthName + ", " + calendar.get(Calendar.YEAR);
            } catch (ParseException ex) {
                result = "date parse error";
            }
            textView.setText(result);
        }
    }

    @BindingAdapter("genres")
    public static void setGenres(TextView textView, @Nullable List<Genre> genres) {
        if (genres == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        MyApplication myApplication = (MyApplication) textView.getContext().getApplicationContext();
        SparseArray<String> genresArray = myApplication.getGenresArray();
        for (Genre g : genres) {
            String gName = genresArray.get(g.getGenreId());
            if (gName != null) {
                sb.append(" " + gName + ",");
            }
        }
        String res = "";
        int len = sb.length();
        // remove last comma
        if (sb.length() > 0) {
            res = sb.substring(0, len - 1).trim();
        }
        textView.setText(res);
    }
}
