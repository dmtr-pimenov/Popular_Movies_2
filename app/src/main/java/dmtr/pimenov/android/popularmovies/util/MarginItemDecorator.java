package dmtr.pimenov.android.popularmovies.util;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MarginItemDecorator extends RecyclerView.ItemDecoration {

    private int mSpaceHeight;

    public MarginItemDecorator(int spaceHeight) {
        mSpaceHeight = spaceHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = mSpaceHeight;
        }
        outRect.bottom = mSpaceHeight;
        outRect.left = mSpaceHeight;
        outRect.right = mSpaceHeight;
    }
}
