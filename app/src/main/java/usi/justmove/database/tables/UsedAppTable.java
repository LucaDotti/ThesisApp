package usi.justmove.database.tables;

/**
 * Created by Luca Dotti on 29/12/16.
 */
public class UsedAppTable {
    public static final String TABLE_USED_APP = "used_app";
    public static final String KEY_USED_APP_ID = "id_used_app";
    public static final String KEY_USED_APP_TIMESTAMP = "ts";
    public static final String KEY_USED_APP_TYPE = "type_id";
    public static final String KEY_USED_APP_NAME = "name";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_USED_APP +
                "(" +
                KEY_USED_APP_ID + " INTEGER PRIMARY KEY," +
                KEY_USED_APP_TIMESTAMP + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_USED_APP_TYPE + " INTEGER NOT NULL, " +
                KEY_USED_APP_NAME + " TEXT, " +
                "FOREIGN KEY (" + KEY_USED_APP_TYPE + ") REFERENCES " + UsedAppTypeTable.TABLE_USED_APP_TYPE + "(" + UsedAppTypeTable.KEY_USED_APP_TYPE_ID + ")" +
                ")";
    }
}
