package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static cz.muni.fi.pv256.movio2.uco_422476.App.BACKDROP_URL;

/**
 * Created by Matej on 3.11.2017.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mAppContext;
    private ArrayList<ListItem> mDataList;
    private MainFragment mMainFragment;
    private final static int CATEGORY = 0;
    private final static int FILM = 1;

    private static final String BACKDROP_URL = "https://image.tmdb.org/t/p/w500/%s";

    public RecyclerViewAdapter(ArrayList<ListItem> dataList, Context context, MainFragment mainFragment) {
        mDataList = dataList;
        mMainFragment = mainFragment;
        mAppContext = context.getApplicationContext();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position) instanceof Film ? FILM : CATEGORY;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mAppContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if(BuildConfig.logging)
            Log.d("Inflating", "Inflating " + parent.getId());
        if (viewType == FILM){
            view = inflater.inflate(R.layout.list_item_film, parent, false);
            return new FilmViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.list_item_category, parent, false);
            return new CategoryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(mDataList.get(position) instanceof Film) {
            Film film = (Film)mDataList.get(position);
            final FilmViewHolder filmHolder = (FilmViewHolder) holder;
            if (BuildConfig.logging)
                Log.d("Binding", "Binding " + film.getTitle());
            filmHolder.text.setText(film.getTitle());
            filmHolder.popularity.setText(String.valueOf(film.getPopularity()));
            Picasso.with(mAppContext).load(String.format(BACKDROP_URL, film.getBackdrop()))
                    .into(filmHolder.backdropIv, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Palette palette = Palette.generate(((BitmapDrawable) filmHolder.backdropIv.getDrawable()).getBitmap());
                    int backgroundColorOpaque = palette.getDarkVibrantColor(0x000000);
                    int backgroundColorTransparent = Color.argb(128, Color.red(backgroundColorOpaque), Color.green(backgroundColorOpaque), Color.blue(backgroundColorOpaque));
                    GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.TRANSPARENT, backgroundColorOpaque});
                    filmHolder.text.setBackgroundColor(backgroundColorTransparent);
                    filmHolder.star.setBackground(gd);
                    filmHolder.popularity.setBackgroundColor(backgroundColorOpaque);
                }

                @Override
                public void onError() {
                    if (BuildConfig.logging)
                        Log.e("Loading image failed", "Error loading backdrop image");
                }
            });

            filmHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMainFragment.clickedFilm(position);
                }
            });

            filmHolder.itemView.setTag(film);
        }
        else {
            CategoryViewHolder categoryHolder = (CategoryViewHolder) holder;
            categoryHolder.text.setText(((Category) mDataList.get(position)).getName());
        }
    }

    public void dataUpdate(ArrayList<ListItem> data) {
        this.mDataList = data;
        notifyDataSetChanged();
    }

    static class FilmViewHolder extends RecyclerView.ViewHolder {
        private ImageView coverIv;
        private ImageView backdropIv;
        public TextView text;
        public ImageView star;
        public TextView popularity;

        public FilmViewHolder(View view) {
            super(view);
            text = (TextView) itemView.findViewById(R.id.list_item_text);
            backdropIv = (ImageView) view.findViewById(R.id.list_item_icon);
            star = (ImageView) itemView.findViewById(R.id.list_item_star);
            popularity = (TextView) itemView.findViewById(R.id.list_item_popularity);
        }
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView text;

        public CategoryViewHolder(View view) {
            super(view);
            text = (TextView) itemView.findViewById(R.id.list_item_category);
        }
    }
}

