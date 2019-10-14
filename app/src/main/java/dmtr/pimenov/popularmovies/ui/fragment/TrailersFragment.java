package dmtr.pimenov.popularmovies.ui.fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import dmtr.pimenov.popularmovies.R;
import dmtr.pimenov.popularmovies.data.model.Resource;
import dmtr.pimenov.popularmovies.data.model.Trailer;
import dmtr.pimenov.popularmovies.databinding.FragmentTrailersBinding;
import dmtr.pimenov.popularmovies.ui.MovieDetailViewModel;
import dmtr.pimenov.popularmovies.ui.adapter.TrailerListAdapter;
import dmtr.pimenov.popularmovies.util.MarginItemDecorator;

import java.util.List;

public class TrailersFragment extends Fragment implements TrailerListAdapter.ListItemClickListener {

    private static final String TAG = TrailersFragment.class.getSimpleName();

    public static final String YOUTUBE_BASE_URI = "https://youtube.com/watch?v=";

    private MovieDetailViewModel mViewModel;
    private FragmentTrailersBinding mBinding;

    public TrailersFragment() {
        // Required empty public constructor
    }

    public static TrailersFragment newInstance() {
        TrailersFragment fragment = new TrailersFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trailers, container, false);

        mViewModel = ViewModelProviders.of(getActivity()).get(MovieDetailViewModel.class);
        mViewModel.getTrailerCollection().observe(this, new Observer<Resource<List<Trailer>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Trailer>> listResource) {
                if (listResource != null) {
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
                } else {
                    showNaMessage();
                }
            }
        });

        return mBinding.getRoot();
    }

    private void setupRecyclerView(List<Trailer> data) {

        mBinding.textTrailerError.setVisibility(View.GONE);
        mBinding.textTrailerNa.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerViewTrailers.setLayoutManager(layoutManager);
        TrailerListAdapter adapter = new TrailerListAdapter(getContext(), data, this);
        mBinding.recyclerViewTrailers.setAdapter(adapter);
        mBinding.recyclerViewTrailers
                .addItemDecoration(new MarginItemDecorator((int) getContext().getResources().getDimension(R.dimen.double_margin)));
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

    private void showTrailer(@Nullable Trailer trailer) {
        if (trailer != null) {
            //initialize a new intent with action
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(YOUTUBE_BASE_URI + trailer.getKey()));
            //check if intent is supported
            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                showToastMessage(R.string.error_no_player);
            }
        }
    }

    /**
     * Helper method
     *
     * @param messageId
     */
    private void showToastMessage(@StringRes int messageId) {
        Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListItemClick(Trailer trailer) {
        showTrailer(trailer);
    }
}
