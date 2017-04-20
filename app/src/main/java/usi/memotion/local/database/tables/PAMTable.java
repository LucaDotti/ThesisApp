package usi.memotion.local.database.tables;

/**
 * Created by usi on 07/02/17.
 */
public class PAMTable {
    public static final String TABLE_PAM = "pam";
    public static final String KEY_PAM_ID = "id_pam";
    public static final String KEY_PAM_PARENT_SURVEY_ID = "parent_survey_id";
    public static final String KEY_PAM_COMPLETED = "completed";
    public static final String KEY_PAM_PERIOD = "period";
    public static final String KEY_PAM_IMAGE_ID = "image_id";
    public static final String KEY_PAM_STRESS = "q_stress";
    public static final String KEY_PAM_SLEEP = "q_sleep";
    public static final String KEY_PAM_LOCATION = "q_location";
    public static final String KEY_PAM_TRANSPORTATION = "q_transportation";
    public static final String KEY_PAM_ACTIVITIES = "q_activities";
    public static final String KEY_PAM_WORKLOAD = "q_workload";
    public static final String KEY_PAM_SOCIAL = "q_social";

    public static String getCreateQuery() {
        return "CREATE TABLE " + TABLE_PAM +
                "(" +
                    KEY_PAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_PAM_PARENT_SURVEY_ID + " INTEGER NULL, " +
                    KEY_PAM_COMPLETED + " INTEGER, " +
                    KEY_PAM_PERIOD + " TEXT, " +
                    KEY_PAM_IMAGE_ID + " INTEGER, " +
                    KEY_PAM_STRESS + " INTEGER, " +
                    KEY_PAM_SLEEP + " TEXT, " +
                    KEY_PAM_LOCATION + " TEXT, " +
                    KEY_PAM_TRANSPORTATION + " TEXT, " +
                    KEY_PAM_ACTIVITIES + " TEXT, " +
                    KEY_PAM_WORKLOAD + " TEXT, " +
                    KEY_PAM_SOCIAL + " INTEGER, " +
                    "FOREIGN KEY (" + KEY_PAM_PARENT_SURVEY_ID + ") REFERENCES " + SurveyTable.TABLE_SURVEY + "(" + SurveyTable.KEY_SURVEY_ID + "));" +
                ")";
    }

    public static String[] getColumns() {
        String[] columns = {
                KEY_PAM_ID,
                KEY_PAM_PARENT_SURVEY_ID,
                KEY_PAM_COMPLETED,
                KEY_PAM_PERIOD,
                KEY_PAM_IMAGE_ID,
                KEY_PAM_STRESS,
                KEY_PAM_SLEEP,
                KEY_PAM_LOCATION,
                KEY_PAM_TRANSPORTATION,
                KEY_PAM_ACTIVITIES,
                KEY_PAM_WORKLOAD,
                KEY_PAM_SOCIAL
        };
        return columns;
    }
}
