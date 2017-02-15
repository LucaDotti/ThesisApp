package usi.justmove.local.database.tables;

/**
 * Created by Luca Dotti on 29/12/16.
 */
public class PhoneLockTable {
    public static final String TABLE_PHONELOCK = "phone_lock";
    public static final String KEY_PHONELOCK_ID = "id_phone_lock";
    public static final String KEY_PHONELOCK_TIMESTAMP = "ts";
    public static final String KEY_PHONELOCK_STATUS = "status";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_PHONELOCK +
                "(" +
                KEY_PHONELOCK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_PHONELOCK_TIMESTAMP + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_PHONELOCK_STATUS + " TEXT)";
    }

    public static String[] getColumns() {
        String[] columns = {KEY_PHONELOCK_ID, KEY_PHONELOCK_TIMESTAMP, KEY_PHONELOCK_STATUS};
        return columns;
    }
}
