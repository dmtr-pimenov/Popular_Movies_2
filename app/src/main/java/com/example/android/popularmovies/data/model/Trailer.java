package com.example.android.popularmovies.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * This Entity is used for Network and DB Operation
 */

@Entity(
        tableName = "trailer",
        foreignKeys = @ForeignKey(entity = MovieDetail.class, parentColumns = "id",
                childColumns = "movie_id", onDelete = CASCADE),
        indices = {@Index(value = {"movie_id"})}
)
public class Trailer {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    private String id;

    @SerializedName("key")
    private String key;

    @ColumnInfo(name = "movie_id")
    private Long movieId;

    @SerializedName("name")
    private String name;

    // Constructor used by Room to create Review instance
    public Trailer(String id, String key, Long movieId, String name) {
        this.id = id;
        this.key = key;
        this.movieId = movieId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
