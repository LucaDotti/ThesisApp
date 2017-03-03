package usi.justmove.local.database.tables;

import static android.R.attr.data;

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
            default:
                return null;
        }
    }

}
