package usi.justmove.database;

/**
 * Created by usi on 29/12/16.
 */
public class BlueToothTable {
    public static final String TABLE_BLUETOOTH = "bluetooth";
    public static final String KEY_BLUETOOTH_ID = "id_bluetooth";
    public static final String KEY_BLUETOOTH_TIMESTAMP = "ts";
    public static final String KEY_BLUETOOTH_MAC = "mac";
    public static final String KEY_BLUETOOTH_LEVEL = "level";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_BLUETOOTH +
                "(" +
                KEY_BLUETOOTH_ID + " INTEGER PRIMARY KEY," +
                KEY_BLUETOOTH_TIMESTAMP + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_BLUETOOTH_MAC + " TEXT, " +
                KEY_BLUETOOTH_LEVEL + " INTEGER" +
                ")";
    }
}
