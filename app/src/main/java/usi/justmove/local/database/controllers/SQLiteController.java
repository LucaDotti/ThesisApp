package usi.justmove.local.database.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.List;
import java.util.Map;

import usi.justmove.R;
import usi.justmove.gathering.surveys.inspection.PAMSurveyInspector;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.LocalSQLiteDBHelper;

/**
 * Created by usi on 03/01/17.
 */

public class SQLiteController implements LocalStorageController {
    private Context context;
    private String dbName;
    private SQLiteDatabase localDb;
    private LocalSQLiteDBHelper dbHelper;
    private static SQLiteController object;


    private SQLiteController(Context context) {
        this.context = context;
        initDatabse();

        this.dbName = context.getString(R.string.dbName);
    }

    public static SQLiteController getInstance(Context context) {
        if(object == null) {
            object = new SQLiteController(context);
//            object.initDatabse();
        }
        return object;
    }

    public void initDatabse() {
        dbHelper = new LocalSQLiteDBHelper(context);
        localDb = dbHelper.getReadableDatabase();
    }

    @Override
    public Cursor rawQuery(String query, String[] args) {
        Cursor cursor = localDb.rawQuery(query, args);
        return cursor;
    }

    @Override
    public void insertRecords(String tableName, List<Map<String, String>> records) {
        ContentValues values;

        for(Map<String, String> record: records) {
            values = new ContentValues();
            for(Map.Entry<String, String> entry: record.entrySet()) {
                values.put(entry.getKey(), entry.getValue());
            }
            localDb.insert(tableName, null, values);
        }
    }

    @Override
    public long getDbSize() {
        File f = context.getDatabasePath(dbName);
        return f.length();
    }

    @Override
    public void delete(String tableName, String clause) {
        localDb.delete(tableName, clause, null);
    }

    @Override
    public void update(String tableName, ContentValues values, String clause) {
        localDb.update(tableName, values, clause, null);
    }
}
