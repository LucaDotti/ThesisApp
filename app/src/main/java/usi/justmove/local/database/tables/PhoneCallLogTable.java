package usi.justmove.local.database.tables;

/**
 * Created by Luca Dotti on 29/12/16.
 */
public class PhoneCallLogTable {
    public static final String TABLE_CALL_LOG = "call_log";
    public static final String KEY_CALL_LOG_ID = "id_call_log";
    public static final String KEY_CALL_LOG_TS = "ts";
    public static final String KEY_CALL_LOG_DIRECTION = "direction";
    public static final String KEY_CALL_LOG_SENDER_NUMBER = "sender_number";
    public static final String KEY_CALL_LOG_RECEIVER_NUMBER = "receiver_number";
    public static final String KEY_CALL_LOG_DURATION = "duration";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_CALL_LOG +
                "(" +
                KEY_CALL_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_CALL_LOG_TS + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_CALL_LOG_DIRECTION + " TEXT, " +
                KEY_CALL_LOG_RECEIVER_NUMBER + " TEXT, " +
                KEY_CALL_LOG_SENDER_NUMBER + " TEXT, " +
                KEY_CALL_LOG_DURATION + " INTEGER)";
    }
    public static String[] getColumns() {
        String[] columns = {KEY_CALL_LOG_ID, KEY_CALL_LOG_TS, KEY_CALL_LOG_DIRECTION, KEY_CALL_LOG_RECEIVER_NUMBER, KEY_CALL_LOG_SENDER_NUMBER, KEY_CALL_LOG_DURATION};
        return columns;
    }
}
