package usi.justmove.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import usi.justmove.database.tables.AccelerometerTable;
import usi.justmove.database.tables.BlueToothTable;
import usi.justmove.database.tables.PhoneCallLogTable;
import usi.justmove.database.tables.CommunicationDirectionTable;
import usi.justmove.database.tables.LocationProviderTable;
import usi.justmove.database.tables.LocationTable;
import usi.justmove.database.tables.PhoneLockStatusTable;
import usi.justmove.database.tables.PhoneLockTable;
import usi.justmove.database.tables.SMSTable;
import usi.justmove.database.tables.UsedAppTable;
import usi.justmove.database.tables.UsedAppTypeTable;
import usi.justmove.database.tables.WiFiTable;

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
        //create foreign tables
        db.execSQL(CommunicationDirectionTable.getCreateQuery());
        db.execSQL(LocationProviderTable.getCreateQuery());
        db.execSQL(UsedAppTypeTable.getCreateQuery());
        db.execSQL(PhoneLockStatusTable.getCreateQuery());

        //create actual data table
        db.execSQL(LocationTable.getCreateQuery());
        db.execSQL(AccelerometerTable.getCreateQuery());
        db.execSQL(PhoneCallLogTable.getCreateQuery());
        db.execSQL(SMSTable.getCreateQuery());
        db.execSQL(UsedAppTable.getCreateQuery());
        db.execSQL(BlueToothTable.getCreateQuery());
        db.execSQL(PhoneLockTable.getCreateQuery());
        db.execSQL(WiFiTable.getCreateQuery());

        //init foreign tables
        insertRecords(db, CommunicationDirectionTable.TABLE_COMMUNICATION_DIRECTION, CommunicationDirectionTable.getRecords());
        insertRecords(db, LocationProviderTable.TABLE_LOCATION_PROVIDER, LocationProviderTable.getRecords());
        insertRecords(db, UsedAppTypeTable.TABLE_USED_APP_TYPE, UsedAppTypeTable.getRecords());
        insertRecords(db, PhoneLockStatusTable.TABLE_PHONE_LOCK_STATUS, PhoneLockStatusTable.getRecords());

        Log.d("DATABASE HELPER", "Db created");
    }

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
