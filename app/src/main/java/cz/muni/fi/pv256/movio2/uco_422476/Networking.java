package cz.muni.fi.pv256.movio2.uco_422476;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static cz.muni.fi.pv256.movio2.uco_422476.App.API_KEY;

/**
 * Created by Matej on 26.11.2017.
 */

public class Networking {

    private static String url = "http://api.themoviedb.org/";
    private static String call = "3/discover/movie?api_key=";

    private Networking() {}

    public static ArrayList<Film> getPopularFilms() throws IOException {
        String filter = "&sort_by=popularity.desc";
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url + call + API_KEY + filter)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        return getData(response.body().string());
    }

    public static ArrayList<Film> getLatestFilms() throws IOException {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowString = formatter.format(cal.getTime());
        cal.add(Calendar.MONTH, -1);
        String dateMonthAgoString = formatter.format(cal.getTime());
        String filter = "&primary_release_date.gte=" + dateMonthAgoString + "&primary_release_date.lte=" + dateNowString;
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url + call + API_KEY + filter)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        return getData(response.body().string());
    }

    private static ArrayList<Film> getData(String data) {
        ArrayList<Film> mData = new ArrayList();

        try {
            JSONObject json = new JSONObject(data);
            Gson gson = new Gson();
            ArrayList<FilmDTO> films = gson.fromJson(json.getJSONArray("results").toString(), new TypeToken<List<FilmDTO>>() {
            }.getType());

            for (FilmDTO m : films) {
                Film film = new Film(m.getReleaseDateAsLong(), m.getCoverPath(), m.getTitle(), m.getBackdrop(), m.getPopularityAsFloat(), m.getDescription());
                mData.add(film);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList();
        }
        return mData;
    }
}
