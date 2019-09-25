package com.example.android.popularmovies.util;

import android.support.annotation.NonNull;

import com.example.android.popularmovies.data.model.Movie;
import com.example.android.popularmovies.data.model.Review;
import com.example.android.popularmovies.data.model.ReviewMinimal;
import com.example.android.popularmovies.data.model.Trailer;
import com.example.android.popularmovies.data.model.TrailerMinimal;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    public static List<TrailerMinimal> convertTrailer2TrailerMinimal(long movieId,
                                                                     @NonNull List<Trailer> trailers) {
        List<TrailerMinimal> result = new ArrayList<>(trailers.size());
        for (Trailer t : trailers) {
            TrailerMinimal tm = new TrailerMinimal(t.getId(), movieId,
                    t.getKey(), t.getName());
            result.add(tm);
        }
        return result;
    }

    public static List<ReviewMinimal> convertReview2ReviewMinimal(long movieId,
                                                                  @NonNull List<Review> reviews) {
        List<ReviewMinimal> result = new ArrayList<>(reviews.size());
        for (Review r : reviews) {
            ReviewMinimal rm = new ReviewMinimal(r.getId(), movieId,
                    r.getAuthor(), r.getContent(), r.getUrl());
            result.add(rm);
        }
        return result;
    }
}
