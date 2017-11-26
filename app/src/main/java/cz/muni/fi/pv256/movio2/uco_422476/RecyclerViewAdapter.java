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
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private Context mAppContext;
    private ArrayList<Film> mFilmList;
    private MainFragment mMainFragment;

    public RecyclerViewAdapter(ArrayList<Film> filmList, Context context, MainFragment mainFragment) {
        mFilmList = filmList;
        mMainFragment = mainFragment;
        mAppContext = context.getApplicationContext();
    }

    @Override
    public int getItemCount() {
        return mFilmList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mAppContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_film, parent, false);
        TextView text = (TextView) view.findViewById(R.id.list_item_text);
        if(BuildConfig.logging)
            Log.d("Inflating", "Inflating " + parent.getId());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Film film = mFilmList.get(position);
        if(BuildConfig.logging)
            Log.d("Binding", "Binding " + film.getTitle());
        holder.text.setText(film.getTitle());
        holder.popularity.setText(String.valueOf(film.getPopularity()));

        int coverId = mAppContext.getResources().getIdentifier(film.getBackdrop(), "drawable", mAppContext.getPackageName());
        Drawable cover = mAppContext.getResources().getDrawable(coverId);
        holder.coverIv.setImageDrawable(cover);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainFragment.clickedFilm(position);
            }
        });

        Palette palette = Palette.generate(BitmapFactory.decodeResource(mAppContext.getResources(), coverId));
        int backgroundColorOpaque = palette.getDarkVibrantColor(0x000000);
        int backgroundColorTransparent = Color.argb(128, Color.red(backgroundColorOpaque), Color.green(backgroundColorOpaque), Color.blue(backgroundColorOpaque));
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {Color.TRANSPARENT, backgroundColorOpaque});
        holder.text.setBackgroundColor(backgroundColorTransparent);
        holder.star.setBackground(gd);
        holder.popularity.setBackgroundColor(backgroundColorOpaque);

        holder.itemView.setTag(film);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView coverIv;
        public TextView text;
        public ImageView star;
        public TextView popularity;

        public ViewHolder(View view) {
            super(view);
            text = (TextView) itemView.findViewById(R.id.list_item_text);
            coverIv = (ImageView) view.findViewById(R.id.list_item_icon);
            star = (ImageView) itemView.findViewById(R.id.list_item_star);
            popularity = (TextView) itemView.findViewById(R.id.list_item_popularity);
        }
    }
}

