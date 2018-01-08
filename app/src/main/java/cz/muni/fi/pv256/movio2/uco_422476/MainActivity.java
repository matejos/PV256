package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFilmSelectListener {

    private boolean mTwoPane;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mMenuItems;
    private boolean mDrawerOpen;
    protected static ArrayList<ListItem> mData = new ArrayList<>();
    protected static ArrayList<ListItem> mFavoriteData = new ArrayList<>();
    protected static int mCategory = 0;
    private SwitchCompat mSwitch;
    private boolean mSwitched;
    private int mPosition;
    private TextView mTitleTextView;
    public static final String SELECTED_FILM = "selected_film";
    public static final String SWITCH = "switch";
    public static final String POSITION = "position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SWITCH)) {
            mSwitched = savedInstanceState.getBoolean(SWITCH);
        }

        mMenuItems = getResources().getStringArray(R.array.menu_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mMenuItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (mDrawerOpen)
        {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mPosition = getIntent().getIntExtra(POSITION, ListView.INVALID_POSITION);
        Film film = getIntent().getParcelableExtra(SELECTED_FILM);
        if (film != null) {
            onFilmSelect(film, mPosition);
        }
        mSwitched = getIntent().getBooleanExtra(SWITCH, false);

        ((MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main)).setPosition(mPosition);
        ((MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main)).updateData();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SWITCH, mSwitch.isChecked());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.switchForActionBar);
        item.setActionView(R.layout.switch_layout);
        mSwitch = (SwitchCompat) item.getActionView().findViewById(R.id.switchForActionBar);
        mTitleTextView = (TextView) item.getActionView().findViewById(R.id.actionBarTitle);
        mTitleTextView.setText(getApplicationContext().getApplicationInfo().nonLocalizedLabel);
        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    compoundButton.setText(getResources().getString(R.string.favorites));
                    compoundButton.setChecked(true);
                    MainFragment mainFragment = (MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                    mainFragment.setPosition(0);
                    mainFragment.setIsFavorite(true);
                    mainFragment.updateData();

                } else {
                    compoundButton.setText(getResources().getString(R.string.discover));
                    compoundButton.setChecked(false);
                    MainFragment mainFragment = (MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                    mainFragment.setPosition(0);
                    mainFragment.setIsFavorite(false);
                    mainFragment.updateData();
                }
            }
        });
        mSwitch.setChecked(mSwitched);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerOpen)
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                else
                    mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mSwitch.isChecked())
                mSwitch.toggle();
            selectItem(position);
            MainFragment mainFragment = (MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            mainFragment.updateData();
        }
    }

    private void selectItem(int position) {
        mDrawerList.setItemChecked(position, false);
        mCategory = position;
        mDrawerLayout.closeDrawer(mDrawerList);
        ((MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main)).scrollToCategory(position);
    }

    @Override
    public void onFilmSelect(Film film, int position) {
        if (mTwoPane) {
            FragmentManager fm = getSupportFragmentManager();

            DetailFragment fragment = DetailFragment.newInstance(film);
            fm.beginTransaction()
                    .replace(R.id.film_detail_container, fragment, DetailFragment.TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, FilmDetailActivity.class);
            intent.putExtra(FilmDetailActivity.DETAILED_FILM, film);
            intent.putExtra(FilmDetailActivity.SWITCH, mSwitch.isChecked());
            intent.putExtra(FilmDetailActivity.POSITION, position);
            startActivity(intent);
        }
    }
}
