package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

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
    private ArrayList<Film> mMovieList;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFilmSelectListener) activity;
        } catch (ClassCastException e) {
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
        if (!fillRecyclerView(view)) {
            view = inflater.inflate(R.layout.list_empty_layout, container, false);
            if (!hasInternetConnection()) {
                ((TextView) (view.findViewById(R.id.emptyText))).setText("Žádné připojení");
            }
        }
        else {
            if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
                mPosition = savedInstanceState.getInt(SELECTED_KEY);

                if (mPosition != ListView.INVALID_POSITION) {
                    mRecyclerView.smoothScrollToPosition(mPosition);
                }
            }
        }
        return view;
    }

    public boolean hasInternetConnection()
    {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        return (network != null && network.isConnected());
    }

    private boolean fillRecyclerView(View rootView) {
        mMovieList = FilmData.getInstance().getFilmList();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_films);

        if (mMovieList != null && !mMovieList.isEmpty()) {
            setAdapter(mRecyclerView, mMovieList);
            return true;
        }
        return false;
    }

    private void setAdapter(RecyclerView filmRV, final ArrayList<Film> movieList) {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(movieList, mContext, this);
        filmRV.setAdapter(adapter);
        filmRV.setLayoutManager(new LinearLayoutManager(mContext));
        filmRV.setItemAnimator(new DefaultItemAnimator());
    }

    public void clickedFilm(int position)
    {
        mPosition = position;
        mListener.onFilmSelect(mMovieList.get(position));
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
}