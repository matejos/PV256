package cz.muni.fi.pv256.movio2.uco_422476;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

/**
 * Created by Matej on 5.10.2017.
 */

public class App extends Application {

    public static final String API_KEY = "92c2177de82d888d852b59046911ff8b";
    public static final String API_URL = "http://api.themoviedb.org/";
    public static final String COVER_URL = "https://image.tmdb.org/t/p/w342/%s";
    public static final String BACKDROP_URL = "https://image.tmdb.org/t/p/w500/%s";

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            initStrictMode();
        }
    }

    private void initStrictMode() {
        StrictMode.ThreadPolicy.Builder tpb = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            tpb.penaltyFlashScreen();
        }
        StrictMode.setThreadPolicy(tpb.build());

        StrictMode.VmPolicy.Builder vmpb = new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            vmpb.detectLeakedClosableObjects();
        }
        StrictMode.setVmPolicy(vmpb.build());
    }
}
