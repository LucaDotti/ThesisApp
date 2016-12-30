package usi.justmove.database;

import static usi.justmove.database.PhoneLockTable.KEY_PHONELOCK_ID;
import static usi.justmove.database.PhoneLockTable.KEY_PHONELOCK_STATUS;
import static usi.justmove.database.PhoneLockTable.KEY_PHONELOCK_TIMESTAMP;
import static usi.justmove.database.PhoneLockTable.TABLE_PHONELOCK;

/**
 * Created by usi on 29/12/16.
 */

public class UsedAppTypeTable {
    public static final String TABLE_USED_APP_TYPE = "used_app_type";
    public static final String KEY_USED_APP_TYPE_ID = "id_used_app_type";
    public static final String KEY_USED_APP_TYPE_TYPE = "type";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_USED_APP_TYPE +
                "(" +
                KEY_USED_APP_TYPE_ID + " INTEGER PRIMARY KEY," +
                KEY_USED_APP_TYPE_TYPE + " TEXT" +
                ")";
    }
}
