package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

/**
 * Created by Matej on 3.11.2017.
 */

public class FilmDetailActivity extends AppCompatActivity {

    public static final String DETAILED_FILM = "detailed_film";
    public static final String SWITCH = "switch";
    public static final String POSITION = "position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Film film = getIntent().getParcelableExtra(DETAILED_FILM);
        if (findViewById(R.id.widedetail) != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.SELECTED_FILM, film);
            intent.putExtra(MainActivity.SWITCH, getIntent().getBooleanExtra(SWITCH, false));
            intent.putExtra(MainActivity.POSITION, getIntent().getIntExtra(POSITION, ListView.INVALID_POSITION));
            startActivity(intent);
            return;
        }
        if(savedInstanceState == null){
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
