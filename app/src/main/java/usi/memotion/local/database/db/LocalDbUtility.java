package usi.memotion.local.database.db;

import usi.memotion.local.database.tables.AccelerometerTable;
import usi.memotion.local.database.tables.BlueToothTable;
import usi.memotion.local.database.tables.LocationTable;
import usi.memotion.local.database.tables.PAMTable;
import usi.memotion.local.database.tables.PHQ8Table;
import usi.memotion.local.database.tables.PSSTable;
import usi.memotion.local.database.tables.PWBTable;
import usi.memotion.local.database.tables.PhoneCallLogTable;
import usi.memotion.local.database.tables.PhoneLockTable;
import usi.memotion.local.database.tables.SHSTable;
import usi.memotion.local.database.tables.SMSTable;
import usi.memotion.local.database.tables.SWLSTable;
import usi.memotion.local.database.tables.SimpleMoodTable;
import usi.memotion.local.database.tables.SurveyAlarmSurveyTable;
import usi.memotion.local.database.tables.SurveyAlarmsTable;
import usi.memotion.local.database.tables.SurveyConfigTable;
import usi.memotion.local.database.tables.SurveyTable;
import usi.memotion.local.database.tables.UsedAppTable;
import usi.memotion.local.database.tables.WiFiTable;

/**
 * Created by usi on 18/01/17.
 */

public class LocalDbUtility {
    private final static int DATA_TABLES_COUNT = 10;


    public static String getTableName(LocalTables table) {
        switch (table) {
            case TABLE_ACCELEROMETER:
                return AccelerometerTable.TABLE_ACCELEROMETER;
            case TABLE_BLUETOOTH:
                return BlueToothTable.TABLE_BLUETOOTH;
            case TABLE_CALL_LOG:
                return PhoneCallLogTable.TABLE_CALL_LOG;
            case TABLE_LOCATION:
                return LocationTable.TABLE_LOCATION;
            case TABLE_PHONELOCK:
                return PhoneLockTable.TABLE_PHONELOCK;
            case TABLE_SMS:
                return SMSTable.TABLE_SMS;
            case TABLE_USED_APP:
                return UsedAppTable.TABLE_USED_APP;
            case TABLE_WIFI:
                return WiFiTable.TABLE_WIFI;
            case TABLE_PAM:
                return PAMTable.TABLE_PAM;
            case TABLE_PWB:
                return PWBTable.TABLE_PWB;
            case TABLE_SIMPLE_MOOD:
                return SimpleMoodTable.TABLE_SIMPLE_MOOD;
            case TABLE_PSS:
                return PSSTable.TABLE_PSS;
            case TABLE_PHQ8:
                return PHQ8Table.TABLE_PHQ8;
            case TABLE_SHS:
                return SHSTable.TABLE_SHS;
            case TABLE_SWLS:
                return SWLSTable.TABLE_SWLS;
            case TABLE_SURVEY:
                return SurveyTable.TABLE_SURVEY;
            case TABLE_SURVEY_ALARMS:
                return SurveyAlarmsTable.TABLE_SURVEY_ALARM;
            case TABLE_SURVEY_ALARMS_SURVEY:
                return SurveyAlarmSurveyTable.TABLE_SURVEY_ALARMS_SURVEY_TABLE;
            case TABLE_SURVEY_CONFIG:
                return SurveyConfigTable.TABLE_SURVEY_CONFIG;
            default:
                return null;
        }
    }

    public static String[] getTableColumns(LocalTables table) {
        switch (table) {
            case TABLE_ACCELEROMETER:
                return AccelerometerTable.getColumns();
            case TABLE_BLUETOOTH:
                return BlueToothTable.getColumns();
            case TABLE_CALL_LOG:
                return PhoneCallLogTable.getColumns();
            case TABLE_LOCATION:
                return LocationTable.getColumns();
            case TABLE_PHONELOCK:
                return PhoneLockTable.getColumns();
            case TABLE_SMS:
                return SMSTable.getColumns();
            case TABLE_USED_APP:
                return UsedAppTable.getColumns();
            case TABLE_WIFI:
                return WiFiTable.getColumns();
            case TABLE_PAM:
                return PAMTable.getColumns();
            case TABLE_PWB:
                return PWBTable.getColumns();
            case TABLE_SIMPLE_MOOD:
                return SimpleMoodTable.getColumns();
            case TABLE_PSS:
                return PSSTable.getColumns();
            case TABLE_PHQ8:
                return PHQ8Table.getColumns();
            case TABLE_SHS:
                return SHSTable.getColumns();
            case TABLE_SWLS:
                return SWLSTable.getColumns();
            case TABLE_SURVEY:
                return SurveyTable.getColumns();
            case TABLE_SURVEY_ALARMS:
                return SurveyAlarmsTable.getColumns();
            case TABLE_SURVEY_ALARMS_SURVEY:
                return SurveyAlarmSurveyTable.getColumns();
            case TABLE_SURVEY_CONFIG:
                return SurveyConfigTable.getColumns();
            default:
                return null;
        }
    }
}
