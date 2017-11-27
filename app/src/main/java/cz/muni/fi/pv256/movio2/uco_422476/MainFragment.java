package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cz.muni.fi.pv256.movio2.uco_422476.MainActivity.mData;
import static cz.muni.fi.pv256.movio2.uco_422476.MainActivity.mCategory;

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
    private DownloadingTask mDownloadingTask;

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
        if (!hasInternetConnection()) {
            ((TextView) (view.findViewById(R.id.emptyText))).setText("Žádné připojení");
        }
        else {
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
        }
        return view;
    }

    public boolean hasInternetConnection()
    {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        return (network != null && network.isConnected());
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
            mDownloadingTask = new DownloadingTask();
            Toast.makeText(getActivity().getApplicationContext(), R.string.dataDownloading, Toast.LENGTH_SHORT).show();
            mDownloadingTask.execute();
        }
        else {
            updateViewAdapter();
        }
    }

    private void updateViewAdapter() {
        MainFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerViewAdapter.dataUpdate(((ArrayList<Film>)mData.get(mCategory)));
            }
        });
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            if(BuildConfig.logging)
                Log.d(TAG, "doInBackground - thread: " + Thread.currentThread().getName());
            try {
                mData.put(mCategory, new ArrayList<Film>());
                switch (mCategory) {
                    case 0:
                        ((ArrayList<Film>)mData.get(mCategory)).addAll(Networking.getLatestFilms());
                        break;
                    case 1:
                        ((ArrayList<Film>)mData.get(mCategory)).addAll(Networking.getPopularFilms());
                        break;
                }
                updateViewAdapter();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(BuildConfig.logging)
                Log.d(TAG, "onPostExecute - thread: " + Thread.currentThread().getName());
            if (result) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.dataSuccess, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(), R.string.dataError, Toast.LENGTH_SHORT).show();
            }

            if (((ArrayList<Film>)mData.get(mCategory)).isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
            mDownloadingTask = null;
        }

        @Override
        protected void onCancelled() {
            if(BuildConfig.logging)
                Log.d(TAG, "onCancelled - thread: " + Thread.currentThread().getName());
        }
    }
}