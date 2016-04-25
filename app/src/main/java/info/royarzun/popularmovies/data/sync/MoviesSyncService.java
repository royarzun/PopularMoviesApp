package info.royarzun.popularmovies.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class MoviesSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MoviesSyncAdapter sPopularMoviesSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sPopularMoviesSyncAdapter == null) {
                sPopularMoviesSyncAdapter = new MoviesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPopularMoviesSyncAdapter.getSyncAdapterBinder();
    }
}