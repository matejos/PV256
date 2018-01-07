package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static cz.muni.fi.pv256.movio2.uco_422476.MainActivity.mData;
import static cz.muni.fi.pv256.movio2.uco_422476.MainActivity.mFavoriteData;
import static cz.muni.fi.pv256.movio2.uco_422476.MainActivity.mCategory;
import static cz.muni.fi.pv256.movio2.uco_422476.DownloadService.ACTION;
import static cz.muni.fi.pv256.movio2.uco_422476.DownloadService.ERROR;
import static cz.muni.fi.pv256.movio2.uco_422476.DownloadService.LATEST;
import static cz.muni.fi.pv256.movio2.uco_422476.DownloadService.NO_ERROR;
import static cz.muni.fi.pv256.movio2.uco_422476.DownloadService.POPULAR;

/**
 * Created by Matej on 3.11.2017.
 */

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Film>>{

    private static final String TAG = MainFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "selected_position";

    public int mPosition = ListView.INVALID_POSITION;
    private OnFilmSelectListener mListener;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ViewStub mEmptyView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private DownloadReceiver mReceiver;
    private SQLiteDatabase mDatabase;
    private boolean mIsFavorite;
    private FilmManager mFilmManager;
    private FilmDbHelper mDbHelper;

    public boolean getIsFavorite() { return mIsFavorite; }

    public void setIsFavorite(boolean isFavorite) { mIsFavorite = isFavorite; }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFilmSelectListener) activity;
        } catch (ClassCastException e) {
            if(BuildConfig.logging)
                Log.e(TAG, "Activity must implement OnFilmSelectListener", e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (mIsFavorite) {
            updateData();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mEmptyView = (ViewStub) view.findViewById(R.id.empty);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_films);
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            if (mPosition != ListView.INVALID_POSITION) {
                mRecyclerView.smoothScrollToPosition(mPosition);
            }
        }
        mRecyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<Film>(), mContext, this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        updateData();
        return view;
    }

    public void clickedFilm(int position)
    {
        if (position == ListView.INVALID_POSITION)
            return;
        mPosition = position;
        if (mIsFavorite)
            mListener.onFilmSelect(mFavoriteData.get(position), position);
        else
            mListener.onFilmSelect(((ArrayList<Film>)mData.get(mCategory)).get(position), position);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            mRecyclerView.smoothScrollToPosition(mPosition);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<List<Film>> onCreateLoader(int id, Bundle args) {
        return new SQLiteFilmLoader(this.getActivity(), mFilmManager, null, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<List<Film>> loader, List<Film> data) {
        mFavoriteData.clear();
        for (Film film : data) {
            mFavoriteData.add(film);
        }
        if (mIsFavorite) {
            updateViewAdapter(mFavoriteData);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Film>> loader) {
    }

    public interface OnFilmSelectListener {
        void onFilmSelect(Film film, int position);
    }

    public void updateData() {
        if (mIsFavorite) {
            mDbHelper = new FilmDbHelper(getActivity());
            mDatabase = mDbHelper.getWritableDatabase();
            mFilmManager = new FilmManager(mDatabase);
            getLoaderManager().restartLoader(1, null, this);
        }
        else {
            downloadAndUpdate();
        }
    }

    public void downloadAndUpdate() {
        if (mData.get(mCategory) == null) {
            Intent intent = new Intent(getActivity(), DownloadService.class);
            getActivity().startService(intent);
            IntentFilter intentFilter = new IntentFilter(ACTION);
            mReceiver = new DownloadReceiver();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
        else {
            updateViewAdapter((ArrayList<Film>)mData.get(mCategory));
        }
    }

    private void updateViewAdapter(final ArrayList<Film> filmList) {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerViewAdapter.dataUpdate(filmList);
                if (filmList != null && !(filmList.isEmpty())) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    if (mPosition == ListView.INVALID_POSITION)
                        mPosition = 0;
                    mRecyclerView.smoothScrollToPosition(mPosition);
                    mEmptyView.setVisibility(View.GONE);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public class DownloadReceiver extends BroadcastReceiver {
         @Override
         public void onReceive(Context context, Intent intent) {
            String error = intent.getStringExtra(ERROR);
            if(error.equals(NO_ERROR)) {
                    mData.put(mCategory, new ArrayList<Film>());
                    switch (mCategory) {
                        case 0:
                            ((ArrayList<Film>)mData.get(mCategory)).addAll(getFilms((ArrayList<FilmDTO>)intent.getSerializableExtra(LATEST)));
                            break;
                        case 1:
                            ((ArrayList<Film>)mData.get(mCategory)).addAll(getFilms((ArrayList<FilmDTO>)intent.getSerializableExtra(POPULAR)));
                            break;
                    }
                    updateViewAdapter((ArrayList<Film>)mData.get(mCategory));
                }
            else {
                updateViewAdapter((ArrayList<Film>)mData.get(mCategory));
            }
        }
        private ArrayList<Film> getFilms(ArrayList<FilmDTO> filmList){
            ArrayList<Film> films = new ArrayList<Film>();
            for (FilmDTO m : filmList) {
                Film film = new Film(Long.parseLong(m.getId(), 10), m.getReleaseDateAsLong(), m.getCoverPath(), m.getTitle(), m.getBackdrop(), m.getPopularityAsFloat(), m.getDescription());
                films.add(film);
                }
            return films;
        }
     }
}