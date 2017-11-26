package cz.muni.fi.pv256.movio2.uco_422476;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Matej on 3.11.2017.
 */

public class FilmData {

    private static FilmData sInstance;
    private ArrayList<Film> mFilmList = new ArrayList<>();

    private FilmData() {
        initFilmList();
    }

    public static FilmData getInstance() {
        if (sInstance == null) {
            sInstance = new FilmData();
        }
        return sInstance;
    }

    private void initFilmList() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));

        cal.set(2016, 01, 12);
        mFilmList.add(new Film(cal.getTimeInMillis(), "deadpool", "Deadpool", "deadpool_back", 7.4f));
        cal.set(2016, 05, 10);
        mFilmList.add(new Film(cal.getTimeInMillis(), "now_you_see_me", "Now You See Me 2", "now_you_see_me_back", 6.7f));
        cal.set(2017, 06, 23);
        mFilmList.add(new Film(cal.getTimeInMillis(), "emoji_movie", "The Emoji Movie", "emoji_movie_back", 5.7f));
    }

    public ArrayList<Film> getFilmList() {
        return mFilmList;
    }

    public void setFilmList(ArrayList<Film> filmList) {
        mFilmList = filmList;
    }
}