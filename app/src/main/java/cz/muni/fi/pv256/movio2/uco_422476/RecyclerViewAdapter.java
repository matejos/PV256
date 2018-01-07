package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Matej on 3.11.2017.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mAppContext;
    private ArrayList<Object> mDataList;
    private MainFragment mMainFragment;
    private final static int CATEGORY = 0;
    private final static int FILM = 1;

    public RecyclerViewAdapter(ArrayList<Object> dataList, Context context, MainFragment mainFragment) {
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(mDataList.get(position) instanceof Film) {
            Film film = (Film)mDataList.get(position);
            Log.d("Binding", "Binding " + film.getTitle());
            FilmViewHolder filmHolder = (FilmViewHolder) holder;
            filmHolder.text.setText(film.getTitle());
            filmHolder.popularity.setText(String.valueOf(film.getPopularity()));

            int coverId = mAppContext.getResources().getIdentifier(film.getBackdrop(), "drawable", mAppContext.getPackageName());
            Drawable cover = mAppContext.getResources().getDrawable(coverId);
            filmHolder.coverIv.setImageDrawable(cover);

            filmHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMainFragment.clickedFilm(position);
                }
            });

            Palette palette = Palette.generate(BitmapFactory.decodeResource(mAppContext.getResources(), coverId));
            int backgroundColorOpaque = palette.getDarkVibrantColor(0x000000);
            int backgroundColorTransparent = Color.argb(128, Color.red(backgroundColorOpaque), Color.green(backgroundColorOpaque), Color.blue(backgroundColorOpaque));
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.TRANSPARENT, backgroundColorOpaque});
            filmHolder.text.setBackgroundColor(backgroundColorTransparent);
            filmHolder.star.setBackground(gd);
            filmHolder.popularity.setBackgroundColor(backgroundColorOpaque);

            filmHolder.itemView.setTag(film);
        }
        else {
            CategoryViewHolder categoryHolder = (CategoryViewHolder) holder;
            categoryHolder.text.setText((String) mDataList.get(position));
        }
    }

    static class FilmViewHolder extends RecyclerView.ViewHolder {
        private ImageView coverIv;
        public TextView text;
        public ImageView star;
        public TextView popularity;

        public FilmViewHolder(View view) {
            super(view);
            text = (TextView) itemView.findViewById(R.id.list_item_text);
            coverIv = (ImageView) view.findViewById(R.id.list_item_icon);
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

