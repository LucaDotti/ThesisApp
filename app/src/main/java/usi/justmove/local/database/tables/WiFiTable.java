package usi.justmove.local.database.tables;

/**
 * Created by Luca Dotti on 29/12/16.
 */
public class WiFiTable {
    public static final String TABLE_WIFI = "wifi";
    public static final String KEY_WIFI_ID = "id_wifi";
    public static final String KEY_WIFI_TIMESTAMP = "ts";
    public static final String KEY_WIFI_SSID = "ssid";
    public static final String KEY_WIFI_FREQ = "freq";
    public static final String KEY_WIFI_LEVEL = "level";

    public static String getCreateQuery() {
        return "CREATE TABLE " + TABLE_WIFI +
                "(" +
                KEY_WIFI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_WIFI_TIMESTAMP + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_WIFI_SSID + " TEXT NOT NULL, " +
                KEY_WIFI_FREQ + " INTEGER, " +
                KEY_WIFI_LEVEL + " INTEGER" +
                ")";
    }

    public static String[] getColumns() {
        String[] columns = {KEY_WIFI_ID, KEY_WIFI_TIMESTAMP, KEY_WIFI_SSID, KEY_WIFI_FREQ, KEY_WIFI_LEVEL};
        return columns;
    }
}
