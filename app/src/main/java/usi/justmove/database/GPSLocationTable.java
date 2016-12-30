package usi.justmove.database;


/**
 * Created by usi on 29/12/16.
 */

public class GPSLocationTable {
    public static final String TABLE_GPS = "gps";
    public static final String KEY_GPS_ID = "id_gps";
    public static final String KEY_GPS_TIMESTAMP = "ts";
    public static final String KEY_GPS_PROVIDER = "provider";
    public static final String KEY_GPS_LATITUDE = "latitude";
    public static final String KEY_GPS_LONGITUDE = "longitude";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_GPS +
                "(" +
                KEY_GPS_ID + " INTEGER PRIMARY KEY," +
                KEY_GPS_TIMESTAMP + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_GPS_PROVIDER + " TEXT, " +
                KEY_GPS_LATITUDE + " REAL NOT NULL, " +
                KEY_GPS_LONGITUDE + " REAL NOT NULL" +
                ")";
    }
}
