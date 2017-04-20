package usi.memotion.local.database.tables;


/**
 * Created by usi on 10/03/17.
 */

public class SurveyTable {
    public static final String TABLE_SURVEY = "survey";
    public static final String KEY_SURVEY_ID = "id_survey";
    public static final String KEY_SURVEY_TS = "timestamp";
    public static final String KEY_SURVEY_SCHEDULED_AT = "scheduled_at";
    public static final String KEY_SURVEY_COMPLETED = "completed";
    public static final String KEY_SURVEY_NOTIFIED = "notified";
    public static final String KEY_SURVEY_EXPIRED = "expired";
    public static final String KEY_SURVEY_GROUPED = "grouped";
    public static final String KEY_SURVEY_TYPE = "type";

    public static String getCreateQuery() {
        return "CREATE TABLE " + TABLE_SURVEY +
                "(" +
                KEY_SURVEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SURVEY_TS + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_SURVEY_SCHEDULED_AT + " INTEGER, " +
                KEY_SURVEY_COMPLETED + " INTEGER, " +
                KEY_SURVEY_NOTIFIED + " INTEGER, " +
                KEY_SURVEY_EXPIRED + " INTEGER, " +
                KEY_SURVEY_GROUPED + " INTEGER, " +
                KEY_SURVEY_TYPE + " TEXT"
                + ")";
    }

    public static String[] getColumns() {
        String[] columns = {
                KEY_SURVEY_ID,
                KEY_SURVEY_TS,
                KEY_SURVEY_SCHEDULED_AT,
                KEY_SURVEY_COMPLETED,
                KEY_SURVEY_NOTIFIED,
                KEY_SURVEY_EXPIRED,
                KEY_SURVEY_GROUPED,
                KEY_SURVEY_TYPE
        };
        return columns;
    }
}
