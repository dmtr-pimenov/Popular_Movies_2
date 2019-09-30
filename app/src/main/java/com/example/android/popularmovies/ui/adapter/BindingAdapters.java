package com.example.android.popularmovies.ui.adapter;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.os.Build;
import android.widget.TextView;

import com.example.android.popularmovies.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BindingAdapters {

    @BindingAdapter("app:releaseDate")
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
}
