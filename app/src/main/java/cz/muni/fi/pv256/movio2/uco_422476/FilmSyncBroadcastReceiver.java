package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Matej on 7.1.2018.
 */

public class FilmSyncBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UpdaterSyncAdapter.getSyncAccount(context);
    }
}
