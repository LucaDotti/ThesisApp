package usi.memotion.local.database.tables;


/**
 * Created by usi on 07/03/17.
 */
public class PSSTable {
    public static final String TABLE_PSS = "pss";
    public static final String KEY_PSS_ID = "id_pss";
    public static final String KEY_PSS_PARENT_SURVEY_ID = "parent_survey_id";
    public static final String KEY_PSS_COMPLETED = "completed";
    public static final String KEY_PSS_Q1 = "question1";
    public static final String KEY_PSS_Q2 = "question2";
    public static final String KEY_PSS_Q3 = "question3";
    public static final String KEY_PSS_Q4 = "question4";
    public static final String KEY_PSS_Q5 = "question5";
    public static final String KEY_PSS_Q6 = "question6";
    public static final String KEY_PSS_Q7 = "question7";
    public static final String KEY_PSS_Q8 = "question8";
    public static final String KEY_PSS_Q9 = "question9";
    public static final String KEY_PSS_Q10 = "question10";



    public static String getCreateQuery() {
        return "CREATE TABLE " + TABLE_PSS +
                "(" +
                KEY_PSS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_PSS_PARENT_SURVEY_ID + " INTEGER NULL, " +
                KEY_PSS_COMPLETED + " INTEGER," +
                KEY_PSS_Q1 + " INTEGER, " +
                KEY_PSS_Q2 + " INTEGER, " +
                KEY_PSS_Q3 + " INTEGER, " +
                KEY_PSS_Q4 + " INTEGER, " +
                KEY_PSS_Q5 + " INTEGER, " +
                KEY_PSS_Q6 + " INTEGER, " +
                KEY_PSS_Q7 + " INTEGER, " +
                KEY_PSS_Q8 + " INTEGER," +
                KEY_PSS_Q9 + " INTEGER," +
                KEY_PSS_Q10 + " INTEGER, " +
                "FOREIGN KEY (" + KEY_PSS_PARENT_SURVEY_ID + ") REFERENCES " + SurveyTable.TABLE_SURVEY + "(" + SurveyTable.KEY_SURVEY_ID + "));" +
                ")";
    }

    public static String[] getColumns() {
        String[] columns = {
                KEY_PSS_ID,
                KEY_PSS_PARENT_SURVEY_ID,
                KEY_PSS_COMPLETED,
                KEY_PSS_Q1,
                KEY_PSS_Q2,
                KEY_PSS_Q3,
                KEY_PSS_Q4,
                KEY_PSS_Q5,
                KEY_PSS_Q6,
                KEY_PSS_Q7,
                KEY_PSS_Q8,
                KEY_PSS_Q9,
                KEY_PSS_Q10
        };
        return columns;
    }
}
