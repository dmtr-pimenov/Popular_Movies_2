package dmtr.pimenov.popularmovies.ui.adapter;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.widget.RatingBar;
import android.widget.TextView;

import dmtr.pimenov.popularmovies.MyApplication;
import dmtr.pimenov.popularmovies.R;
import dmtr.pimenov.popularmovies.data.model.Genre;
import dmtr.pimenov.popularmovies.data.model.Language;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BindingAdapters {

    @BindingAdapter("releaseDate")
    public static void setReleaseDate(TextView textView, String releaseDate) {
        String result = "";
        if (releaseDate != null) {
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
        }
        textView.setText(result);
    }

    @BindingAdapter("releaseDateFull")
    public static void setReleaseDateFull(TextView textView, String releaseDate) {
        String result = "";
        if (releaseDate != null) {
            Context context = textView.getContext().getApplicationContext();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat();
                int jsonDateFormat = R.string.json_date_format;
                sdf.applyPattern(context.getString(jsonDateFormat));
                Date date = sdf.parse(releaseDate);

                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
                result = dateFormat.format(date);

            } catch (ParseException ex) {
                result = "date parse error";
            }
        }
        textView.setText(result);
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

    @BindingAdapter("ratingString")
    public static void setRating(TextView textView, @Nullable Double voteAverage) {
        if (voteAverage != null) {
            NumberFormat numberInstance = DecimalFormat.getNumberInstance();
            numberInstance.setMaximumFractionDigits(1);
            String ratingString = numberInstance.format(voteAverage) + "/10";
            textView.setText(ratingString);
        }
    }

    @BindingAdapter("stars")
    public static void setStars(RatingBar ratingBar, @Nullable Double voteAverage) {
        if (voteAverage != null) {
            float numStars = ratingBar.getNumStars();
            float rating = voteAverage.floatValue() * numStars / 10f;
            ratingBar.setRating(rating);
        }
    }

    @BindingAdapter("language")
    public static void setLanguage(TextView textView, @Nullable String langCode) {
        if (langCode != null) {
            MyApplication myApplication = (MyApplication) textView.getContext().getApplicationContext();
            Language language = myApplication.getLanguageMap().get(langCode);
            if (language != null) {
                textView.setText(language.getName());
            }
        } else {
            textView.setText("N/A");
        }
    }

    @BindingAdapter("runtime")
    public static void setRuntime(TextView textView, @Nullable Integer runtime) {
        String result = "N/A";
        if (runtime != null) {
            String formatStr = textView.getContext().getString(R.string.runtime_format);
            result = String.format(formatStr, runtime / 60, runtime % 60);
        }
        textView.setText(result);
    }

    @BindingAdapter("money")
    public static void setMoney(TextView textView, @Nullable Long money) {
        String result = "N/A";
        if (money != null && money != 0) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
            result = formatter.format(money);
        }
        textView.setText(result);
    }
}
