package usi.memotion.local.database.tables;

/**
 * Created by usi on 07/03/17.
 */
public class SWLSTable {
    public static final String TABLE_SWLS = "swls";
    public static final String KEY_SWLS_ID = "id_swls";
    public static final String KEY_SWLS_PARENT_SURVEY_ID = "parent_survey_id";
    public static final String KEY_SWLS_COMPLETED = "completed";
    public static final String KEY_SWLS_Q1 = "question1";
    public static final String KEY_SWLS_Q2 = "question2";
    public static final String KEY_SWLS_Q3 = "question3";
    public static final String KEY_SWLS_Q4 = "question4";
    public static final String KEY_SWLS_Q5 = "question5";

    public static String getCreateQuery() {
        return "CREATE TABLE " + TABLE_SWLS +
                "(" +
                KEY_SWLS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SWLS_PARENT_SURVEY_ID + " INTEGER NULL, " +
                KEY_SWLS_COMPLETED + " INTEGER, " +
                KEY_SWLS_Q1 + " INTEGER, " +
                KEY_SWLS_Q2 + " INTEGER, " +
                KEY_SWLS_Q3 + " INTEGER, " +
                KEY_SWLS_Q4 + " INTEGER, " +
                KEY_SWLS_Q5 + " INTEGER, " +
                "FOREIGN KEY (" + KEY_SWLS_PARENT_SURVEY_ID + ") REFERENCES " + SurveyTable.TABLE_SURVEY + "(" + SurveyTable.KEY_SURVEY_ID + "));";

    }

    public static String[] getColumns() {
        String[] columns = {
                KEY_SWLS_ID,
                KEY_SWLS_PARENT_SURVEY_ID,
                KEY_SWLS_COMPLETED,
                KEY_SWLS_Q1,
                KEY_SWLS_Q2,
                KEY_SWLS_Q3,
                KEY_SWLS_Q4,
                KEY_SWLS_Q5
        };
        return columns;
    }
}
