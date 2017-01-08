package usi.justmove.database.tables;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luca Dotti on 03/01/17.
 */
public class LocationProviderTable {
    public static final String TABLE_LOCATION_PROVIDER = "location_provider";
    public static final String KEY_LOCATION_PROVIDER_ID = "id_location_provider";
    public static final String KEY_LOCATION_PROVIDER_PROVIDER = "provider";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION_PROVIDER +
                "(" +
                KEY_LOCATION_PROVIDER_ID + " INTEGER PRIMARY KEY," +
                KEY_LOCATION_PROVIDER_PROVIDER + " TEXT" +
                ")";
    }

    public static List<ContentValues> getRecords() {
        List<ContentValues> records = new ArrayList<>();
        ContentValues record = new ContentValues();

        record.put(KEY_LOCATION_PROVIDER_ID, 0);
        record.put(KEY_LOCATION_PROVIDER_PROVIDER, "gps");
        records.add(record);

        record = new ContentValues();
        record.put(KEY_LOCATION_PROVIDER_ID, 1);
        record.put(KEY_LOCATION_PROVIDER_PROVIDER, "wifi");
        records.add(record);

        record = new ContentValues();
        record.put(KEY_LOCATION_PROVIDER_ID, 2);
        record.put(KEY_LOCATION_PROVIDER_PROVIDER, "cell");
        records.add(record);

        return records;
    }
}
