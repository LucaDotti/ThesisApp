package usi.justmove.database.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import usi.justmove.database.base.DbController;
import usi.justmove.database.LocalSQLiteDBHelper;

/**
 * Created by usi on 03/01/17.
 */

public class LocalDbController implements DbController {
    private String dbName;
    private Context context;
    private SQLiteDatabase localDb;
    private LocalSQLiteDBHelper dbHelper;

    public LocalDbController(Context context, String dbName) {
        this.context = context;
        this.dbName = dbName;
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
}
