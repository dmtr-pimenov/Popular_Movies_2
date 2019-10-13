package dmtr.pimenov.popularmovies.data.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import dmtr.pimenov.popularmovies.data.model.Backdrop;
import dmtr.pimenov.popularmovies.data.model.Genre;
import dmtr.pimenov.popularmovies.data.model.MovieDetail;
import dmtr.pimenov.popularmovies.data.model.Review;
import dmtr.pimenov.popularmovies.data.model.Trailer;

@Database(entities = {MovieDetail.class, Genre.class, Trailer.class, Review.class, Backdrop.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String TAG = AppDatabase.class.getSimpleName();
    
    private static final String DATABASE_NAME = "popular_movies";

    private static final Object lock = new Object();
    private static volatile AppDatabase mInstance;

    public static AppDatabase getInstance(Context context) {
        if (mInstance == null) {
            synchronized (lock) {
                if (mInstance == null) {
                    mInstance = Room.databaseBuilder(context,
                            AppDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return mInstance;
    }

    public abstract MovieDao getMovieDao();
}
