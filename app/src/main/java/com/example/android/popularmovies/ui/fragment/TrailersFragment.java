package com.example.android.popularmovies.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.model.Resource;
import com.example.android.popularmovies.data.model.Trailer;
import com.example.android.popularmovies.databinding.FragmentTrailersBinding;
import com.example.android.popularmovies.ui.MovieDetailViewModel;
import com.example.android.popularmovies.ui.adapter.TrailerListAdapter;

import java.util.List;

public class TrailersFragment extends Fragment {

    private MovieDetailViewModel mViewModel;
    private FragmentTrailersBinding mBinding;

    public TrailersFragment() {
        // Required empty public constructor
    }

    public static TrailersFragment newInstance() {
        TrailersFragment fragment = new TrailersFragment();
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
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trailers, container, false);
        View v = mBinding.getRoot();

        mViewModel.getTrailerCollection().observe(this, new Observer<Resource<List<Trailer>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Trailer>> listResource) {
                if (listResource.status == Resource.Status.SUCCESS) {
                    List<Trailer> data = listResource.data;
                    if (data.size() == 0) {
                        showNaMessage();
                    } else {
                        setupRecyclerView(data);
                    }

                } else {
                    showError();
                }
            }
        });

        return v;
    }

    private void setupRecyclerView(List<Trailer> data) {

        mBinding.textTrailerError.setVisibility(View.GONE);
        mBinding.textTrailerNa.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerViewTrailers.setLayoutManager(layoutManager);
        TrailerListAdapter adapter = new TrailerListAdapter(getContext(), data);
        mBinding.recyclerViewTrailers.setAdapter(adapter);
        mBinding.recyclerViewTrailers.setVisibility(View.VISIBLE);
    }

    private void showNaMessage() {
        mBinding.textTrailerError.setVisibility(View.GONE);
        mBinding.textTrailerNa.setVisibility(View.VISIBLE);
        mBinding.recyclerViewTrailers.setVisibility(View.GONE);
    }

    private void showError() {
        mBinding.textTrailerError.setVisibility(View.VISIBLE);
        mBinding.textTrailerNa.setVisibility(View.GONE);
        mBinding.recyclerViewTrailers.setVisibility(View.GONE);
    }
}
