package com.example.android.popularmovies.ui;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.model.MovieDetail;
import com.example.android.popularmovies.data.model.Resource;
import com.example.android.popularmovies.databinding.FragmentMovieInfoBinding;

public class MovieInfoFragment extends Fragment {

    private MovieDetailViewModel mViewModel;
    private FragmentMovieInfoBinding mBinding;

    public MovieInfoFragment() {
        // Required empty public constructor
    }

    public static MovieInfoFragment newInstance() {
        MovieInfoFragment fragment = new MovieInfoFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(MovieDetailViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_info, container, false);
        View v = mBinding.getRoot();

        mViewModel.getMovieDetail().observe(this, new Observer<Resource<MovieDetail>>() {
            @Override
            public void onChanged(@Nullable Resource<MovieDetail> movieDetailResource) {
                if (movieDetailResource.status == Resource.Status.SUCCESS) {
                    mViewModel.getMovieDetail().removeObserver(this);
                    mBinding.setMovieDetail(movieDetailResource.data);
                }
            }
        });

        return v;
    }
}
