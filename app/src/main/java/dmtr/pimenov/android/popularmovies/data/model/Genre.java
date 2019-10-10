package dmtr.pimenov.android.popularmovies.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
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
public class Genre implements IIdSetter {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long entityId;

    @SerializedName("id")
    @ColumnInfo(name = "genre_id")
    private Integer genreId;

    @ColumnInfo(name = "movie_id")
    private Long movieId;

    @Ignore
    public Genre(Integer genreId, Long movieId) {
        this.genreId = genreId;
        this.movieId = movieId;
    }

    // Constructor used by Room to create Backdrop instance
    public Genre(Long entityId, Integer genreId, Long movieId) {
        this.entityId = entityId;
        this.genreId = genreId;
        this.movieId = movieId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long id) {
        this.entityId = id;
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
