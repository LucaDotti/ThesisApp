package usi.justmove.database.base;

import android.database.Cursor;

import java.util.List;
import java.util.Map;

/**
 * Created by usi on 03/01/17.
 */

public interface DbController {
    Cursor rawQuery(String query, String[] args);
    void insertRecords(String tableName, List<Map<String, String>> records);
}
