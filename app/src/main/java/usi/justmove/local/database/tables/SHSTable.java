package usi.justmove.local.database.tables;

/**
 * Created by usi on 07/03/17.
 */
public class SHSTable {
    public static final String TABLE_SHS = "shs";
    public static final String KEY_SHS_ID = "id_shs";
    public static final String KEY_SHS_TS = "timestamp";
    public static final String KEY_SHS_SCHEDULED_AT = "scheduled_at";
    public static final String KEY_SHS_COMPLETED = "completed";
    public static final String KEY_SHS_NOTIFIED = "notified";
    public static final String KEY_SHS_EXPIRED = "expired";
    public static final String KEY_SHS_Q1 = "question1";
    public static final String KEY_SHS_Q2 = "question2";
    public static final String KEY_SHS_Q3 = "question3";
    public static final String KEY_SHS_Q4 = "question4";

    public static String getCreateQuery() {
        return "CREATE TABLE " + TABLE_SHS +
                "(" +
                KEY_SHS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_SHS_TS + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_SHS_SCHEDULED_AT + " INTEGER," +
                KEY_SHS_COMPLETED + " INTEGER, " +
                KEY_SHS_NOTIFIED + " INTEGER, " +
                KEY_SHS_EXPIRED + " INTEGER, " +
                KEY_SHS_Q1 + " INTEGER, " +
                KEY_SHS_Q2 + " INTEGER, " +
                KEY_SHS_Q3 + " INTEGER, " +
                KEY_SHS_Q4 + " INTEGER" +
                ")";
    }

    public static String[] getColumns() {
        String[] columns = {KEY_SHS_ID, KEY_SHS_TS, KEY_SHS_SCHEDULED_AT, KEY_SHS_COMPLETED, KEY_SHS_NOTIFIED, KEY_SHS_EXPIRED, KEY_SHS_Q1, KEY_SHS_Q2, KEY_SHS_Q3, KEY_SHS_Q4};
        return columns;
    }
}
