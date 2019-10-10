package dmtr.pimenov.popularmovies.util;

import java.util.concurrent.Executor;

public interface IAppExecutors {
    Executor diskIO();
    Executor mainThread();
    Executor networkIO();
}
