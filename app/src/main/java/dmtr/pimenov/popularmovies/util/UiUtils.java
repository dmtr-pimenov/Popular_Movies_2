package dmtr.pimenov.popularmovies.util;

import android.content.Context;
import android.os.Build;

import dmtr.pimenov.popularmovies.data.AppRepository;

public class UiUtils {
    public static boolean isTransitionAvailable(Context context) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                && AppRepository.isTransitionEnabled(context);
    }
}
