package usi.justmove.local.database.tables;

import static android.provider.Contacts.SettingsColumns.KEY;

/**
 * Created by usi on 07/02/17.
 */
public class PAMTable {
    public static final String TABLE_PAM = "pam";
    public static final String KEY_PAM_ID = "id_pam";
    public static final String KEY_PAM_SCHEDULED_AT = "scheduled_at";
    public static final String KEY_PAM_TS = "timestamp";
    public static final String KEY_PAM_COMPLETED = "completed";
    public static final String KEY_PAM_STRESS = "q_stress";
    public static final String KEY_PAM_SLEEP = "q_sleep";
    public static final String KEY_PAM_LOCATION = "q_location";
    public static final String KEY_PAM_TRANSPORTATION = "q_transportation";
    public static final String KEY_PAM_ACTIVITIES = "q_activities";
    public static final String KEY_PAM_WORKLOAD = "q_workload";
    public static final String KEY_PAM_SOCIAL = "q_social";
    public static final String KEY_PAM_IMAGE_ID = "image_id";
    public static final String KEY_PAM_PERIOD = "period";
    public static final String KEY_PAM_NOTIFED = "notified";

    public static String getCreateQuery() {
        return "CREATE TABLE " + TABLE_PAM +
                "(" +
                KEY_PAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_PAM_TS + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_PAM_SCHEDULED_AT + " INTEGER, " +
                KEY_PAM_COMPLETED + " INTEGER, " +
                KEY_PAM_STRESS + " INTEGER, " +
                KEY_PAM_SLEEP + " FLOAT, " +
                KEY_PAM_LOCATION + " TEXT, " +
                KEY_PAM_TRANSPORTATION + " TEXT, " +
                KEY_PAM_ACTIVITIES + " FLOAT, " +
                KEY_PAM_WORKLOAD + " FLOAT, " +
                KEY_PAM_SOCIAL + " INTEGER, " +
                KEY_PAM_IMAGE_ID + " INTEGER, " +
                KEY_PAM_PERIOD + " TEXT, " +
                KEY_PAM_NOTIFED + " INTEGER" +
                ")";
    }

    public static String[] getColumns() {
        String[] columns = {KEY_PAM_ID, KEY_PAM_TS, KEY_PAM_SCHEDULED_AT, KEY_PAM_COMPLETED,
                KEY_PAM_STRESS, KEY_PAM_SLEEP, KEY_PAM_LOCATION, KEY_PAM_TRANSPORTATION, KEY_PAM_ACTIVITIES,
                KEY_PAM_WORKLOAD, KEY_PAM_SOCIAL, KEY_PAM_IMAGE_ID, KEY_PAM_PERIOD, KEY_PAM_NOTIFED};
        return columns;
    }
}
