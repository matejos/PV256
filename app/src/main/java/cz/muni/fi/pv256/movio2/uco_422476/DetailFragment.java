package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Matej on 3.11.2017.
 */

public class DetailFragment extends Fragment {

    public static final String TAG = DetailFragment.class.getSimpleName();
    private static final String ARGS_FILM = "args_film";

    private Context mContext;
    private Film mFilm;

    public static DetailFragment newInstance(Film film) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGS_FILM, film);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        Bundle args = getArguments();
        if (args != null) {
            mFilm = args.getParcelable(ARGS_FILM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView titleTv = (TextView) view.findViewById(R.id.detail_film);
        TextView titleLowTv = (TextView) view.findViewById(R.id.detail_film_low);
        TextView descTv = (TextView) view.findViewById(R.id.detail_film_desc);
        ImageView coverIv = (ImageView) view.findViewById(R.id.detail_icon);
        if (mFilm != null) {
            titleTv.setText(mFilm.getTitle());
            java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
            String dateString = dateFormat.format(mFilm.getReleaseDate());
            titleLowTv.setText(dateString);
            descTv.setText(mFilm.getDescription());
            Picasso.with(mContext).load("https://image.tmdb.org/t/p/w342/" + mFilm.getCoverPath()).into(coverIv);
        }
        return view;
    }
}