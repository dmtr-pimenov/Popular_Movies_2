package com.example.android.popularmovies.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * This Entity is used for Network and DB Operation
 */

@Entity(tableName = "backdrop", indices = {
        @Index(value = {"id"}, unique = true),
        @Index(value = {"movie_id"})},
        foreignKeys = @ForeignKey(entity = MovieDetail.class, parentColumns = "id",
                childColumns = "movie_id", onDelete = CASCADE)
)
public class Backdrop implements IIdSetter {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "movie_id")
    private Long movieId;

    @ColumnInfo(name = "aspect_ratio")
    @SerializedName("aspect_ratio")
    @Expose
    private Double aspectRatio;

    @ColumnInfo(name = "file_path")
    @SerializedName("file_path")
    @Expose
    private String filePath;

    @SerializedName("height")
    @Expose
    private Integer height;

    @SerializedName("width")
    @Expose
    private Integer width;

    // Constructor used by Room to create Backdrop instance
    public Backdrop(Long id, Long movieId, Double aspectRatio, String filePath,
                    Integer height, Integer width) {
        this.id = id;
        this.movieId = movieId;
        this.aspectRatio = aspectRatio;
        this.filePath = filePath;
        this.height = height;
        this.width = width;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Double getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(Double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }
}
