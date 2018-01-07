package cz.muni.fi.pv256.movio2.uco_422476;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Matej on 5.1.2018.
 */

public class FilmDbHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "films.db";
    private static final int DATABASE_VERSION = 1;

    public FilmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + FilmContract.FilmEntry.TABLE_NAME + " (" +
                FilmContract.FilmEntry._ID + " INTEGER PRIMARY KEY," +
                FilmContract.FilmEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FilmContract.FilmEntry.COLUMN_COVER_PATH + " TEXT NOT NULL, " +
                FilmContract.FilmEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FilmContract.FilmEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                FilmContract.FilmEntry.COLUMN_POPULARITY + " FLOAT NOT NULL, " +
                FilmContract.FilmEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL" +
                " );";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FilmContract.FilmEntry.TABLE_NAME);
        onCreate(db);
    }
}
