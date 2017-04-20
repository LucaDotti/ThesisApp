package usi.memotion.local.database.tables;

/**
 * Created by Luca Dotti on 29/12/16.
 */
public class LocationTable {
    public static final String TABLE_LOCATION = "gps";
    public static final String KEY_LOCATION_ID = "id_gps";
    public static final String KEY_LOCATION_TIMESTAMP = "ts";
    public static final String KEY_LOCATION_PROVIDER = "provider";
    public static final String KEY_LOCATION_LATITUDE = "latitude";
    public static final String KEY_LOCATION_LONGITUDE = "longitude";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION +
                "(" +
                KEY_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_LOCATION_TIMESTAMP + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_LOCATION_PROVIDER + " TEXT, " +
                KEY_LOCATION_LATITUDE + " REAL NOT NULL, " +
                KEY_LOCATION_LONGITUDE + " REAL NOT NULL)";

    }

    public static String[] getColumns() {
        String[] columns = {KEY_LOCATION_ID, KEY_LOCATION_TIMESTAMP, KEY_LOCATION_PROVIDER, KEY_LOCATION_LATITUDE, KEY_LOCATION_LONGITUDE};
        return columns;
    }
}
