package usi.justmove.database.tables;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luca Dotti on 29/12/16.
 */
public class UsedAppTypeTable {
    public static final String TABLE_USED_APP_TYPE = "used_app_type";
    public static final String KEY_USED_APP_TYPE_ID = "id_used_app_type";
    public static final String KEY_USED_APP_TYPE_TYPE = "type";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_USED_APP_TYPE +
                "(" +
                KEY_USED_APP_TYPE_ID + " INTEGER PRIMARY KEY," +
                KEY_USED_APP_TYPE_TYPE + " TEXT" +
                ")";
    }

    public static List<ContentValues> getRecords() {
        List<ContentValues> records = new ArrayList<>();
        ContentValues record = new ContentValues();

        record.put(KEY_USED_APP_TYPE_ID, 0);
        record.put(KEY_USED_APP_TYPE_TYPE, "unkown");
        records.add(record);


        return records;
    }
}
