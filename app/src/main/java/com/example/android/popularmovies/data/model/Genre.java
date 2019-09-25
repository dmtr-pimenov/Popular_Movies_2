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

@Entity(tableName = "genre", indices = {
        @Index(value = {"id"}, unique = true),
        @Index(value = {"movie_id"})},
        foreignKeys = @ForeignKey(entity = MovieDetail.class, parentColumns = "id",
                childColumns = "movie_id", onDelete = CASCADE)
)
public class Genre {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @SerializedName("genreId")
    private Integer genreId;

    @ColumnInfo(name = "movie_id")
    private Long movieId;

    // Constructor used by Room to create Backdrop instance
    public Genre(Long id, Integer genreId, Long movieId) {
        this.id = id;
        this.genreId = genreId;
        this.movieId = movieId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
}
