package com.example.android.popularmovies;

import android.app.Application;
import android.net.Uri;
import android.support.v7.app.AppCompatDelegate;

import java.net.URI;

public class MyApplication extends Application {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
