package usi.justmove.local.database;

import usi.justmove.local.database.tables.AccelerometerTable;
import usi.justmove.local.database.tables.BlueToothTable;
import usi.justmove.local.database.tables.LocationTable;
import usi.justmove.local.database.tables.PAMTable;
import usi.justmove.local.database.tables.PHQ8Table;
import usi.justmove.local.database.tables.PSSTable;
import usi.justmove.local.database.tables.PWBTable;
import usi.justmove.local.database.tables.PhoneCallLogTable;
import usi.justmove.local.database.tables.PhoneLockTable;
import usi.justmove.local.database.tables.SHSTable;
import usi.justmove.local.database.tables.SMSTable;
import usi.justmove.local.database.tables.SWLSTable;
import usi.justmove.local.database.tables.SimpleMoodTable;
import usi.justmove.local.database.tables.SurveyAlarmsTable;
import usi.justmove.local.database.tables.SurveyTable;
import usi.justmove.local.database.tables.UsedAppTable;
import usi.justmove.local.database.tables.WiFiTable;

/**
 * Created by usi on 18/01/17.
 */

public class LocalDbUtility {
    private final static int DATA_TABLES_COUNT = 10;

    public static int getDataTablesCount() {
        return DATA_TABLES_COUNT;
    }

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
            default:
                return null;
        }
    }
}
