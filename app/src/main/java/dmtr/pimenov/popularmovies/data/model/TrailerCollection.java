package dmtr.pimenov.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerCollection {

    @SerializedName("id")
    private Integer id;

    @SerializedName("results")
    private List<Trailer> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }
}