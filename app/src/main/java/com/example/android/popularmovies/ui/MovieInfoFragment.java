package com.example.android.popularmovies.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
