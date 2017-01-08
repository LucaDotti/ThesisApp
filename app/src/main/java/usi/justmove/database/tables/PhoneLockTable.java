package usi.justmove.database.tables;

/**
 * Created by Luca Dotti on 29/12/16.
 */
public class PhoneLockTable {
    public static final String TABLE_PHONELOCK = "phone_lock";
    public static final String KEY_PHONELOCK_ID = "id_phone_lock";
    public static final String KEY_PHONELOCK_TIMESTAMP = "ts";
    public static final String KEY_PHONELOCK_STATUS = "status_id";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_PHONELOCK +
                "(" +
                KEY_PHONELOCK_ID + " INTEGER PRIMARY KEY," +
                KEY_PHONELOCK_TIMESTAMP + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_PHONELOCK_STATUS + " INTEGER," +
                "FOREIGN KEY (" + KEY_PHONELOCK_STATUS + ") REFERENCES " + PhoneLockStatusTable.TABLE_PHONE_LOCK_STATUS + "(" + PhoneLockStatusTable.KEY_PHONE_LOCK_STATUS_ID + ")" +
                ")";
    }
}
