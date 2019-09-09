package com.example.android.popularmovies.data.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BackdropCollection {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("backdrops")
    @Expose
    private List<Backdrop> backdrops = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Backdrop> getBackdrops() {
        return backdrops;
    }

    public void setBackdrops(List<Backdrop> backdrops) {
        this.backdrops = backdrops;
    }
}