package dmtr.pimenov.popularmovies.ui.fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dmtr.pimenov.popularmovies.R;
import dmtr.pimenov.popularmovies.data.model.Resource;
import dmtr.pimenov.popularmovies.data.model.Review;
import dmtr.pimenov.popularmovies.databinding.FragmentReviewsBinding;
import dmtr.pimenov.popularmovies.ui.MovieDetailViewModel;
import dmtr.pimenov.popularmovies.ui.adapter.ReviewListAdapter;

import java.util.List;

public class ReviewsFragment extends Fragment {

    private MovieDetailViewModel mViewModel;
    private FragmentReviewsBinding mBinding;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    public static ReviewsFragment newInstance() {
        ReviewsFragment fragment = new ReviewsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reviews, container, false);

        mViewModel = ViewModelProviders.of(getActivity()).get(MovieDetailViewModel.class);
        mViewModel.getReviewCollection().observe(this, new Observer<Resource<List<Review>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Review>> listResource) {
                if (listResource != null) {
                    if (listResource.status == Resource.Status.SUCCESS) {
                        List<Review> data = listResource.data;
                        if (data.size() == 0) {
                            showNaMessage();
                        } else {
                            setupRecyclerView(data);
                        }
                    } else {
                        showError();
                    }
                } else {
                    showNaMessage();
                }
            }
        });
        return mBinding.getRoot();
    }

    private void setupRecyclerView(List<Review> data) {
        mBinding.textReviewError.setVisibility(View.GONE);
        mBinding.textReviewNa.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerViewReview.setLayoutManager(layoutManager);
        ReviewListAdapter adapter = new ReviewListAdapter(getContext(), data);
        mBinding.recyclerViewReview.setAdapter(adapter);
        mBinding.recyclerViewReview.setVisibility(View.VISIBLE);
    }

    private void showNaMessage() {
        mBinding.textReviewError.setVisibility(View.GONE);
        mBinding.textReviewNa.setVisibility(View.VISIBLE);
        mBinding.recyclerViewReview.setVisibility(View.GONE);
    }

    private void showError() {
        mBinding.textReviewError.setVisibility(View.VISIBLE);
        mBinding.textReviewNa.setVisibility(View.GONE);
        mBinding.recyclerViewReview.setVisibility(View.GONE);
    }

}
