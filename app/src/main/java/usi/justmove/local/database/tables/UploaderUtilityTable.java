package usi.justmove.local.database.tables;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

import usi.justmove.local.database.LocalDbUtility;
import usi.justmove.local.database.LocalTables;

/**
 * Created by usi on 29/01/17.
 */

public class UploaderUtilityTable {
    public static final String TABLE_UPLOADER_UTILITY = "uploader_utility";
    public static final String KEY_UPLOADER_UTILITY_ID = "id_uploader_utility";
    public static final String KEY_UPLOADER_UTILITY_TABLE = "target_table";
    public static final String KEY_UPLOADER_UTILITY_DATE = "date";
    public static final String KEY_UPLOADER_UTILITY_RECORD_ID = "record_id";
    public static final String KEY_UPLOADER_UTILITY_FILE_PART = "file_part";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_UPLOADER_UTILITY +
                "(" +
                KEY_UPLOADER_UTILITY_ID + " INTEGER PRIMARY KEY, " +
                KEY_UPLOADER_UTILITY_TABLE + " TEXT, " +
                KEY_UPLOADER_UTILITY_DATE + " TEXT, " +
                KEY_UPLOADER_UTILITY_RECORD_ID + " INTEGER, " +
                KEY_UPLOADER_UTILITY_FILE_PART + " INTEGER)";
    }

    public static String[] getColumns() {
        String[] columns = {KEY_UPLOADER_UTILITY_ID, KEY_UPLOADER_UTILITY_TABLE, KEY_UPLOADER_UTILITY_DATE, KEY_UPLOADER_UTILITY_RECORD_ID, KEY_UPLOADER_UTILITY_FILE_PART};
        return columns;
    }

    public static List<ContentValues> getRecords() {
        List<ContentValues> records = new ArrayList<>();
        ContentValues record;
        for(int i = 0; i < LocalTables.values().length; i++) {
            record = new ContentValues();
            record.put(KEY_UPLOADER_UTILITY_ID, i);
            record.put(KEY_UPLOADER_UTILITY_TABLE, LocalDbUtility.getTableName(LocalTables.values()[i]));
            record.put(KEY_UPLOADER_UTILITY_DATE, "");
            record.put(KEY_UPLOADER_UTILITY_RECORD_ID, 0);
            record.put(KEY_UPLOADER_UTILITY_FILE_PART, 0);
            records.add(record);
        }
        return records;
    }
}
