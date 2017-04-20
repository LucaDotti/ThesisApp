package usi.memotion.local.database.tables;

/**
 * Created by Luca Dotti on 29/12/16.
 */
public class SMSTable {
    public static final String TABLE_SMS = "sms";
    public static final String KEY_SMS_ID = "id_sms";
    public static final String KEY_SMS_TS = "ts";
    public static final String KEY_SMS_DIRECTION = "direction";
    public static final String KEY_SMS_SENDER_NUMBER = "sender_number";
    public static final String KEY_SMS_RECEIVER_NUMBER = "receiver_number";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SMS +
                "(" +
                KEY_SMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_SMS_TS + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_SMS_DIRECTION + " TEXT, " +
                KEY_SMS_RECEIVER_NUMBER + " TEXT, " +
                KEY_SMS_SENDER_NUMBER + " TEXT)";
    }

    public static String[] getColumns() {
        String[] columns = {KEY_SMS_ID, KEY_SMS_TS, KEY_SMS_DIRECTION, KEY_SMS_RECEIVER_NUMBER, KEY_SMS_SENDER_NUMBER};
        return columns;
    }
}
