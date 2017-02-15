package usi.justmove.local.database.tables;

/**
 * Created by Luca Dotti on 29/12/16.
 */
public class UsedAppTable {
    public static final String TABLE_USED_APP = "used_app";
    public static final String KEY_USED_APP_ID = "id_used_app";
    public static final String KEY_USED_APP_TIMESTAMP = "ts";
    public static final String KEY_USED_APP_TYPE = "type";
    public static final String KEY_USED_APP_NAME = "name";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_USED_APP +
                "(" +
                KEY_USED_APP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_USED_APP_TIMESTAMP + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_USED_APP_TYPE + " TEXT, " +
                KEY_USED_APP_NAME + " TEXT)";
    }

    public static String[] getColumns() {
        String[] columns = {KEY_USED_APP_ID, KEY_USED_APP_TIMESTAMP, KEY_USED_APP_TYPE, KEY_USED_APP_NAME};
        return columns;
    }
}
