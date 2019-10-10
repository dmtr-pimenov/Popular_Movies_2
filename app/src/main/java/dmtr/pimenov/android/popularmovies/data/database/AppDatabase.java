package dmtr.pimenov.android.popularmovies.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import dmtr.pimenov.android.popularmovies.data.model.Backdrop;
import dmtr.pimenov.android.popularmovies.data.model.Genre;
import dmtr.pimenov.android.popularmovies.data.model.MovieDetail;
import dmtr.pimenov.android.popularmovies.data.model.Review;
import dmtr.pimenov.android.popularmovies.data.model.Trailer;

@Database(entities = {MovieDetail.class, Genre.class, Trailer.class, Review.class, Backdrop.class},
        version = 2, exportSchema = false)
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
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        Log.d(TAG, "getInstance");
        return mInstance;
    }

    public abstract MovieDao getMovieDao();
}
