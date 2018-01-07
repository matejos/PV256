package cz.muni.fi.pv256.movio2.uco_422476;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Matej on 3.11.2017.
 */

public class FilmDetailActivity extends AppCompatActivity {

    public static final String DETAILED_FILM = "detailed_film";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(savedInstanceState == null){
            Film film = getIntent().getParcelableExtra(DETAILED_FILM);
            FragmentManager fm = getSupportFragmentManager();
            DetailFragment fragment = (DetailFragment) fm.findFragmentById(R.id.film_detail_container);

            if (fragment == null) {
                fragment = DetailFragment.newInstance(film);
                fm.beginTransaction()
                        .add(R.id.film_detail_container, fragment)
                        .commit();
            }
        }
    }

}
