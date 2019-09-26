package com.example.android.popularmovies.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.model.Trailer;

import java.util.List;

public class TrailerListAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    List<Trailer> mItems;

    public TrailerListAdapter(Context context, List<Trailer> trailers) {
        mContext = context;
        mItems = trailers;
        mInflater = LayoutInflater.from(mContext);
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
            view = mInflater.inflate(R.layout.lv_trailer_layout, parent, false);
        }
        TextView text = view.findViewById(R.id.tv_trailer_name);
        text.setText(mItems.get(position).getName());
        return view;
    }
}
