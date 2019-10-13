package dmtr.pimenov.popularmovies.ui.adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import dmtr.pimenov.popularmovies.R;
import dmtr.pimenov.popularmovies.ui.fragment.MovieInfoFragment;
import dmtr.pimenov.popularmovies.ui.fragment.ReviewsFragment;
import dmtr.pimenov.popularmovies.ui.fragment.TrailersFragment;

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

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabLabels[position];
    }

    @Override
    public int getCount() {
        return tabLabels.length;
    }
}
