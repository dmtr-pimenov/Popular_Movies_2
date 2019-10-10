package dmtr.example.android.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BackdropCollection {

    @SerializedName("id")
    private Integer id;

    @SerializedName("backdrops")
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