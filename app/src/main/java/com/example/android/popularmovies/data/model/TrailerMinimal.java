package com.example.android.popularmovies.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * This is minimal set of information about Trailer.
 * Used to display Trailer List in DetailActivity
 * and to store Trailer info in the Database
 */

@Entity(
        tableName = "trailer_minimal",
        foreignKeys = @ForeignKey(entity = Movie.class, parentColumns = "id",
                childColumns = "movie_id", onDelete = CASCADE),
        indices = {@Index(value = {"movie_id"})}
)
public class TrailerMinimal {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String id;

    @ColumnInfo(name = "movie_id")
    private Long movieId;

    private String key;

    private String name;

    public TrailerMinimal(@NonNull String id, Long movieId, String key, String name) {
        this.id = id;
        this.movieId = movieId;
        this.key = key;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
