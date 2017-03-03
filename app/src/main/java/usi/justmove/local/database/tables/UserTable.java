package usi.justmove.local.database.tables;

/**
 * Created by usi on 04/02/17.
 */
public class UserTable {
    public static final String TABLE_USER = "user";
    public static final String KEY_USER_ID = "id_user";
    public static final String KEY_USER_UID = "uid_user";
    public static final String KEY_USER_AGREED = "agreed";
    public static final String KEY_USER_AGE = "age";
    public static final String KEY_USER_GENDER = "gender";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_FACULTY = "faculty";
    public static final String KEY_USER_ACADEMIC_STATUS = "academic_status";
    public static final String KEY_USER_UPDATE_TS = "update_timestamp";
    public static final String KEY_USER_CREATION_TS = "creation_timestamp";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_USER +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_USER_UID + " INTEGER, " +
                KEY_USER_AGREED + " INTEGER," +
                KEY_USER_AGE + " INTEGER," +
                KEY_USER_GENDER + " TEXT," +
                KEY_USER_EMAIL + " TEXT," +
                KEY_USER_FACULTY + " TEXT," +
                KEY_USER_ACADEMIC_STATUS + " TEXT, " +
                KEY_USER_CREATION_TS + " INTEGER DEFAULT CURRENT_TIMESTAMP, " +
                KEY_USER_UPDATE_TS + " INTEGER DEFAULT CURRENT_TIMESTAMP)";
    }

    public static String[] getColumns() {
        String[] columns = {KEY_USER_ID, KEY_USER_UID, KEY_USER_AGREED, KEY_USER_AGE, KEY_USER_GENDER, KEY_USER_EMAIL, KEY_USER_FACULTY, KEY_USER_ACADEMIC_STATUS};
        return columns;
    }
}
