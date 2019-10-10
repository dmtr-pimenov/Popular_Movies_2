package dmtr.example.android.popularmovies.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import dmtr.example.android.popularmovies.data.model.Review;

import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {

    private static final int MAX_LINES = 3;

    private List<Review> mReviews;
    private Context mContext;
    LayoutInflater mInflater;

    public ReviewListAdapter(Context context, @NonNull List<Review> reviews) {
        mReviews = reviews;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.review_list_item, viewGroup, false);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder viewHolder, int i) {
        viewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView reviewer;
        TextView review;
        AppCompatImageButton expandButton;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewer = itemView.findViewById(R.id.text_reviewer_name);
            review = itemView.findViewById(R.id.text_review_text);
            expandButton = itemView.findViewById(R.id.button_expand);
        }

        void bind(int itemIndex) {
            Review r = mReviews.get(itemIndex);
            reviewer.setText(r.getAuthor());
            review.setText(r.getContent());
            review.post(new Runnable() {

                int lineCount;
                @Override
                public void run() {
                    lineCount = review.getLineCount();
                    if (review.getLineCount() <= MAX_LINES) {
                        expandButton.setVisibility(View.GONE);
                    } else {
                        expandButton.setVisibility(View.VISIBLE);
                        review.setMaxLines(MAX_LINES);
                    }

                    expandButton.setOnClickListener(new View.OnClickListener() {
                        boolean expanded = false;
                        @Override
                        public void onClick(View view) {
                            if (expanded) {
                                expandButton.setImageResource(R.drawable.ic_keyboard_arrow_down);
                                review.setMaxLines(MAX_LINES);
                                expanded = false;
                            } else {
                                expandButton.setImageResource(R.drawable.ic_keyboard_arrow_up);
                                review.setMaxLines(lineCount);
                                expanded = true;
                            }
                        }
                    });

                }
            });
        }
    }
}
