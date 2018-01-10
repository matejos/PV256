package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matej on 5.1.2018.
 */

public class FilmManager {
    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_RELEASE_DATE = 1;
    public static final int COL_MOVIE_COVER_PATH = 2;
    public static final int COL_MOVIE_TITLE = 3;
    public static final int COL_MOVIE_BACKDROP_PATH = 4;
    public static final int COL_MOVIE_POPULARITY = 5;
    public static final int COL_MOVIE_DESCRIPTION = 6;

    SQLiteDatabase mDatabase;

    private static final String[] FILM_COLUMNS = {
            FilmContract.FilmEntry._ID,
            FilmContract.FilmEntry.COLUMN_RELEASE_DATE,
            FilmContract.FilmEntry.COLUMN_COVER_PATH,
            FilmContract.FilmEntry.COLUMN_TITLE,
            FilmContract.FilmEntry.COLUMN_BACKDROP_PATH,
            FilmContract.FilmEntry.COLUMN_POPULARITY,
            FilmContract.FilmEntry.COLUMN_DESCRIPTION,
    };

    public FilmManager(SQLiteDatabase database) {
        mDatabase = database;
    }

    public boolean createFilm(Film film) {
        if (film == null) {
            throw new NullPointerException("Cannot create film: film is null");
        }
        if (film.getCoverPath() == null) {
            throw new IllegalStateException("Cannot create film: cover path is null");
        }
        if (film.getTitle() == null) {
            throw new IllegalStateException("Cannot create film: title is null");
        }
        if (film.getBackdrop() == null) {
            throw new IllegalStateException("Cannot create film: backdrop path is null");
        }
        if (film.getDescription() == null) {
            throw new IllegalStateException("Cannot create film: description is null");
        }
        long result = mDatabase.insert(FilmContract.FilmEntry.TABLE_NAME, null, prepareFilmValues(film));
        return result != -1;
    }

    public List<Film> getFavouriteFilms() {
        Cursor cursor = mDatabase.query(FilmContract.FilmEntry.TABLE_NAME, FILM_COLUMNS, null,
                null, null, null, null);
        List<Film> films = new ArrayList();
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                films.add(getFilm(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return films;
    }

    private Film getFilm(Cursor cursor) {
        return new Film(
                cursor.getLong(COL_MOVIE_ID),
                FilmContract.getDateFromDb(cursor.getString(COL_MOVIE_RELEASE_DATE)),
                cursor.getString(COL_MOVIE_COVER_PATH),
                cursor.getString(COL_MOVIE_TITLE),
                cursor.getString(COL_MOVIE_BACKDROP_PATH),
                cursor.getFloat(COL_MOVIE_POPULARITY),
                cursor.getString(COL_MOVIE_DESCRIPTION)
        );
    }

    public boolean deleteFilm(Film film) {
        if (film == null) {
            return false;
        }
        if (film.getId() == null) {
            throw new IllegalStateException("Cannot delete film: ID is null");
        }

        int result = mDatabase.delete(FilmContract.FilmEntry.TABLE_NAME, FilmContract.FilmEntry._ID + " = " + film.getId(), null);
        return result != 0;
    }

    public boolean containsId(Long id) {
        String Query = "Select * from " + FilmContract.FilmEntry.TABLE_NAME + " where " + FilmContract.FilmEntry._ID + " = " + id;
        Cursor cursor = mDatabase.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private ContentValues prepareFilmValues(Film film) {
        ContentValues values = new ContentValues();
        values.put(FilmContract.FilmEntry._ID, film.getId());
        values.put(FilmContract.FilmEntry.COLUMN_RELEASE_DATE, FilmContract.insertDateToDb(film.getReleaseDate()));
        values.put(FilmContract.FilmEntry.COLUMN_COVER_PATH, film.getCoverPath());
        values.put(FilmContract.FilmEntry.COLUMN_TITLE, film.getTitle());
        values.put(FilmContract.FilmEntry.COLUMN_BACKDROP_PATH, film.getBackdrop());
        values.put(FilmContract.FilmEntry.COLUMN_POPULARITY, film.getPopularity());
        values.put(FilmContract.FilmEntry.COLUMN_DESCRIPTION, film.getDescription());
        return values;
    }
}
