package dmtr.pimenov.android.popularmovies.ui.fragment;

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
import dmtr.pimenov.android.popularmovies.data.model.Resource;
import dmtr.pimenov.android.popularmovies.data.model.Review;
import com.example.android.popularmovies.databinding.FragmentReviewsBinding;
import dmtr.pimenov.android.popularmovies.ui.MovieDetailViewModel;
import dmtr.pimenov.android.popularmovies.ui.adapter.ReviewListAdapter;

import java.util.List;

public class ReviewsFragment extends Fragment {

    private MovieDetailViewModel mViewModel;
    private FragmentReviewsBinding mBinding;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    public static ReviewsFragment newInstance() {
        ReviewsFragment fragment = new ReviewsFragment();
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reviews, container, false);
        mViewModel.getReviewCollection().observe(this, new Observer<Resource<List<Review>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Review>> listResource) {
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
/*
        mBinding.recyclerViewTrailers
                .addItemDecoration(new MarginItemDecorator((int) getContext().getResources().getDimension(R.dimen.double_margin)));
*/
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
