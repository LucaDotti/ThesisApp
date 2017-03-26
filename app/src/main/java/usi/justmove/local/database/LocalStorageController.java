package usi.justmove.local.database;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;
import java.util.Map;

/**
 * Created by usi on 03/01/17.
 */

public interface LocalStorageController {
    Cursor rawQuery(String query, String[] args);
    long insertRecord(String tableName, ContentValues record);
    long getDbSize();
    void delete(String tableName, String clause);
    void update(String tableName, ContentValues values, String clause);
}
