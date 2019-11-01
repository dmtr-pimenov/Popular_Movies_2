package dmtr.pimenov.popularmovies.util;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class MarginItemDecorator extends RecyclerView.ItemDecoration {

    private final int mSpace;
    private int mSpanCount = 1;

    public MarginItemDecorator(int space) {
        mSpace = space;
    }

    public MarginItemDecorator(int space, int spanCount) {
        mSpace = space;
        mSpanCount = spanCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position < mSpanCount) {
            outRect.top = mSpace;
        }
        outRect.bottom = mSpace;

        if (mSpanCount == 1) {
            outRect.left = mSpace;
            outRect.right = mSpace;
        } else {
            int column = position % mSpanCount;
            outRect.left = mSpace - column * mSpace / mSpanCount;
            outRect.right = (column + 1) * mSpace / mSpanCount;
        }
    }
}
