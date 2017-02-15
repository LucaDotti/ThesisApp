package usi.justmove.local.database.tables;

import static usi.justmove.local.database.tables.SimpleMoodTable.KEY_SIMPLE_MOOD_ID;
import static usi.justmove.local.database.tables.SimpleMoodTable.KEY_SIMPLE_MOOD_STATUS;
import static usi.justmove.local.database.tables.SimpleMoodTable.KEY_SIMPLE_MOOD_TIMESTAMP;
import static usi.justmove.local.database.tables.SimpleMoodTable.TABLE_SIMPLE_MOOD;

/**
 * Created by usi on 07/02/17.
 */

public class PWBTable {
    public static final String TABLE_PWB = "pwb";
    public static final String KEY_PWB_ID = "id_pwb";
    public static final String KEY_PWB_SCHEDULED_AT = "scheduled_at";
    public static final String KEY_PWB_TS = "timestamp";
    public static final String KEY_PWB_COMPLETED = "completed";
    public static final String KEY_PWB_NOTIFIED = "notified";
    public static final String KEY_PWB_Q1 = "question1";
    public static final String KEY_PWB_Q2 = "question2";
    public static final String KEY_PWB_Q3 = "question3";
    public static final String KEY_PWB_Q4 = "question4";
    public static final String KEY_PWB_Q5 = "question5";
    public static final String KEY_PWB_Q6 = "question6";
    public static final String KEY_PWB_Q7 = "question7";
    public static final String KEY_PWB_Q8 = "question8";


    public static String getCreateQuery() {
        return "CREATE TABLE " + TABLE_PWB +
                "(" +
                KEY_PWB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_PWB_TS + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_PWB_SCHEDULED_AT + " INTEGER," +
                KEY_PWB_COMPLETED + " INTEGER, " +
                KEY_PWB_NOTIFIED + " INTEGER, " +
                KEY_PWB_Q1 + " INTEGER, " +
                KEY_PWB_Q2 + " INTEGER, " +
                KEY_PWB_Q3 + " INTEGER, " +
                KEY_PWB_Q4 + " INTEGER, " +
                KEY_PWB_Q5 + " INTEGER, " +
                KEY_PWB_Q6 + " INTEGER, " +
                KEY_PWB_Q7 + " INTEGER, " +
                KEY_PWB_Q8 + " INTEGER" +
                ")";
    }

    public static String[] getColumns() {
        String[] columns = {KEY_PWB_ID, KEY_PWB_TS, KEY_PWB_SCHEDULED_AT, KEY_PWB_COMPLETED, KEY_PWB_NOTIFIED, KEY_PWB_Q1, KEY_PWB_Q2, KEY_PWB_Q3, KEY_PWB_Q4, KEY_PWB_Q5, KEY_PWB_Q6, KEY_PWB_Q7, KEY_PWB_Q8};
        return columns;
    }

}
