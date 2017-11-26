package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFilmSelectListener {

    private boolean mTwoPane;
    private ArrayList<Film> mFilmList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFilmList.add(new Film(Calendar.getInstance().getTimeInMillis(), "", "Film 1", "", 1));
        mFilmList.add(new Film(Calendar.getInstance().getTimeInMillis(), "", "Film 2", "", 2));
        mFilmList.add(new Film(Calendar.getInstance().getTimeInMillis(), "", "Film 3", "", 3));

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.film_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.film_detail_container, new DetailFragment(), DetailFragment.TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public void onFilmSelect(Film film) {
        if (mTwoPane) {
            FragmentManager fm = getSupportFragmentManager();

            DetailFragment fragment = DetailFragment.newInstance(film);
            fm.beginTransaction()
                    .replace(R.id.film_detail_container, fragment, DetailFragment.TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, FilmDetailActivity.class);
            intent.putExtra(FilmDetailActivity.DETAILED_FILM, film);
            startActivity(intent);
        }
    }

    public ArrayList<Film> getFilmList() {
        return mFilmList;
    }
}
