package com.example.android.popularmovies.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.ui.MovieInfoFragment;
import com.example.android.popularmovies.ui.ReviewsFragment;
import com.example.android.popularmovies.ui.TrailersFragment;

public class MovieFragmentAdapter extends FragmentPagerAdapter {

    private final String[] tabLabels;

    public MovieFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        tabLabels = context.getResources().getStringArray(R.array.array_movie_tab_label);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return MovieInfoFragment.newInstance();
            case 1:
                return TrailersFragment.newInstance();
            case 2:
                return ReviewsFragment.newInstance();
            default:
                throw new RuntimeException("Unexpected fragment index");
        }
    }

    @Override
    public int getCount() {
        return tabLabels.length;
    }
}
