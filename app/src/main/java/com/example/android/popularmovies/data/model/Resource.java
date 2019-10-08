package com.example.android.popularmovies.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * This class encapsulates data (the result of a network request)
 * and status of the network request.
 * This is useful for transferring network request status to an observer.
 * Also we will use this class to wrap data from a database.
 * Since the top level isn't aware about nature of the data, we
 * must provide the result in a unified way
 *
 * Borrowed from
 * https://developer.android.com/jetpack/docs/guide#addendum
 */

public class Resource<T> {

    @NonNull public final Status status;
    @Nullable public final T data;
    @Nullable public final String message;

    private Resource(@NonNull Status status, @Nullable T data,
                     @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    public enum Status { SUCCESS, ERROR }
}