package usi.justmove.database.tables;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usi on 03/01/17.
 */
public class PhoneLockStatusTable {
    public static final String TABLE_PHONE_LOCK_STATUS = "lock_status";
    public static final String KEY_PHONE_LOCK_STATUS_ID = "id_lock_status";
    public static final String KEY_PHONE_LOCK_STATUS_STATUS = "status";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_PHONE_LOCK_STATUS +
                "(" +
                KEY_PHONE_LOCK_STATUS_ID + " INTEGER PRIMARY KEY," +
                KEY_PHONE_LOCK_STATUS_STATUS + " TEXT" +
                ")";
    }

    public static List<ContentValues> getRecords() {
        List<ContentValues> records = new ArrayList<>();
        ContentValues record = new ContentValues();

        record.put(KEY_PHONE_LOCK_STATUS_ID, 0);
        record.put(KEY_PHONE_LOCK_STATUS_STATUS, "lock");
        records.add(record);

        record.put(KEY_PHONE_LOCK_STATUS_ID, 1);
        record.put(KEY_PHONE_LOCK_STATUS_STATUS, "unlock");
        records.add(record);

        return records;
    }
}
