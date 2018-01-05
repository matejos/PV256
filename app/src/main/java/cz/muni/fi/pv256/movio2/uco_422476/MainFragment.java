package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ListView;

import java.util.ArrayList;

import static cz.muni.fi.pv256.movio2.uco_422476.MainActivity.mData;
import static cz.muni.fi.pv256.movio2.uco_422476.MainActivity.mCategory;
import static cz.muni.fi.pv256.movio2.uco_422476.DownloadService.ACTION;
import static cz.muni.fi.pv256.movio2.uco_422476.DownloadService.ERROR;
import static cz.muni.fi.pv256.movio2.uco_422476.DownloadService.LATEST;
import static cz.muni.fi.pv256.movio2.uco_422476.DownloadService.NO_ERROR;
import static cz.muni.fi.pv256.movio2.uco_422476.DownloadService.POPULAR;

/**
 * Created by Matej on 3.11.2017.
 */

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "selected_position";

    private int mPosition = ListView.INVALID_POSITION;
    private OnFilmSelectListener mListener;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ViewStub mEmptyView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private DownloadReceiver mReceiver;

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
        downloadAndUpdate();

        return view;
    }

    public void clickedFilm(int position)
    {
        mPosition = position;
        mListener.onFilmSelect(((ArrayList<Film>)mData.get(mCategory)).get(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public interface OnFilmSelectListener {
        void onFilmSelect(Film film);
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
            updateViewAdapter();
        }
    }

    private void updateViewAdapter() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerViewAdapter.dataUpdate(((ArrayList<Film>)mData.get(mCategory)));
                if (mData.get(mCategory) != null && !((ArrayList<Film>)mData.get(mCategory)).isEmpty()) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mRecyclerView.smoothScrollToPosition(0);
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
                    updateViewAdapter();
                }
            else {
                updateViewAdapter();
            }
        }
        private ArrayList<Film> getFilms(ArrayList<FilmDTO> filmList){
            ArrayList<Film> films = new ArrayList<Film>();
            for (FilmDTO m : filmList) {
                Film film = new Film(m.getReleaseDateAsLong(), m.getCoverPath(), m.getTitle(), m.getBackdrop(), m.getPopularityAsFloat(), m.getDescription());
                films.add(film);
                }
            return films;
        }
     }
}