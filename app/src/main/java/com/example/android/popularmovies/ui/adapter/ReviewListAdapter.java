package com.example.android.popularmovies.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.model.ReviewMinimal;

import java.util.List;

/**
 * ReviewListAdapter to display Review List
 * each list item has 3 TextView:
 * 1. Author Name
 * 2. Review Content
 * 3. Http Link to Review on web site
 * Also OnClickListener is attached to TextView that contains Http Link
 * It is necessary to open the Review in external browser
 */
public class ReviewListAdapter extends BaseAdapter implements View.OnClickListener {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final List<ReviewMinimal> mItems;
    private final OnLinkClickListener mListener;

    public ReviewListAdapter(Context context, List<ReviewMinimal> reviews, OnLinkClickListener listener) {
        mContext = context;
        mItems = reviews;
        mInflater = LayoutInflater.from(mContext);
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.lv_review_layout, parent, false);
        }
        TextView text = view.findViewById(R.id.tv_reviewer_name);
        text.setText(mItems.get(position).getAuthor());

        text = view.findViewById(R.id.tv_review_text);
        text.setText(mItems.get(position).getContent());

        text = view.findViewById(R.id.tv_review_link);
        text.setText(mItems.get(position).getUrl());
        text.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            String s = ((TextView) v).getText().toString();
            mListener.onLinkClickListener(s);
        }
    }

    public interface OnLinkClickListener {
        public void onLinkClickListener(String link);
    }
}
