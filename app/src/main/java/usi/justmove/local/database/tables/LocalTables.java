package usi.justmove.local.database.tables;

import static usi.justmove.local.database.tables.PAMTable.TABLE_PAM;

/**
 * Created by usi on 18/01/17.
 */

public enum LocalTables {
    TABLE_ACCELEROMETER(AccelerometerTable.class),
    TABLE_BLUETOOTH(BlueToothTable.class),
    TABLE_LOCATION(LocationTable.class),
    TABLE_CALL_LOG(PhoneCallLogTable.class),
    TABLE_PHONELOCK(PhoneLockTable.class),
    TABLE_SMS(SMSTable.class),
    TABLE_USED_APP(UsedAppTable.class),
    TABLE_WIFI(WiFiTable.class),
    TABLE_PAM(PAMTable.class),
    TABLE_PWB(PWBTable.class),
    TABLE_SIMPLE_MOOD(SimpleMoodTable.class);


    LocalTables(Class a) {
    }
}
