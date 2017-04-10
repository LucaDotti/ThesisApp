package usi.justmove.local.database.tables;

import static usi.justmove.local.database.tables.SurveyAlarmsTable.KEY_SURVEY_ALARM_CURRENT;
import static usi.justmove.local.database.tables.SurveyAlarmsTable.KEY_SURVEY_ALARM_ID;
import static usi.justmove.local.database.tables.SurveyAlarmsTable.KEY_SURVEY_ALARM_SURVEY_TYPE;
import static usi.justmove.local.database.tables.SurveyAlarmsTable.KEY_SURVEY_ALARM_TS;
import static usi.justmove.local.database.tables.SurveyAlarmsTable.TABLE_SURVEY_ALARM;

/**
 * Created by usi on 09/04/17.
 */

public class SurveyAlarmSurveyTable {
    public static final String TABLE_SURVEY_ALARMS_SURVEY_TABLE = "surveyAlarms_survey";
    public static final String KEY_ID_SURVEY_ALARMS_SURVEY = "id_surveyAlarms_survey";
    public static final String KEY_SURVEY_ALARMS_SURVEY_SURVEY_ID = "survey_id";
    public static final String KEY_SURVEY_ALARMS_SURVEY_SURVEY_ALARMS_ID = "survey_alarms_id";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SURVEY_ALARMS_SURVEY_TABLE +
                "(" +
                KEY_ID_SURVEY_ALARMS_SURVEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SURVEY_ALARMS_SURVEY_SURVEY_ID + " INTEGER, " +
                KEY_SURVEY_ALARMS_SURVEY_SURVEY_ALARMS_ID + " INTEGER, " +
                "FOREIGN KEY (" + KEY_SURVEY_ALARMS_SURVEY_SURVEY_ID + ") REFERENCES " + SurveyTable.TABLE_SURVEY + " (" + SurveyTable.KEY_SURVEY_ID + "), " +
                "FOREIGN KEY (" + KEY_SURVEY_ALARMS_SURVEY_SURVEY_ALARMS_ID + ") REFERENCES " + SurveyAlarmsTable.TABLE_SURVEY_ALARM + " (" + SurveyAlarmsTable.KEY_SURVEY_ALARM_ID + ")"
                + ")";
    }

    public static String[] getColumns() {
        String[] columns = {
                KEY_ID_SURVEY_ALARMS_SURVEY,
                KEY_SURVEY_ALARMS_SURVEY_SURVEY_ID,
                KEY_SURVEY_ALARMS_SURVEY_SURVEY_ALARMS_ID
        };

        return columns;
    }
}
