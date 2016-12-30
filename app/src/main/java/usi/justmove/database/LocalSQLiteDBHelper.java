package usi.justmove.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by usi on 29/12/16.
 */

public class LocalSQLiteDBHelper extends SQLiteOpenHelper {
    //db information
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "JustMove";

    public LocalSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GPSLocationTable.getCreateQuery());
        db.execSQL(AccelerometerTable.getCreateQuery());
        db.execSQL(BlueToothTable.getCreateQuery());
        db.execSQL(PhoneLockTable.getCreateQuery());
        db.execSQL(WiFiTable.getCreateQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        return;
    }
}
