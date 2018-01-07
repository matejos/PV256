package cz.muni.fi.pv256.movio2.uco_422476;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import static cz.muni.fi.pv256.movio2.uco_422476.App.API_KEY;

/**
 * Created by Matej on 3.1.2018.
 */

public interface FilmAPI {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })

    @GET("3/discover/movie?api_key=" + API_KEY + "&sort_by=popularity.desc")
    Call<FilmList> getPopularFilms();

    @GET("3/discover/movie?api_key=" + API_KEY)
    Call<FilmList> getLatestFilms(@Query("primary_release_date.gte") String dateMonthAgo, @Query("primary_release_date.lte") String dateNow);
}
