package usi.memotion.local.database.tables;


import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by usi on 05/02/17.
 */

public class SimpleMoodTable {
    public static final String TABLE_SIMPLE_MOOD = "simple_mood";
    public static final String KEY_SIMPLE_MOOD_ID = "id_simple_mood";
    public static final String KEY_SIMPLE_MOOD_TIMESTAMP = "ts";
    public static final String KEY_SIMPLE_MOOD_STATUS = "status";

    public static String getCreateQuery() {
        return "CREATE TABLE " + TABLE_SIMPLE_MOOD +
                "(" +
                KEY_SIMPLE_MOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_SIMPLE_MOOD_TIMESTAMP + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_SIMPLE_MOOD_STATUS + " INTEGER" +
                ")";
    }

    public static String[] getColumns() {
        String[] columns = {KEY_SIMPLE_MOOD_ID, KEY_SIMPLE_MOOD_TIMESTAMP, KEY_SIMPLE_MOOD_STATUS};
        return columns;
    }

    public static List<ContentValues> getRecords() {
        List<ContentValues> records = new ArrayList<>();
        ContentValues record;
        long now = System.currentTimeMillis() + 86400000;
        Random r = new Random();
        for(int i = 0; i < 20; i++) {
            now += 86400000;
            record = new ContentValues();
            record.put(KEY_SIMPLE_MOOD_ID, (String) null);
            record.put(KEY_SIMPLE_MOOD_TIMESTAMP, now);
            record.put(KEY_SIMPLE_MOOD_STATUS, r.nextInt(7));
            records.add(record);
        }
        return records;
    }

}
