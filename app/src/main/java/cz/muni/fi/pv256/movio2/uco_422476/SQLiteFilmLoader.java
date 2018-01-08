package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.Context;

import java.util.List;

/**
 * Created by Matej on 5.1.2018.
 */

public class SQLiteFilmLoader extends AbstractDataLoader<List<Film>> {
    private FilmManager mFilmManager;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mGroupBy;
    private String mHaving;
    private String mOrderBy;

    public SQLiteFilmLoader(Context context, FilmManager filmManager, String selection, String[] selectionArgs,
                            String groupBy, String having, String orderBy) {
        super(context);
        mFilmManager = filmManager;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mOrderBy = orderBy;
    }

    @Override
    protected List<Film> buildList() {
        return mFilmManager.getFavouriteFilms();
    }

    public void create(Film film) {
        new InsertTask(this).execute(film);
    }

    public void delete(Film film) {
        new DeleteTask(this).execute(film);
    }

    private class InsertTask extends ContentChangingTask<Film, Void, Void> {
        InsertTask(SQLiteFilmLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Film... params) {
            mFilmManager.createFilm(params[0]);
            return (null);
        }
    }

    private class DeleteTask extends ContentChangingTask<Film, Void, Void> {
        DeleteTask(SQLiteFilmLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Film... params) {
            mFilmManager.deleteFilm(params[0]);
            return (null);
        }
    }
}
