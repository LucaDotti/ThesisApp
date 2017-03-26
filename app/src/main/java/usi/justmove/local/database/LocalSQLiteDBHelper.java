package usi.justmove.local.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import usi.justmove.local.database.tables.AccelerometerTable;
import usi.justmove.local.database.tables.BlueToothTable;
import usi.justmove.local.database.tables.PAMTable;
import usi.justmove.local.database.tables.PHQ8Table;
import usi.justmove.local.database.tables.PSSTable;
import usi.justmove.local.database.tables.PWBTable;
import usi.justmove.local.database.tables.PhoneCallLogTable;
import usi.justmove.local.database.tables.LocationTable;
import usi.justmove.local.database.tables.PhoneLockTable;
import usi.justmove.local.database.tables.SHSTable;
import usi.justmove.local.database.tables.SMSTable;
import usi.justmove.local.database.tables.SWLSTable;
import usi.justmove.local.database.tables.SimpleMoodTable;
import usi.justmove.local.database.tables.SurveyTable;
import usi.justmove.local.database.tables.UploaderUtilityTable;
import usi.justmove.local.database.tables.UsedAppTable;
import usi.justmove.local.database.tables.UserTable;
import usi.justmove.local.database.tables.WiFiTable;

/**
 * Created by usi on 29/12/16.
 */

public class LocalSQLiteDBHelper extends SQLiteOpenHelper {
    //db information
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "JustMove";

    private SQLiteDatabase db;

    public LocalSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create actual data table
        db.execSQL(LocationTable.getCreateQuery());
        db.execSQL(AccelerometerTable.getCreateQuery());
        db.execSQL(PhoneCallLogTable.getCreateQuery());
        db.execSQL(SMSTable.getCreateQuery());
        db.execSQL(UsedAppTable.getCreateQuery());
        db.execSQL(BlueToothTable.getCreateQuery());
        db.execSQL(PhoneLockTable.getCreateQuery());
        db.execSQL(WiFiTable.getCreateQuery());
        db.execSQL(UploaderUtilityTable.getCreateQuery());
        db.execSQL(UserTable.getCreateQuery());
        db.execSQL(SimpleMoodTable.getCreateQuery());
        db.execSQL(PAMTable.getCreateQuery());
        db.execSQL(PWBTable.getCreateQuery());
        db.execSQL(SHSTable.getCreateQuery());
        db.execSQL(SWLSTable.getCreateQuery());
        db.execSQL(PHQ8Table.getCreateQuery());
        db.execSQL(PSSTable.getCreateQuery());
        db.execSQL(SurveyTable.getCreateQuery());

        //insert init data to uploader_utility table
        insertRecords(db, UploaderUtilityTable.TABLE_UPLOADER_UTILITY, UploaderUtilityTable.getRecords());
//        insertRecords(db, SimpleMoodTable.TABLE_SIMPLE_MOOD, SimpleMoodTable.getRecords());

        Log.d("DATABASE HELPER", "Db created");
    }

    /**
     * Utility function to insert the given records in the given table.
     *
     * @param db
     * @param tableName
     * @param records
     */
    private void insertRecords(SQLiteDatabase db, String tableName, List<ContentValues> records) {
        for(ContentValues record: records) {
            db.insert(tableName, null, record);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        return;
    }
}
