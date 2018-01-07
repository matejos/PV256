package cz.muni.fi.pv256.movio2.uco_422476;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cz.muni.fi.pv256.movio2.uco_422476.MainActivity.mCategory;

/**
 * Created by Matej on 3.1.2018.
 */

public class DownloadService extends IntentService {
    public static final String TAG = DownloadService.class.getSimpleName();
    public static final String ACTION = "downloading";
    public static final String ERROR = "error";
    public static final String NO_ERROR = "no error";
    public static final String CONNECTION_ERROR = "connection error";
    public static final String POPULAR = "popular";
    public static final String LATEST = "latest";

    public DownloadService() {
        super(TAG);
    }

    public DownloadService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION);

        try {
            if(!hasInternetConnection(this)) {
                Toast.makeText(this, getResources().getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                broadcastIntent.putExtra(ERROR, CONNECTION_ERROR);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                errorDownloadNotification();
            }
            else {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(App.API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                FilmAPI service = retrofit.create(FilmAPI.class);

                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateNowString = formatter.format(cal.getTime());
                cal.add(Calendar.MONTH, -1);
                String dateMonthAgoString = formatter.format(cal.getTime());

                startDownloadNotification();
                Toast.makeText(this, R.string.dataDownloading, Toast.LENGTH_SHORT).show();
                Call<FilmList> request;
                FilmList films;

                        request = service.getLatestFilms(dateMonthAgoString, dateNowString);
                        films = request.execute().body();
                        broadcastIntent.putExtra(LATEST, new ArrayList<FilmDTO>(films.getResults()));

                        request = service.getPopularFilms();
                        films = request.execute().body();
                        broadcastIntent.putExtra(POPULAR, new ArrayList<FilmDTO>(films.getResults()));

                broadcastIntent.putExtra(ERROR, NO_ERROR);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                doneDownloadNotification();
            }
        } catch (IOException e) {
            e.printStackTrace();
            broadcastIntent.putExtra(ERROR, CONNECTION_ERROR);
            sendBroadcast(broadcastIntent);
        }
    }

    private Notification.Builder prepareNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder n = new Notification.Builder(this);
        n.setContentTitle(getResources().getString(R.string.app_name))
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();
        return n;
    }

    private void startDownloadNotification() {
        Notification.Builder n = prepareNotification();
        n.setContentText(getResources().getString(R.string.dataDownloading)).setSmallIcon(R.mipmap.ic_download);

        NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n.build());
    }

    private void doneDownloadNotification() {
        Notification.Builder n = prepareNotification();
        n.setContentText(getResources().getString(R.string.dataSuccess)).setSmallIcon(R.mipmap.ic_success);

        NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n.build());
    }

    private void errorDownloadNotification() {
        Notification.Builder n = prepareNotification();
        n.setContentText(getResources().getString(R.string.dataError)).setSmallIcon(R.mipmap.ic_error);
        NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n.build());
    }

    public boolean hasInternetConnection(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        return (network != null && network.isConnected());
    }
}
