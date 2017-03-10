package usi.justmove.local.database.tables;

/**
 * Created by usi on 07/03/17.
 */
public class PHQ8Table {
    public static final String TABLE_PHQ8 = "phq8";
    public static final String KEY_PHQ8_ID = "id_phq8";
    public static final String KEY_PHQ8_TS = "timestamp";
    public static final String KEY_PHQ8_SCHEDULED_AT = "scheduled_at";
    public static final String KEY_PHQ8_COMPLETED = "completed";
    public static final String KEY_PHQ8_NOTIFIED = "notified";
    public static final String KEY_PHQ8_EXPIRED = "expired";
    public static final String KEY_PHQ8_Q1 = "question1";
    public static final String KEY_PHQ8_Q2 = "question2";
    public static final String KEY_PHQ8_Q3 = "question3";
    public static final String KEY_PHQ8_Q4 = "question4";
    public static final String KEY_PHQ8_Q5 = "question5";
    public static final String KEY_PHQ8_Q6 = "question6";
    public static final String KEY_PHQ8_Q7 = "question7";
    public static final String KEY_PHQ8_Q8 = "question8";

    public static String getCreateQuery() {
        return "CREATE TABLE " + TABLE_PHQ8 +
                "(" +
                KEY_PHQ8_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_PHQ8_TS + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_PHQ8_SCHEDULED_AT + " INTEGER," +
                KEY_PHQ8_COMPLETED + " INTEGER, " +
                KEY_PHQ8_NOTIFIED + " INTEGER, " +
                KEY_PHQ8_EXPIRED + " INTEGER, " +
                KEY_PHQ8_Q1 + " INTEGER, " +
                KEY_PHQ8_Q2 + " INTEGER, " +
                KEY_PHQ8_Q3 + " INTEGER, " +
                KEY_PHQ8_Q4 + " INTEGER, " +
                KEY_PHQ8_Q5 + " INTEGER, " +
                KEY_PHQ8_Q6 + " INTEGER, " +
                KEY_PHQ8_Q7 + " INTEGER, " +
                KEY_PHQ8_Q8 + " INTEGER" +
                ")";
    }

    public static String[] getColumns() {
        String[] columns = {KEY_PHQ8_ID, KEY_PHQ8_TS, KEY_PHQ8_SCHEDULED_AT, KEY_PHQ8_COMPLETED, KEY_PHQ8_NOTIFIED, KEY_PHQ8_EXPIRED, KEY_PHQ8_Q1, KEY_PHQ8_Q2, KEY_PHQ8_Q3, KEY_PHQ8_Q4, KEY_PHQ8_Q5, KEY_PHQ8_Q6, KEY_PHQ8_Q7, KEY_PHQ8_Q8};
        return columns;
    }
}
