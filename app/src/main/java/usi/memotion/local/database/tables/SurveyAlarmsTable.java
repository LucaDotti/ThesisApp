package usi.memotion.local.database.tables;

/**
 * Created by usi on 30/03/17.
 */

public class SurveyAlarmsTable {
    public static final String TABLE_SURVEY_ALARM = "survey_alarm";
    public static final String KEY_SURVEY_ALARM_ID = "id_survey_alarm";
    public static final String KEY_SURVEY_ALARM_CURRENT = "current";
    public static final String KEY_SURVEY_ALARM_TS = "ts";
    public static final String KEY_SURVEY_ALARM_SURVEY_TYPE = "type";


    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SURVEY_ALARM +
                "(" +
                KEY_SURVEY_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SURVEY_ALARM_CURRENT + " INTEGER, " +
                KEY_SURVEY_ALARM_TS + " INTEGER, " +
                KEY_SURVEY_ALARM_SURVEY_TYPE + " TEXT"
                + ")";

    }

    public static String[] getColumns() {
        String[] columns = {
            KEY_SURVEY_ALARM_ID,
            KEY_SURVEY_ALARM_CURRENT,
            KEY_SURVEY_ALARM_TS,
            KEY_SURVEY_ALARM_SURVEY_TYPE
        };

        return columns;
    }

}
