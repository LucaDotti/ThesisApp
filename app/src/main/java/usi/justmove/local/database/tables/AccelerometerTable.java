package usi.justmove.local.database.tables;

/**
 * Created by Luca Dotti on 29/12/16.
 */
public class AccelerometerTable {
    public static final String TABLE_ACCELEROMETER = "accelerometer";
    public static final String KEY_ACCELEROMETER_ID = "id_accelerometer";
    public static final String KEY_ACCELEROMETER_TIMESTAMP = "ts";
    public static final String KEY_ACCELEROMETER_X = "x";
    public static final String KEY_ACCELEROMETER_Y = "y";
    public static final String KEY_ACCELEROMETER_Z = "z";

    public static String getCreateQuery() {
        return "CREATE TABLE " + TABLE_ACCELEROMETER +
                "(" +
                KEY_ACCELEROMETER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_ACCELEROMETER_TIMESTAMP + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_ACCELEROMETER_X + " REAL NOT NULL, " +
                KEY_ACCELEROMETER_Y + " REAL NOT NULL, " +
                KEY_ACCELEROMETER_Z + " REAL NOT NULL" +
                ")";
    }

    public static String[] getColumns() {
        String[] columns = {KEY_ACCELEROMETER_ID, KEY_ACCELEROMETER_TIMESTAMP, KEY_ACCELEROMETER_X, KEY_ACCELEROMETER_Y, KEY_ACCELEROMETER_Z};
        return columns;
    }
}
