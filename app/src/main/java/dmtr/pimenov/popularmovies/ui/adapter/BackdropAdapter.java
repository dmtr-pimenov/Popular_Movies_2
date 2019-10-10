package dmtr.pimenov.popularmovies.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import dmtr.pimenov.popularmovies.R;
import dmtr.pimenov.popularmovies.data.model.Backdrop;
import dmtr.pimenov.popularmovies.data.network.NetworkApi;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BackdropAdapter extends PagerAdapter {

    private final Context mContext;
    private final List<Backdrop> mBackdropList;
    private final LayoutInflater mLayoutInflater;

    public BackdropAdapter(@NonNull Context context, @Nullable List<Backdrop> backdropList) {
        mContext = context;
        mBackdropList = backdropList;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mBackdropList == null) {
            return 0;
        } else {
            return mBackdropList.size();
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = mLayoutInflater.inflate(R.layout.layout_movie_backdrop, container, false);
        ImageView imageView = view.findViewById(R.id.image_backdrop);

        String filePath = mBackdropList.get(position).getFilePath();
        String backdropUrl = NetworkApi.getBackdropUrl(filePath, NetworkApi.BackdropSize.W780);

        Drawable error = ContextCompat.getDrawable(mContext, R.drawable.ic_error);

        // show backdrop image
        Picasso.with(mContext).load(backdropUrl)
                .error(error)
                .into(imageView);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
