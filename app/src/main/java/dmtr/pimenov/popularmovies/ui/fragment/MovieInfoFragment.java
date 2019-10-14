package dmtr.pimenov.popularmovies.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import dmtr.pimenov.popularmovies.R;
import dmtr.pimenov.popularmovies.data.model.MovieDetail;
import dmtr.pimenov.popularmovies.data.model.Resource;
import dmtr.pimenov.popularmovies.databinding.FragmentMovieInfoBinding;
import dmtr.pimenov.popularmovies.ui.MovieDetailViewModel;

public class MovieInfoFragment extends Fragment {

    private MovieDetailViewModel mViewModel;
    private FragmentMovieInfoBinding mBinding;

    public MovieInfoFragment() {
        // Required empty public constructor
    }

    public static MovieInfoFragment newInstance() {
        MovieInfoFragment fragment = new MovieInfoFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_info, container, false);

        mViewModel = ViewModelProviders.of(getActivity()).get(MovieDetailViewModel.class);
        mViewModel.getMovieDetail().observe(this, new Observer<Resource<MovieDetail>>() {
            @Override
            public void onChanged(@Nullable Resource<MovieDetail> movieDetailResource) {
                if (movieDetailResource.status == Resource.Status.SUCCESS) {
                    mViewModel.getMovieDetail().removeObserver(this);
                    mBinding.setMovieDetail(movieDetailResource.data);
                }
            }
        });

        return mBinding.getRoot();
    }
}
