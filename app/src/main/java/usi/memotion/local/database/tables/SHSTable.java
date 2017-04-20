package usi.memotion.local.database.tables;


import static usi.memotion.local.database.tables.PSSTable.KEY_PSS_COMPLETED;

/**
 * Created by usi on 07/03/17.
 */
public class SHSTable {
    public static final String TABLE_SHS = "shs";
    public static final String KEY_SHS_ID = "id_shs";
    public static final String KEY_SHS_PARENT_SURVEY_ID = "parent_survey_id";
    public static final String KEY_SHS_COMPLETED = "completed";
    public static final String KEY_SHS_Q1 = "question1";
    public static final String KEY_SHS_Q2 = "question2";
    public static final String KEY_SHS_Q3 = "question3";
    public static final String KEY_SHS_Q4 = "question4";

    public static String getCreateQuery() {
        return "CREATE TABLE " + TABLE_SHS +
                "(" +
                KEY_SHS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SHS_PARENT_SURVEY_ID + " INTEGER NULL, " +
                KEY_PSS_COMPLETED + " INTEGER," +
                KEY_SHS_Q1 + " INTEGER, " +
                KEY_SHS_Q2 + " INTEGER, " +
                KEY_SHS_Q3 + " INTEGER, " +
                KEY_SHS_Q4 + " INTEGER, " +
                "FOREIGN KEY (" + KEY_SHS_PARENT_SURVEY_ID + ") REFERENCES " + SurveyTable.TABLE_SURVEY + "(" + SurveyTable.KEY_SURVEY_ID + ")" +
                ")";
    }

    public static String[] getColumns() {
        String[] columns = {
                KEY_SHS_ID,
                KEY_SHS_PARENT_SURVEY_ID,
                KEY_SHS_COMPLETED,
                KEY_SHS_Q1,
                KEY_SHS_Q2,
                KEY_SHS_Q3,
                KEY_SHS_Q4};
        return columns;
    }
}
