package usi.justmove.database.tables;

/**
 * Created by Luca Dotti on 29/12/16.
 */
public class PhoneCallLogTable {
    public static final String TABLE_CALL_LOG = "call_log";
    public static final String KEY_CALL_LOG_ID = "id_call_log";
    public static final String KEY_CALL_LOG_TS = "ts";
    public static final String KEY_CALL_LOG_DIRECTION = "direction_id";
    public static final String KEY_CALL_LOG_SENDER_NUMBER = "sender_number";
    public static final String KEY_CALL_LOG_RECEIVER_NUMBER = "receiver_number";
    public static final String KEY_CALL_LOG_DURATION = "duration";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_CALL_LOG +
                "(" +
                KEY_CALL_LOG_ID + " INTEGER PRIMARY KEY," +
                KEY_CALL_LOG_TS + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_CALL_LOG_DIRECTION + " INTEGER NOT NULL, " +
                KEY_CALL_LOG_RECEIVER_NUMBER + " TEXT, " +
                KEY_CALL_LOG_SENDER_NUMBER + " TEXT, " +
                KEY_CALL_LOG_DURATION + " INTEGER, " +
                "FOREIGN KEY (" + KEY_CALL_LOG_DIRECTION + ") REFERENCES " + CommunicationDirectionTable.TABLE_COMMUNICATION_DIRECTION + "(" + CommunicationDirectionTable.KEY_COMMUNICATION_DIRECTION_ID + ")" +
                ")";
    }
}
