package com.example.android.popularmovies.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * This is minimal set of information about Review.
 * Used to display Review List in DetailActivity
 * and to store Review info in the Database
 */

@Entity(
        tableName = "review_minimal",
        foreignKeys = @ForeignKey(entity = Movie.class, parentColumns = "id",
                childColumns = "movie_id", onDelete = CASCADE),
        indices = {@Index(value = {"movie_id"})}
)
public class ReviewMinimal {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String id;

    @ColumnInfo(name = "movie_id")
    private Long movieId;

    private String author;

    private String content;

    private String url;

    public ReviewMinimal(@NonNull String id, Long movieId, String author, String content, String url) {
        this.id = id;
        this.movieId = movieId;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
